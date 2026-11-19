package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "cached_ayahs",
    primaryKeys = ["surahNumber", "ayahNumber"]
)
data class CachedAyahEntity(
    val surahNumber: Int,
    val ayahNumber: Int,
    val globalAyahNumber: Int,
    val arabicUthmani: String,
    val englishTranslation: String,
    val urduTranslation: String,
    val romanUrdu: String,
    val translatorEn: String,
    val translatorUr: String,
    val audioUrl: String,
    val wordsJson: String
)

@Entity(tableName = "local_checklist")
data class LocalChecklistEntity(
    @PrimaryKey val dateKey: String,
    val completedIdsCsv: String,
    val missedPrayerIdsCsv: String,
    val fastingCompleted: Boolean,
    val totalItems: Int,
    val completionPercent: Int,
    val updatedAtMillis: Long
)

@Entity(tableName = "local_bookmarks")
data class LocalBookmarkEntity(
    @PrimaryKey val id: String,
    val itemType: String,
    val refId: String,
    val title: String,
    val arabicText: String,
    val subtitle: String,
    val createdAtMillis: Long
)

@Entity(tableName = "local_custom_activities")
data class LocalCustomActivityEntity(
    @PrimaryKey val id: String,
    val title: String,
    val timeSlot: String,
    val recommendedTime: String,
    val note: String,
    val createdAtMillis: Long
)

@Entity(tableName = "local_reminders")
data class LocalReminderEntity(
    @PrimaryKey val id: String,
    val category: String,
    val title: String,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean,
    val frequency: String,
    val soundType: String
)

@Entity(tableName = "local_ocr_submissions")
data class LocalOcrSubmissionEntity(
    @PrimaryKey val id: String,
    val imageUri: String,
    val title: String,
    val category: String,
    val contentType: String,
    val ocrText: String,
    val verificationSummary: String,
    val status: String,
    val referenceSource: String,
    val createdAtMillis: Long
)

@Dao
interface NoorDao {
    // Quran cache
    @Query("SELECT * FROM cached_ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    fun observeAyahsBySurah(surahNumber: Int): Flow<List<CachedAyahEntity>>

    @Query("SELECT * FROM cached_ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    suspend fun getAyahsBySurah(surahNumber: Int): List<CachedAyahEntity>

    @Query("SELECT * FROM cached_ayahs WHERE arabicUthmani LIKE '%' || :query || '%' OR englishTranslation LIKE '%' || :query || '%' OR urduTranslation LIKE '%' || :query || '%' LIMIT 40")
    suspend fun searchCachedAyahs(query: String): List<CachedAyahEntity>

    @Query("SELECT COUNT(DISTINCT surahNumber) FROM cached_ayahs")
    fun observeCachedSurahCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cached_ayahs")
    fun observeCachedAyahCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahs(ayahs: List<CachedAyahEntity>)

    // Checklist
    @Query("SELECT * FROM local_checklist ORDER BY dateKey DESC LIMIT 60")
    fun observeRecentChecklists(): Flow<List<LocalChecklistEntity>>

    @Query("SELECT * FROM local_checklist WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getChecklistForDate(dateKey: String): LocalChecklistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChecklist(entity: LocalChecklistEntity)

    @Query("DELETE FROM local_checklist")
    suspend fun clearChecklists()

    // Bookmarks
    @Query("SELECT * FROM local_bookmarks ORDER BY createdAtMillis DESC")
    fun observeBookmarks(): Flow<List<LocalBookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookmark(entity: LocalBookmarkEntity)

    @Query("DELETE FROM local_bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: String)

    @Query("DELETE FROM local_bookmarks")
    suspend fun clearBookmarks()

    // Custom Activities
    @Query("SELECT * FROM local_custom_activities ORDER BY createdAtMillis DESC")
    fun observeCustomActivities(): Flow<List<LocalCustomActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomActivity(entity: LocalCustomActivityEntity)

    @Query("DELETE FROM local_custom_activities WHERE id = :id")
    suspend fun deleteCustomActivityById(id: String)

    @Query("DELETE FROM local_custom_activities")
    suspend fun clearCustomActivities()

    // Reminders
    @Query("SELECT * FROM local_reminders ORDER BY hour ASC, minute ASC")
    fun observeReminders(): Flow<List<LocalReminderEntity>>

    @Query("SELECT * FROM local_reminders ORDER BY hour ASC, minute ASC")
    suspend fun getAllReminders(): List<LocalReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReminders(items: List<LocalReminderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReminder(item: LocalReminderEntity)

    // OCR Submissions
    @Query("SELECT * FROM local_ocr_submissions ORDER BY createdAtMillis DESC")
    fun observeOcrSubmissions(): Flow<List<LocalOcrSubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOcrSubmission(entity: LocalOcrSubmissionEntity)
}

@Database(
    entities = [
        CachedAyahEntity::class,
        LocalChecklistEntity::class,
        LocalBookmarkEntity::class,
        LocalCustomActivityEntity::class,
        LocalReminderEntity::class,
        LocalOcrSubmissionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NoorDatabase : RoomDatabase() {
    abstract fun noorDao(): NoorDao

    companion object {
        @Volatile
        private var INSTANCE: NoorDatabase? = null

        fun getInstance(context: Context): NoorDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoorDatabase::class.java,
                    "noor_e_zindagi_db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
