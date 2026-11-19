package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.local.LocalBookmarkEntity
import com.example.data.local.LocalChecklistEntity
import com.example.data.local.LocalCustomActivityEntity
import com.example.data.local.LocalOcrSubmissionEntity
import com.example.data.local.LocalReminderEntity
import com.example.data.local.NoorDao
import com.example.data.local.UserPreferencesState
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.example.model.BookmarkDoc
import com.example.model.ChecklistDayDoc
import com.example.model.CommunityContentDoc
import com.example.model.CustomActivityDoc
import com.example.model.ReminderConfigDoc
import com.example.model.UserProfileDoc
import com.example.notifications.ReminderScheduler
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class UserSyncRepository(
    private val dbOrNull: FirebaseFirestore?,
    private val noorDao: NoorDao,
    private val authOrNull: FirebaseAuth? = null
) {
    constructor(context: Context, noorDao: NoorDao) : this(
        dbOrNull = if (FirebaseApp.getApps(context.applicationContext).isNotEmpty()) {
            FirebaseFirestore.getInstance(
                context.applicationContext.getString(R.string.firestore_database_id)
            )
        } else null,
        noorDao = noorDao,
        authOrNull = if (FirebaseApp.getApps(context.applicationContext).isNotEmpty()) {
            Firebase.auth
        } else null
    )

    private val db: FirebaseFirestore
        get() = dbOrNull ?: throw IllegalStateException("Cloud sync requires Firebase configuration.")

    private val auth: FirebaseAuth
        get() = authOrNull ?: throw IllegalStateException("Firebase Auth requires Firebase configuration.")

    companion object {
        const val ADMIN_EMAIL = "hamzaanxari0786@gmail.com"
    }

    fun isCurrentUserAdmin(): Boolean {
        val user = authOrNull?.currentUser ?: return false
        return user.email.equals(ADMIN_EMAIL, ignoreCase = true) && user.isEmailVerified
    }

    private fun requireUserId(): String {
        return authOrNull?.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    private fun safeDocId(raw: String): String =
        raw.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120).ifBlank { UUID.randomUUID().toString() }

    suspend fun ensureUserProfile(
        userId: String,
        displayName: String,
        email: String,
        prefs: UserPreferencesState
    ): Result<Unit> {
        val path = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch" }
            val ref = db.collection("users").document(uid)
            val snap = ref.get().await()
            if (!snap.exists()) {
                val doc = UserProfileDoc(
                    userId = uid,
                    displayName = displayName.ifBlank { "Believer" },
                    email = email,
                    preferredLanguage = prefs.language.code,
                    arabicFontScale = prefs.arabicFontScale.toDouble(),
                    translationFontScale = prefs.translationFontScale.toDouble(),
                    darkReadingMode = prefs.darkReadingMode,
                    calculationMethod = prefs.calculationMethodId.toLong(),
                    latitude = prefs.latitude,
                    longitude = prefs.longitude,
                    locationName = prefs.locationName,
                    missedPrayersEnabled = prefs.missedPrayersEnabled,
                    currentStreak = 0L,
                    lastActiveDate = ""
                )
                ref.set(doc.toCreateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun syncPreferencesToCloud(userId: String?, prefs: UserPreferencesState, streak: Int, lastDate: String): Result<Unit> {
        if (userId.isNullOrBlank()) return Result.success(Unit)
        val path = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch" }
            val ref = db.collection("users").document(uid)
            val snap = ref.get().await()
            if (snap.exists()) {
                ref.update(
                    mapOf(
                        "preferredLanguage" to prefs.language.code,
                        "arabicFontScale" to prefs.arabicFontScale.toDouble().coerceIn(0.7, 2.5),
                        "translationFontScale" to prefs.translationFontScale.toDouble().coerceIn(0.7, 2.5),
                        "darkReadingMode" to prefs.darkReadingMode,
                        "calculationMethod" to prefs.calculationMethodId.toLong().coerceIn(0L, 25L),
                        "latitude" to prefs.latitude.coerceIn(-90.0, 90.0),
                        "longitude" to prefs.longitude.coerceIn(-180.0, 180.0),
                        "locationName" to prefs.locationName.take(120),
                        "missedPrayersEnabled" to prefs.missedPrayersEnabled,
                        "currentStreak" to streak.toLong().coerceAtLeast(0L),
                        "lastActiveDate" to lastDate.take(20),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // --- CHECKLIST ---
    fun observeChecklistHistory(userId: String?): Flow<List<LocalChecklistEntity>> {
        if (userId.isNullOrBlank()) {
            return noorDao.observeRecentChecklists()
        }
        val path = "users/$userId/checklist"
        return flow {
            val uid = requireUserId()
            emitAll(
                db.collection("users").document(uid).collection("checklist")
                    .snapshots()
                    .map { snapshot ->
                        val docs = snapshot.toObjects(
                            ChecklistDayDoc::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                        docs.map { doc ->
                            LocalChecklistEntity(
                                dateKey = doc.dateKey.ifBlank { doc.id },
                                completedIdsCsv = doc.completedIds.joinToString(","),
                                missedPrayerIdsCsv = doc.missedPrayerIds.joinToString(","),
                                fastingCompleted = doc.fastingCompleted,
                                totalItems = doc.totalItems.toInt().coerceAtLeast(1),
                                completionPercent = doc.completionPercent.toInt().coerceIn(0, 100),
                                updatedAtMillis = doc.updatedAt?.toDate()?.time ?: System.currentTimeMillis()
                            )
                        }.sortedByDescending { it.dateKey }
                    }
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        val ex = firestoreError ?: (error as? Exception)
                        if (ex != null && auth.currentUser?.uid == uid) {
                            handleFirestoreError(ex, OperationType.LIST, path)
                        }
                        throw error
                    }
            )
        }
    }

    suspend fun saveChecklistDay(
        userId: String?,
        dateKey: String,
        completedIds: Set<String>,
        missedPrayerIds: Set<String>,
        fastingCompleted: Boolean,
        totalItems: Int
    ): Result<Unit> {
        val safeTotal = totalItems.coerceIn(1, 120)
        val pct = ((completedIds.size * 100) / safeTotal).coerceIn(0, 100)
        val local = LocalChecklistEntity(
            dateKey = dateKey,
            completedIdsCsv = completedIds.joinToString(","),
            missedPrayerIdsCsv = missedPrayerIds.joinToString(","),
            fastingCompleted = fastingCompleted,
            totalItems = safeTotal,
            completionPercent = pct,
            updatedAtMillis = System.currentTimeMillis()
        )
        noorDao.upsertChecklist(local)

        if (userId.isNullOrBlank()) return Result.success(Unit)

        val safeDate = safeDocId(dateKey)
        val path = "users/$userId/checklist/$safeDate"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch" }
            val ref = db.collection("users").document(uid).collection("checklist").document(safeDate)
            val snap = ref.get().await()
            if (snap.exists()) {
                ref.update(
                    mapOf(
                        "completedIds" to completedIds.toList().take(120),
                        "missedPrayerIds" to missedPrayerIds.toList().take(20),
                        "fastingCompleted" to fastingCompleted,
                        "totalItems" to safeTotal.toLong(),
                        "completionPercent" to pct.toLong(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val doc = ChecklistDayDoc(
                    userId = uid,
                    dateKey = safeDate,
                    completedIds = completedIds.toList(),
                    missedPrayerIds = missedPrayerIds.toList(),
                    fastingCompleted = fastingCompleted,
                    totalItems = safeTotal.toLong(),
                    completionPercent = pct.toLong()
                )
                ref.set(doc.toCreateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // --- BOOKMARKS ---
    fun observeBookmarks(userId: String?): Flow<List<LocalBookmarkEntity>> {
        if (userId.isNullOrBlank()) {
            return noorDao.observeBookmarks()
        }
        val path = "users/$userId/bookmarks"
        return flow {
            val uid = requireUserId()
            emitAll(
                db.collection("users").document(uid).collection("bookmarks")
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObjects(
                            BookmarkDoc::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ).map { doc ->
                            LocalBookmarkEntity(
                                id = doc.id,
                                itemType = doc.itemType,
                                refId = doc.refId,
                                title = doc.title,
                                arabicText = doc.arabicText,
                                subtitle = doc.subtitle,
                                createdAtMillis = doc.createdAt?.toDate()?.time ?: System.currentTimeMillis()
                            )
                        }.sortedByDescending { it.createdAtMillis }
                    }
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        val ex = firestoreError ?: (error as? Exception)
                        if (ex != null && auth.currentUser?.uid == uid) {
                            handleFirestoreError(ex, OperationType.LIST, path)
                        }
                        throw error
                    }
            )
        }
    }

    suspend fun toggleBookmark(
        userId: String?,
        itemType: String,
        refId: String,
        title: String,
        arabicText: String,
        subtitle: String,
        currentlyBookmarked: Boolean
    ): Result<Unit> {
        val docId = safeDocId("${itemType}_${refId}")
        if (currentlyBookmarked) {
            noorDao.deleteBookmarkById(docId)
        } else {
            noorDao.upsertBookmark(
                LocalBookmarkEntity(
                    id = docId,
                    itemType = itemType,
                    refId = refId,
                    title = title,
                    arabicText = arabicText,
                    subtitle = subtitle,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
        }

        if (userId.isNullOrBlank()) return Result.success(Unit)

        val path = "users/$userId/bookmarks/$docId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch" }
            val ref = db.collection("users").document(uid).collection("bookmarks").document(docId)
            if (currentlyBookmarked) {
                ref.delete().await()
            } else {
                val doc = BookmarkDoc(
                    userId = uid,
                    itemType = itemType,
                    refId = refId,
                    title = title,
                    arabicText = arabicText,
                    subtitle = subtitle
                )
                ref.set(doc.toCreateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // --- CUSTOM ACTIVITIES ---
    fun observeCustomActivities(userId: String?): Flow<List<LocalCustomActivityEntity>> {
        if (userId.isNullOrBlank()) {
            return noorDao.observeCustomActivities()
        }
        val path = "users/$userId/customActivities"
        return flow {
            val uid = requireUserId()
            emitAll(
                db.collection("users").document(uid).collection("customActivities")
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObjects(
                            CustomActivityDoc::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ).map { doc ->
                            LocalCustomActivityEntity(
                                id = doc.id,
                                title = doc.title,
                                timeSlot = doc.timeSlot,
                                recommendedTime = doc.recommendedTime,
                                note = doc.note,
                                createdAtMillis = doc.createdAt?.toDate()?.time ?: System.currentTimeMillis()
                            )
                        }.sortedByDescending { it.createdAtMillis }
                    }
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        val ex = firestoreError ?: (error as? Exception)
                        if (ex != null && auth.currentUser?.uid == uid) {
                            handleFirestoreError(ex, OperationType.LIST, path)
                        }
                        throw error
                    }
            )
        }
    }

    suspend fun addCustomActivity(
        userId: String?,
        title: String,
        timeSlot: String,
        recommendedTime: String,
        note: String
    ): Result<Unit> {
        val cleanTitle = title.trim().take(120)
        if (cleanTitle.isBlank()) return Result.failure(IllegalArgumentException("Activity title cannot be blank."))
        val id = "custom_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        noorDao.upsertCustomActivity(
            LocalCustomActivityEntity(
                id = id,
                title = cleanTitle,
                timeSlot = timeSlot,
                recommendedTime = recommendedTime.take(40),
                note = note.take(400),
                createdAtMillis = System.currentTimeMillis()
            )
        )
        if (userId.isNullOrBlank()) return Result.success(Unit)
        val path = "users/$userId/customActivities/$id"
        return try {
            val uid = requireUserId()
            val doc = CustomActivityDoc(
                userId = uid,
                title = cleanTitle,
                timeSlot = timeSlot,
                recommendedTime = recommendedTime.take(40),
                note = note.take(400)
            )
            db.collection("users").document(uid).collection("customActivities")
                .document(id).set(doc.toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteCustomActivity(userId: String?, activityId: String): Result<Unit> {
        noorDao.deleteCustomActivityById(activityId)
        if (userId.isNullOrBlank()) return Result.success(Unit)
        val safeId = safeDocId(activityId)
        val path = "users/$userId/customActivities/$safeId"
        return try {
            val uid = requireUserId()
            db.collection("users").document(uid).collection("customActivities")
                .document(safeId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // --- REMINDERS ---
    suspend fun ensureDefaultRemindersSeeded() {
        if (noorDao.getAllReminders().isEmpty()) {
            noorDao.upsertReminders(ReminderScheduler.defaultReminders)
        }
    }

    fun observeReminders(userId: String?): Flow<List<LocalReminderEntity>> {
        if (userId.isNullOrBlank()) {
            return noorDao.observeReminders()
        }
        val path = "users/$userId/reminders"
        return flow {
            val uid = requireUserId()
            emitAll(
                db.collection("users").document(uid).collection("reminders")
                    .snapshots()
                    .map { snapshot ->
                        val remote = snapshot.toObjects(
                            ReminderConfigDoc::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ).map { doc ->
                            LocalReminderEntity(
                                id = doc.id,
                                category = doc.category,
                                title = doc.title,
                                hour = doc.hour.toInt().coerceIn(0, 23),
                                minute = doc.minute.toInt().coerceIn(0, 59),
                                enabled = doc.enabled,
                                frequency = doc.frequency,
                                soundType = doc.soundType
                            )
                        }
                        if (remote.isEmpty()) ReminderScheduler.defaultReminders else remote.sortedBy { it.hour * 60 + it.minute }
                    }
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        val ex = firestoreError ?: (error as? Exception)
                        if (ex != null && auth.currentUser?.uid == uid) {
                            handleFirestoreError(ex, OperationType.LIST, path)
                        }
                        throw error
                    }
            )
        }
    }

    suspend fun saveReminder(userId: String?, reminder: LocalReminderEntity): Result<Unit> {
        noorDao.upsertReminder(reminder)
        if (userId.isNullOrBlank()) return Result.success(Unit)
        val safeId = safeDocId(reminder.id)
        val path = "users/$userId/reminders/$safeId"
        return try {
            val uid = requireUserId()
            val ref = db.collection("users").document(uid).collection("reminders").document(safeId)
            val snap = ref.get().await()
            if (snap.exists()) {
                ref.update(
                    mapOf(
                        "title" to reminder.title.take(100),
                        "hour" to reminder.hour.toLong().coerceIn(0L, 23L),
                        "minute" to reminder.minute.toLong().coerceIn(0L, 59L),
                        "enabled" to reminder.enabled,
                        "frequency" to reminder.frequency,
                        "soundType" to reminder.soundType,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val doc = ReminderConfigDoc(
                    userId = uid,
                    category = reminder.category,
                    title = reminder.title,
                    hour = reminder.hour.toLong(),
                    minute = reminder.minute.toLong(),
                    enabled = reminder.enabled,
                    frequency = reminder.frequency,
                    soundType = reminder.soundType
                )
                ref.set(doc.toCreateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // --- COMMUNITY CONTENT, OCR SUBMISSIONS & ADMIN APPROVAL ---
    fun observeOcrSubmissions(): Flow<List<LocalOcrSubmissionEntity>> = noorDao.observeOcrSubmissions()

    fun observeApprovedCommunityContent(userId: String?): Flow<List<CommunityContentDoc>> {
        if (userId.isNullOrBlank()) return flow { emit(emptyList()) }
        val path = "communityContent"
        return flow {
            val uid = requireUserId()
            emitAll(
                db.collection("communityContent")
                    .whereEqualTo("status", "approved")
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObjects(
                            CommunityContentDoc::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    }
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        val ex = firestoreError ?: (error as? Exception)
                        if (ex != null && auth.currentUser?.uid == uid) {
                            handleFirestoreError(ex, OperationType.LIST, path)
                        }
                        throw error
                    }
            )
        }
    }

    fun observeAdminCommunityQueue(userId: String?): Flow<List<CommunityContentDoc>> {
        if (userId.isNullOrBlank() || !isCurrentUserAdmin()) return flow { emit(emptyList()) }
        val path = "communityContent"
        return flow {
            val uid = requireUserId()
            emitAll(
                db.collection("communityContent")
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObjects(
                            CommunityContentDoc::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ).sortedByDescending { it.updatedAt?.toDate()?.time ?: 0L }
                    }
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        val ex = firestoreError ?: (error as? Exception)
                        if (ex != null && auth.currentUser?.uid == uid) {
                            handleFirestoreError(ex, OperationType.LIST, path)
                        }
                        throw error
                    }
            )
        }
    }

    suspend fun submitPendingOcrContent(
        userId: String?,
        imageUri: String,
        title: String,
        category: String,
        contentType: String,
        ocrText: String,
        verificationSummary: String,
        referenceSource: String
    ): Result<Unit> {
        val id = "ocr_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        noorDao.upsertOcrSubmission(
            LocalOcrSubmissionEntity(
                id = id,
                imageUri = imageUri,
                title = title.take(200),
                category = category.take(80),
                contentType = contentType,
                ocrText = ocrText.take(4000),
                verificationSummary = verificationSummary,
                status = "pending",
                referenceSource = referenceSource.take(300),
                createdAtMillis = System.currentTimeMillis()
            )
        )
        if (userId.isNullOrBlank()) return Result.success(Unit)
        val path = "communityContent/$id"
        return try {
            val uid = requireUserId()
            val doc = CommunityContentDoc(
                authorId = uid,
                contentType = contentType,
                category = category,
                title = title,
                ocrExtractedText = ocrText,
                verificationNotes = verificationSummary,
                referenceSource = referenceSource.ifBlank { "Pending Scholarly Reference Verification" },
                status = "pending"
            )
            db.collection("communityContent").document(id).set(doc.toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminSaveOrReviewContent(
        contentId: String?,
        contentType: String,
        category: String,
        title: String,
        arabicText: String,
        romanUrdu: String,
        urduTranslation: String,
        englishTranslation: String,
        referenceSource: String,
        grading: String,
        verificationNotes: String,
        status: String
    ): Result<Unit> {
        val id = contentId?.takeIf { it.isNotBlank() }?.let(::safeDocId)
            ?: "adm_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val path = "communityContent/$id"
        return try {
            val uid = requireUserId()
            val verifier = auth.currentUser?.email ?: uid
            val ref = db.collection("communityContent").document(id)
            val snap = ref.get().await()
            if (snap.exists()) {
                ref.update(
                    mapOf(
                        "contentType" to contentType,
                        "category" to category.take(80),
                        "title" to title.take(200),
                        "arabicText" to arabicText.take(3000),
                        "romanUrdu" to romanUrdu.take(2000),
                        "urduTranslation" to urduTranslation.take(3000),
                        "englishTranslation" to englishTranslation.take(3000),
                        "referenceSource" to referenceSource.take(300).ifBlank { "Verified Reference" },
                        "grading" to grading.take(80),
                        "verificationNotes" to verificationNotes.take(1000),
                        "status" to status,
                        "verifiedBy" to verifier.take(120),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val doc = CommunityContentDoc(
                    authorId = uid,
                    contentType = contentType,
                    category = category,
                    title = title,
                    arabicText = arabicText,
                    romanUrdu = romanUrdu,
                    urduTranslation = urduTranslation,
                    englishTranslation = englishTranslation,
                    referenceSource = referenceSource.ifBlank { "Verified Reference" },
                    grading = grading,
                    verificationNotes = verificationNotes,
                    status = status,
                    verifiedBy = verifier
                )
                ref.set(doc.toCreateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // --- DATA EXPORT & ACCOUNT DELETION ---
    suspend fun exportUserDataJson(
        userId: String?,
        prefs: UserPreferencesState,
        checklists: List<LocalChecklistEntity>,
        bookmarks: List<LocalBookmarkEntity>,
        customActivities: List<LocalCustomActivityEntity>
    ): String {
        val root = JSONObject().apply {
            put("app", "Noor-e-Zindagi")
            put("userId", userId ?: "guest_local")
            put("language", prefs.language.code)
            put("location", prefs.locationName)
            put("checklists", JSONArray().apply {
                checklists.forEach { c ->
                    put(JSONObject().apply {
                        put("dateKey", c.dateKey)
                        put("completionPercent", c.completionPercent)
                        put("completedIds", c.completedIdsCsv)
                        put("fastingCompleted", c.fastingCompleted)
                    })
                }
            })
            put("bookmarks", JSONArray().apply {
                bookmarks.forEach { b ->
                    put(JSONObject().apply {
                        put("type", b.itemType)
                        put("refId", b.refId)
                        put("title", b.title)
                    })
                }
            })
            put("customActivities", JSONArray().apply {
                customActivities.forEach { a ->
                    put(JSONObject().apply {
                        put("title", a.title)
                        put("timeSlot", a.timeSlot)
                    })
                }
            })
        }
        return root.toString(2)
    }

    suspend fun deleteAccountAndData(userId: String?): Result<Unit> {
        return try {
            noorDao.clearChecklists()
            noorDao.clearBookmarks()
            noorDao.clearCustomActivities()
            if (!userId.isNullOrBlank()) {
                val uid = requireUserId()
                check(uid == userId) { "Signed-in user mismatch" }
                val userDoc = db.collection("users").document(uid)
                listOf("checklist", "bookmarks", "customActivities", "reminders").forEach { sub ->
                    val docs = userDoc.collection(sub).get().await()
                    docs.documents.forEach { it.reference.delete().await() }
                }
                userDoc.delete().await()
                auth.currentUser?.delete()?.await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "users/$userId")
            Result.failure(e)
        }
    }
}
