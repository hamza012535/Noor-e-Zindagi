package com.example.ui.viewmodel

import com.example.data.local.LocalBookmarkEntity
import com.example.data.local.LocalChecklistEntity
import com.example.data.local.LocalCustomActivityEntity
import com.example.data.local.LocalOcrSubmissionEntity
import com.example.data.local.LocalReminderEntity
import com.example.data.local.UserPreferencesState
import com.example.data.repository.OcrVerificationResult
import com.example.model.AiChatMessage
import com.example.model.AyahData
import com.example.model.CalendarDayCell
import com.example.model.CommunityContentDoc
import com.example.model.PrayerDaySchedule

sealed interface QuranReaderState {
    data object Loading : QuranReaderState
    data class Success(val ayahs: List<AyahData>) : QuranReaderState
    data class Error(val message: String) : QuranReaderState
}

sealed interface AiStreamState {
    data object Idle : AiStreamState
    data object Loading : AiStreamState
    data class Streaming(val partialText: String) : AiStreamState
    data class Error(val message: String) : AiStreamState
}

data class HabitReportStats(
    val todayPercent: Int = 0,
    val weeklyAveragePercent: Int = 0,
    val monthlyAveragePercent: Int = 0,
    val currentStreakDays: Int = 0,
    val totalDaysLogged: Int = 0,
    val fastingDaysThisMonth: Int = 0
)

data class NoorUiSnapshot(
    val prefs: UserPreferencesState = UserPreferencesState(),
    val todayDateKey: String = "",
    val completedIdsToday: Set<String> = emptySet(),
    val missedPrayerIdsToday: Set<String> = emptySet(),
    val fastingCompletedToday: Boolean = false,
    val checklistHistory: List<LocalChecklistEntity> = emptyList(),
    val habitStats: HabitReportStats = HabitReportStats(),
    val bookmarks: List<LocalBookmarkEntity> = emptyList(),
    val customActivities: List<LocalCustomActivityEntity> = emptyList(),
    val reminders: List<LocalReminderEntity> = emptyList(),
    val approvedCommunityContent: List<CommunityContentDoc> = emptyList(),
    val adminQueueContent: List<CommunityContentDoc> = emptyList(),
    val localOcrSubmissions: List<LocalOcrSubmissionEntity> = emptyList(),
    val prayerSchedule: PrayerDaySchedule? = null,
    val nextPrayerLabel: String = "Loading...",
    val nextPrayerCountdown: String = "--:--:--",
    val calendarYear: Int = 2026,
    val calendarMonth: Int = 11,
    val calendarCells: List<CalendarDayCell> = emptyList(),
    val isAdminUser: Boolean = false,
    val syncErrorBanner: String? = null
)
