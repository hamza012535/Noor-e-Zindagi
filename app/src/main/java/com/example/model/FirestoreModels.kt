package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class UserProfileDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Believer",
    val email: String = "",
    val preferredLanguage: String = "en",
    val arabicFontScale: Double = 1.1,
    val translationFontScale: Double = 1.0,
    val darkReadingMode: Boolean = false,
    val calculationMethod: Long = 1L,
    val latitude: Double = 24.8607,
    val longitude: Double = 67.0011,
    val locationName: String = "Karachi, Pakistan",
    val missedPrayersEnabled: Boolean = true,
    val currentStreak: Long = 0L,
    val lastActiveDate: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "displayName" to displayName.take(80).ifBlank { "Believer" },
        "email" to email.take(120),
        "preferredLanguage" to preferredLanguage,
        "arabicFontScale" to arabicFontScale.coerceIn(0.7, 2.5),
        "translationFontScale" to translationFontScale.coerceIn(0.7, 2.5),
        "darkReadingMode" to darkReadingMode,
        "calculationMethod" to calculationMethod.coerceIn(0L, 25L),
        "latitude" to latitude.coerceIn(-90.0, 90.0),
        "longitude" to longitude.coerceIn(-180.0, 180.0),
        "locationName" to locationName.take(120),
        "missedPrayersEnabled" to missedPrayersEnabled,
        "currentStreak" to currentStreak.coerceAtLeast(0L),
        "lastActiveDate" to lastActiveDate.take(20),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class ChecklistDayDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val dateKey: String = "",
    val completedIds: List<String> = emptyList(),
    val missedPrayerIds: List<String> = emptyList(),
    val fastingCompleted: Boolean = false,
    val totalItems: Long = 1L,
    val completionPercent: Long = 0L,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "dateKey" to dateKey,
        "completedIds" to completedIds.take(120),
        "missedPrayerIds" to missedPrayerIds.take(20),
        "fastingCompleted" to fastingCompleted,
        "totalItems" to totalItems.coerceIn(1L, 120L),
        "completionPercent" to completionPercent.coerceIn(0L, 100L),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class BookmarkDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val itemType: String = "quran",
    val refId: String = "",
    val title: String = "",
    val arabicText: String = "",
    val subtitle: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "itemType" to itemType,
        "refId" to refId.take(120),
        "title" to title.take(200),
        "arabicText" to arabicText.take(2500),
        "subtitle" to subtitle.take(300),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class CustomActivityDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val timeSlot: String = "daily",
    val recommendedTime: String = "",
    val note: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "title" to title.take(120),
        "timeSlot" to timeSlot,
        "recommendedTime" to recommendedTime.take(40),
        "note" to note.take(400),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class ReminderConfigDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val category: String = "",
    val title: String = "",
    val hour: Long = 5L,
    val minute: Long = 0L,
    val enabled: Boolean = true,
    val frequency: String = "daily",
    val soundType: String = "chime",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "category" to category.take(40),
        "title" to title.take(100),
        "hour" to hour.coerceIn(0L, 23L),
        "minute" to minute.coerceIn(0L, 59L),
        "enabled" to enabled,
        "frequency" to frequency,
        "soundType" to soundType,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class CommunityContentDoc(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val contentType: String = "dua",
    val category: String = "General",
    val title: String = "",
    val arabicText: String = "",
    val romanUrdu: String = "",
    val urduTranslation: String = "",
    val englishTranslation: String = "",
    val referenceSource: String = "",
    val grading: String = "",
    val ocrExtractedText: String = "",
    val verificationNotes: String = "",
    val status: String = "pending",
    val verifiedBy: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "authorId" to authorId,
        "contentType" to contentType,
        "category" to category.take(80),
        "title" to title.take(200),
        "arabicText" to arabicText.take(3000),
        "romanUrdu" to romanUrdu.take(2000),
        "urduTranslation" to urduTranslation.take(3000),
        "englishTranslation" to englishTranslation.take(3000),
        "referenceSource" to referenceSource.take(300).ifBlank { "Pending Scholarly Verification" },
        "grading" to grading.take(80),
        "ocrExtractedText" to ocrExtractedText.take(4000),
        "verificationNotes" to verificationNotes.take(1000),
        "status" to status,
        "verifiedBy" to verifiedBy.take(120),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}
