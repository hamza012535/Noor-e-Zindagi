const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_user_01";
const BOB_UID = "bob_user_02";
const ADMIN_EMAIL = "hamzaanxari0786@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: denied all reads and writes", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb
      .collection("communityContent")
      .where("status", "==", "approved")
      .get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("checklist")
      .doc("2026-11-19")
      .set({
        userId: ALICE_UID,
        dateKey: "2026-11-19",
        completedIds: ["fajr"],
        missedPrayerIds: [],
        fastingCompleted: false,
        totalItems: 10,
        completionPercent: 10,
        createdAt: new Date(Date.now() - 1000),
        updatedAt: new Date(Date.now() - 1000),
      })
  );
});

test("User profile and subcollections: owner CRUD succeeds, cross-user fails", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Amina",
      email: "amina@example.com",
      preferredLanguage: "en",
      arabicFontScale: 1.1,
      translationFontScale: 1.0,
      darkReadingMode: false,
      calculationMethod: 1,
      latitude: 24.8607,
      longitude: 67.0011,
      locationName: "Karachi",
      missedPrayersEnabled: true,
      currentStreak: 3,
      lastActiveDate: "2026-11-19",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Partial update on profile succeeds; tampering createdAt fails
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      preferredLanguage: "ur",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Cross-user read/write fails
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({ displayName: "Hijack" })
  );

  // Checklist subcollection
  const checklistRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("checklist")
    .doc("2026-11-19");
  await assertSucceeds(
    checklistRef.set({
      userId: ALICE_UID,
      dateKey: "2026-11-19",
      completedIds: ["waking_dua", "fajr_salah"],
      missedPrayerIds: [],
      fastingCompleted: true,
      totalItems: 12,
      completionPercent: 17,
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("checklist").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("checklist").get()
  );

  // Bookmarks subcollection (no @DocumentId id key in map)
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("bookmarks")
      .doc("quran_2_255")
      .set({
        userId: ALICE_UID,
        itemType: "quran",
        refId: "2:255",
        title: "Ayat-ul-Kursi",
        arabicText: "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ",
        subtitle: "Al-Baqarah 2:255",
        createdAt: now,
        updatedAt: now,
      })
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("bookmarks")
      .doc("quran_bad")
      .set({
        id: "quran_bad",
        userId: ALICE_UID,
        itemType: "quran",
        refId: "1:1",
        title: "Al-Fatihah",
        createdAt: now,
        updatedAt: now,
      })
  );
});

test("Community content & Admin approval workflow: enforces human verification and email_verified", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const spoofedAdminDb = testEnv
    .authenticatedContext("spoof_uid", {
      email: ADMIN_EMAIL,
      email_verified: false,
    })
    .firestore();
  const verifiedAdminDb = testEnv
    .authenticatedContext("admin_uid", {
      email: ADMIN_EMAIL,
      email_verified: true,
    })
    .firestore();

  const now = new Date(Date.now() - 2000);

  // Regular user cannot self-publish as approved
  await assertFails(
    aliceDb.collection("communityContent").doc("ocr_1").set({
      authorId: ALICE_UID,
      contentType: "ocr_poster",
      category: "Duas",
      title: "Morning Dua Poster",
      referenceSource: "User Upload - Pending Review",
      ocrExtractedText: "أصبحنا وأصبح الملك لله",
      status: "approved",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Regular user can submit pending OCR draft
  await assertSucceeds(
    aliceDb.collection("communityContent").doc("ocr_1").set({
      authorId: ALICE_UID,
      contentType: "ocr_poster",
      category: "Duas",
      title: "Morning Dua Poster",
      referenceSource: "Hisn al-Muslim #76",
      ocrExtractedText: "أصبحنا وأصبح الملك لله",
      status: "pending",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Unverified email spoofing admin fails to approve
  await assertFails(
    spoofedAdminDb.collection("communityContent").doc("ocr_1").update({
      status: "approved",
      verifiedBy: ADMIN_EMAIL,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Regular user unfiltered list fails; filtered approved/own list succeeds
  await assertFails(aliceDb.collection("communityContent").get());
  await assertSucceeds(
    aliceDb
      .collection("communityContent")
      .where("status", "==", "approved")
      .get()
  );
  await assertSucceeds(
    aliceDb
      .collection("communityContent")
      .where("authorId", "==", ALICE_UID)
      .get()
  );

  // Verified admin can list all and approve OCR submission
  await assertSucceeds(verifiedAdminDb.collection("communityContent").get());
  await assertSucceeds(
    verifiedAdminDb.collection("communityContent").doc("ocr_1").update({
      status: "approved",
      arabicText: "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ",
      verifiedBy: ADMIN_EMAIL,
      verificationNotes: "Verified against Hisn al-Muslim #76",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});
