package com.example.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientSoundState
import com.example.audio.AudioPlaybackMode
import com.example.audio.AudioPlaybackState
import com.example.audio.NoorMediaController
import com.example.audio.QuranReciter
import com.example.data.corpus.QuranCatalogData
import com.example.data.corpus.SunnahRoutineRepository
import com.example.data.local.LocalChecklistEntity
import com.example.data.local.LocalReminderEntity
import com.example.data.local.UserPreferencesDataStore
import com.example.data.repository.IslamicAiRepository
import com.example.data.repository.OcrVerificationResult
import com.example.data.repository.PrayerCalendarRepository
import com.example.data.repository.QuranRepository
import com.example.data.repository.UserSyncRepository
import com.example.model.AiChatMessage
import com.example.model.AppLanguage
import com.example.model.AyahData
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class NoorViewModel(
    private val appContext: Context,
    private val currentUserId: String?,
    private val userDisplayName: String,
    private val userEmail: String,
    private val prefsStore: UserPreferencesDataStore,
    private val syncRepo: UserSyncRepository,
    private val quranRepo: QuranRepository,
    private val prayerRepo: PrayerCalendarRepository,
    private val aiRepo: IslamicAiRepository,
    private val mediaController: NoorMediaController
) : ViewModel() {

    private val audioPlayer = mediaController.audioPlayer

    private val _todayDateKey = MutableStateFlow(todayKey())
    private val _prayerSchedule = MutableStateFlow<com.example.model.PrayerDaySchedule?>(null)
    private val _nextPrayerTicker = MutableStateFlow("Calculating..." to "--:--:--")
    private val _calendarMonthYear = MutableStateFlow(currentYearMonth())
    private val _calendarCells = MutableStateFlow<List<com.example.model.CalendarDayCell>>(emptyList())
    private val _syncErrorBanner = MutableStateFlow<String?>(null)

    private val _selectedSurah = MutableStateFlow(1)
    val selectedSurah: StateFlow<Int> = _selectedSurah.asStateFlow()

    private val _quranState = MutableStateFlow<QuranReaderState>(QuranReaderState.Loading)
    val quranState: StateFlow<QuranReaderState> = _quranState.asStateFlow()

    private val _quranSearchResults = MutableStateFlow<List<AyahData>>(emptyList())
    val quranSearchResults: StateFlow<List<AyahData>> = _quranSearchResults.asStateFlow()

    private val _aiMessages = MutableStateFlow(
        listOf(
            AiChatMessage(
                id = "welcome_ai",
                isUser = false,
                text = "Assalamu Alaikum! Ask me about daily Sunnah practices, authentic duas, Quranic meanings, or Hadith references. Every answer is grounded in verified Islamic sources.",
                citedSources = listOf("Sahih al-Bukhari", "Sahih Muslim", "Tanzil Quran Text")
            )
        )
    )
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    private val _aiStreamState = MutableStateFlow<AiStreamState>(AiStreamState.Idle)
    val aiStreamState: StateFlow<AiStreamState> = _aiStreamState.asStateFlow()

    private val _ocrResult = MutableStateFlow<OcrVerificationResult?>(null)
    val ocrResult: StateFlow<OcrVerificationResult?> = _ocrResult.asStateFlow()

    private val _ocrBusy = MutableStateFlow(false)
    val ocrBusy: StateFlow<Boolean> = _ocrBusy.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val audioState: StateFlow<AudioPlaybackState> = audioPlayer.state
    val ambientState: StateFlow<AmbientSoundState> = mediaController.ambientState

    private val _offlineDownloadProgress = MutableStateFlow(OfflineQuranDownloadState())
    val offlineDownloadState: StateFlow<OfflineQuranDownloadState> = combine(
        _offlineDownloadProgress,
        quranRepo.observeCachedSurahCount(),
        quranRepo.observeCachedAyahCount()
    ) { progress, cachedSurahs, cachedAyahs ->
        progress.copy(
            cachedSurahsCount = cachedSurahs,
            cachedAyahsCount = cachedAyahs,
            offlineAudioFilesCount = quranRepo.countOfflineAudioFiles(appContext),
            offlineAudioMb = quranRepo.computeOfflineAudioSizeMb(appContext)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = OfflineQuranDownloadState()
    )
    private var offlineDownloadJob: Job? = null

    private val checklistsFlow = syncRepo.observeChecklistHistory(currentUserId)
        .catch { err -> handleStreamError("checklist", err); emit(emptyList()) }

    private val bookmarksFlow = syncRepo.observeBookmarks(currentUserId)
        .catch { err -> handleStreamError("bookmarks", err); emit(emptyList()) }

    private val customActivitiesFlow = syncRepo.observeCustomActivities(currentUserId)
        .catch { err -> handleStreamError("custom activities", err); emit(emptyList()) }

    private val remindersFlow = syncRepo.observeReminders(currentUserId)
        .catch { err -> handleStreamError("reminders", err); emit(emptyList()) }

    private val approvedContentFlow = syncRepo.observeApprovedCommunityContent(currentUserId)
        .catch { err -> handleStreamError("community content", err); emit(emptyList()) }

    private val adminContentFlow = syncRepo.observeAdminCommunityQueue(currentUserId)
        .catch { err -> handleStreamError("admin queue", err); emit(emptyList()) }

    val uiState: StateFlow<NoorUiSnapshot> = combine(
        prefsStore.preferencesFlow,
        checklistsFlow,
        bookmarksFlow,
        customActivitiesFlow,
        remindersFlow,
        approvedContentFlow,
        adminContentFlow,
        syncRepo.observeOcrSubmissions(),
        _prayerSchedule,
        _nextPrayerTicker,
        _calendarMonthYear,
        _calendarCells,
        _todayDateKey,
        _syncErrorBanner
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val prefs = values[0] as com.example.data.local.UserPreferencesState
        @Suppress("UNCHECKED_CAST")
        val checklists = values[1] as List<LocalChecklistEntity>
        @Suppress("UNCHECKED_CAST")
        val bookmarks = values[2] as List<com.example.data.local.LocalBookmarkEntity>
        @Suppress("UNCHECKED_CAST")
        val customActivities = values[3] as List<com.example.data.local.LocalCustomActivityEntity>
        @Suppress("UNCHECKED_CAST")
        val reminders = values[4] as List<LocalReminderEntity>
        @Suppress("UNCHECKED_CAST")
        val approved = values[5] as List<com.example.model.CommunityContentDoc>
        @Suppress("UNCHECKED_CAST")
        val adminQueue = values[6] as List<com.example.model.CommunityContentDoc>
        @Suppress("UNCHECKED_CAST")
        val localOcr = values[7] as List<com.example.data.local.LocalOcrSubmissionEntity>
        val schedule = values[8] as com.example.model.PrayerDaySchedule?
        @Suppress("UNCHECKED_CAST")
        val ticker = values[9] as Pair<String, String>
        @Suppress("UNCHECKED_CAST")
        val calYm = values[10] as Pair<Int, Int>
        @Suppress("UNCHECKED_CAST")
        val calCells = values[11] as List<com.example.model.CalendarDayCell>
        val dateKey = values[12] as String
        val syncErr = values[13] as String?

        val todayEntry = checklists.firstOrNull { it.dateKey == dateKey }
        val completedToday = todayEntry?.completedIdsCsv?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
        val missedToday = todayEntry?.missedPrayerIdsCsv?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
        val fastingToday = todayEntry?.fastingCompleted ?: false

        NoorUiSnapshot(
            prefs = prefs,
            todayDateKey = dateKey,
            completedIdsToday = completedToday,
            missedPrayerIdsToday = missedToday,
            fastingCompletedToday = fastingToday,
            checklistHistory = checklists,
            habitStats = computeHabitStats(dateKey, completedToday.size, customActivities.size, checklists),
            bookmarks = bookmarks,
            customActivities = customActivities,
            reminders = reminders,
            approvedCommunityContent = approved,
            adminQueueContent = adminQueue,
            localOcrSubmissions = localOcr,
            prayerSchedule = schedule,
            nextPrayerLabel = ticker.first,
            nextPrayerCountdown = ticker.second,
            calendarYear = calYm.first,
            calendarMonth = calYm.second,
            calendarCells = calCells,
            isAdminUser = syncRepo.isCurrentUserAdmin(),
            syncErrorBanner = syncErr
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = NoorUiSnapshot()
    )

    init {
        viewModelScope.launch {
            syncRepo.ensureDefaultRemindersSeeded()
            val prefs = prefsStore.preferencesFlow.first()
            audioPlayer.setMode(AudioPlaybackMode.fromName(prefs.audioPlaybackMode))
            audioPlayer.setReciter(QuranReciter.fromEditionId(prefs.reciterId))
            audioPlayer.setRecitationVolume(prefs.recitationVolume)
            val initialAmbient = AmbientSoundState(
                isEnabled = prefs.ambientEnabled,
                birdsVolume = prefs.birdsVolume,
                rainVolume = prefs.rainVolume,
                airVolume = prefs.airVolume
            )
            mediaController.updateAmbientState(initialAmbient)
            if (!currentUserId.isNullOrBlank()) {
                syncRepo.ensureUserProfile(currentUserId, userDisplayName, userEmail, prefs)
            }
            _selectedSurah.value = prefs.lastReadSurah
            loadSurah(prefs.lastReadSurah)
            refreshPrayerSchedule()
            loadCalendarMonth(_calendarMonthYear.value.first, _calendarMonthYear.value.second)
        }
        startCountdownAndDayRolloverTicker()
    }

    private fun startCountdownAndDayRolloverTicker() {
        viewModelScope.launch {
            while (true) {
                val nowDateKey = todayKey()
                if (nowDateKey != _todayDateKey.value) {
                    _todayDateKey.value = nowDateKey
                    refreshPrayerSchedule()
                }
                val schedule = _prayerSchedule.value
                if (schedule != null && schedule.prayers.isNotEmpty()) {
                    val nowSec = System.currentTimeMillis() / 1000L
                    val next = schedule.prayers.firstOrNull { it.epochSeconds > nowSec }
                    if (next != null) {
                        val diff = (next.epochSeconds - nowSec).coerceAtLeast(0L)
                        _nextPrayerTicker.value = "${next.nameEn} at ${next.timeFormatted}" to formatCountdown(diff)
                    } else {
                        val fajrTomorrow = schedule.prayers.first()
                        val diff = ((fajrTomorrow.epochSeconds + 86400L) - nowSec).coerceAtLeast(0L)
                        _nextPrayerTicker.value = "Fajr (Tomorrow) ${fajrTomorrow.timeFormatted}" to formatCountdown(diff)
                    }
                }
                delay(1000L)
            }
        }
    }

    private fun handleStreamError(streamName: String, error: Throwable) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val root = firestoreError ?: error
        _syncErrorBanner.value = "Cloud sync note ($streamName): ${root.localizedMessage ?: "Working offline with local cache."}"
    }

    fun clearSnackbar() { _snackbarMessage.value = null }
    fun clearSyncBanner() { _syncErrorBanner.value = null }

    fun toggleChecklistActivity(activityId: String) {
        viewModelScope.launch {
            val snap = uiState.value
            val updated = if (activityId in snap.completedIdsToday) {
                snap.completedIdsToday - activityId
            } else {
                snap.completedIdsToday + activityId
            }
            val total = SunnahRoutineRepository.coreChecklistActivities.size + snap.customActivities.size
            val res = syncRepo.saveChecklistDay(
                userId = currentUserId,
                dateKey = snap.todayDateKey,
                completedIds = updated,
                missedPrayerIds = snap.missedPrayerIdsToday,
                fastingCompleted = snap.fastingCompletedToday,
                totalItems = total
            )
            if (res.isFailure) {
                _snackbarMessage.value = "Saved locally; cloud sync pending."
            }
        }
    }

    fun togglePrayerStatus(prayerId: String, markMissed: Boolean = false) {
        viewModelScope.launch {
            val snap = uiState.value
            val completed = snap.completedIdsToday.toMutableSet()
            val missed = snap.missedPrayerIdsToday.toMutableSet()
            if (markMissed) {
                completed.remove(prayerId)
                if (prayerId in missed) missed.remove(prayerId) else missed.add(prayerId)
            } else {
                missed.remove(prayerId)
                if (prayerId in completed) completed.remove(prayerId) else completed.add(prayerId)
            }
            val total = SunnahRoutineRepository.coreChecklistActivities.size + snap.customActivities.size
            syncRepo.saveChecklistDay(
                userId = currentUserId,
                dateKey = snap.todayDateKey,
                completedIds = completed,
                missedPrayerIds = missed,
                fastingCompleted = snap.fastingCompletedToday,
                totalItems = total
            )
            refreshPrayerSchedule()
        }
    }

    fun toggleFastingToday() {
        viewModelScope.launch {
            val snap = uiState.value
            val total = SunnahRoutineRepository.coreChecklistActivities.size + snap.customActivities.size
            syncRepo.saveChecklistDay(
                userId = currentUserId,
                dateKey = snap.todayDateKey,
                completedIds = snap.completedIdsToday,
                missedPrayerIds = snap.missedPrayerIdsToday,
                fastingCompleted = !snap.fastingCompletedToday,
                totalItems = total
            )
        }
    }

    fun toggleBookmark(itemType: String, refId: String, title: String, arabicText: String, subtitle: String) {
        viewModelScope.launch {
            val exists = uiState.value.bookmarks.any { it.itemType == itemType && it.refId == refId }
            val res = syncRepo.toggleBookmark(
                userId = currentUserId,
                itemType = itemType,
                refId = refId,
                title = title,
                arabicText = arabicText,
                subtitle = subtitle,
                currentlyBookmarked = exists
            )
            _snackbarMessage.value = if (res.isSuccess) {
                if (exists) "Removed from bookmarks" else "Saved to bookmarks"
            } else {
                "Bookmark saved locally"
            }
        }
    }

    fun addCustomActivity(title: String, timeSlot: String, recommendedTime: String, note: String) {
        viewModelScope.launch {
            val res = syncRepo.addCustomActivity(currentUserId, title, timeSlot, recommendedTime, note)
            _snackbarMessage.value = if (res.isSuccess) "Custom Sunnah habit added" else res.exceptionOrNull()?.message
        }
    }

    fun deleteCustomActivity(id: String) {
        viewModelScope.launch {
            syncRepo.deleteCustomActivity(currentUserId, id)
        }
    }

    // --- QURAN ---
    fun selectSurah(surahNumber: Int) {
        _selectedSurah.value = surahNumber
        loadSurah(surahNumber)
    }

    fun loadSurah(surahNumber: Int) {
        viewModelScope.launch {
            _quranState.value = QuranReaderState.Loading
            val res = quranRepo.getSurahAyahs(surahNumber)
            _quranState.value = res.fold(
                onSuccess = { ayahs ->
                    prefsStore.setLastReadPosition(surahNumber, ayahs.firstOrNull()?.ayahNumber ?: 1)
                    QuranReaderState.Success(ayahs)
                },
                onFailure = { err ->
                    QuranReaderState.Error(err.localizedMessage ?: "Connect to internet once to download Surah #$surahNumber for offline reading.")
                }
            )
        }
    }

    fun markLastRead(surahNumber: Int, ayahNumber: Int) {
        viewModelScope.launch {
            prefsStore.setLastReadPosition(surahNumber, ayahNumber)
            _snackbarMessage.value = "Last-read set to Surah $surahNumber:$ayahNumber"
        }
    }

    fun searchQuranVerses(query: String) {
        viewModelScope.launch {
            _quranSearchResults.value = quranRepo.searchVerses(query)
        }
    }

    fun toggleAudio(id: String, title: String, url: String) {
        audioPlayer.togglePlay(id, title, url)
        mediaController.syncForegroundService()
    }

    fun toggleAudioWithUrdu(
        id: String,
        title: String,
        arabicUrl: String?,
        urduUrl: String?,
        urduText: String
    ) {
        audioPlayer.togglePlayWithUrdu(id, title, arabicUrl, urduUrl, urduText)
        mediaController.syncForegroundService()
    }

    fun togglePlayPauseAudio() {
        audioPlayer.togglePlayPause()
        mediaController.syncForegroundService()
    }

    fun stopAllAudio() {
        mediaController.stopAllFromMediaSession()
    }

    fun playAyahWithAutoContinue(ayah: AyahData, ayahsInSurah: List<AyahData>) {
        val currentIndex = ayahsInSurah.indexOfFirst {
            it.surahNumber == ayah.surahNumber && it.ayahNumber == ayah.ayahNumber
        }
        val nextAyah = if (currentIndex >= 0 && currentIndex + 1 < ayahsInSurah.size) {
            ayahsInSurah[currentIndex + 1]
        } else {
            null
        }
        viewModelScope.launch {
            prefsStore.setLastReadPosition(ayah.surahNumber, ayah.ayahNumber)
        }
        audioPlayer.togglePlayWithUrdu(
            id = "ayah_${ayah.surahNumber}_${ayah.ayahNumber}",
            title = "Surah ${ayah.surahNumber}:${ayah.ayahNumber}",
            arabicUrl = ayah.audioUrl,
            urduUrl = ayah.urduAudioUrl,
            urduText = ayah.urduTranslation,
            onFinished = {
                if (uiState.value.prefs.autoPlayNextAyah && nextAyah != null) {
                    playAyahWithAutoContinue(nextAyah, ayahsInSurah)
                }
            }
        )
        mediaController.syncForegroundService()
    }

    fun startOfflineQuranDownload(includeArabicAudio: Boolean, includeUrduAudio: Boolean) {
        if (offlineDownloadJob?.isActive == true) return
        val reciter = audioState.value.reciter
        val includeAnyAudio = includeArabicAudio || includeUrduAudio
        val totalAudioTarget = when {
            includeArabicAudio && includeUrduAudio -> 6236 * 2
            includeArabicAudio || includeUrduAudio -> 6236
            else -> 0
        }

        offlineDownloadJob = viewModelScope.launch(Dispatchers.IO) {
            var completedAudio = 0
            val semaphore = Semaphore(4)
            _offlineDownloadProgress.value = OfflineQuranDownloadState(
                isDownloading = true,
                currentSurah = 0,
                totalSurahs = 114,
                currentSurahName = "Preparing...",
                downloadedAudioFiles = 0,
                totalAudioFilesTarget = totalAudioTarget,
                includeAudio = includeAnyAudio,
                statusMessage = "Starting offline download of all 114 Surahs..."
            )
            try {
                for (surahNum in 1..114) {
                    val info = QuranCatalogData.getSurahInfo(surahNum)
                    _offlineDownloadProgress.value = _offlineDownloadProgress.value.copy(
                        currentSurah = surahNum,
                        currentSurahName = info.nameEnglish,
                        statusMessage = "Downloading Surah $surahNum/114: ${info.nameEnglish}..."
                    )
                    val res = quranRepo.getSurahAyahs(surahNum)
                    val ayahs = res.getOrNull().orEmpty()

                    if (includeAnyAudio && ayahs.isNotEmpty()) {
                        coroutineScope {
                            val jobs = ayahs.flatMap { ayah ->
                                buildList {
                                    if (includeArabicAudio && ayah.globalAyahNumber in 1..6236) {
                                        add(async {
                                            semaphore.withPermit {
                                                val arUrl = "https://cdn.islamic.network/quran/audio/128/${reciter.editionId}/${ayah.globalAyahNumber}.mp3"
                                                if (quranRepo.downloadAudioFileIfNeeded(appContext, reciter.editionId, ayah.globalAyahNumber, arUrl)) {
                                                    completedAudio++
                                                }
                                            }
                                        })
                                    }
                                    if (includeUrduAudio && ayah.globalAyahNumber in 1..6236) {
                                        add(async {
                                            semaphore.withPermit {
                                                val urUrl = "https://cdn.islamic.network/quran/audio/64/ur.khan/${ayah.globalAyahNumber}.mp3"
                                                if (quranRepo.downloadAudioFileIfNeeded(appContext, "ur.khan", ayah.globalAyahNumber, urUrl)) {
                                                    completedAudio++
                                                }
                                            }
                                        })
                                    }
                                }
                            }
                            jobs.awaitAll()
                        }
                        _offlineDownloadProgress.value = _offlineDownloadProgress.value.copy(
                            downloadedAudioFiles = completedAudio
                        )
                    }
                }
                _offlineDownloadProgress.value = OfflineQuranDownloadState(
                    isDownloading = false,
                    currentSurah = 114,
                    totalSurahs = 114,
                    currentSurahName = "Completed",
                    downloadedAudioFiles = completedAudio,
                    totalAudioFilesTarget = totalAudioTarget,
                    includeAudio = includeAnyAudio,
                    statusMessage = if (includeAnyAudio) {
                        "All 114 Surahs & $completedAudio audio clips ready offline."
                    } else {
                        "All 114 Surahs text & translations saved offline."
                    }
                )
            } catch (_: CancellationException) {
                _offlineDownloadProgress.value = _offlineDownloadProgress.value.copy(
                    isDownloading = false,
                    statusMessage = "Offline download paused/cancelled. Already cached Surahs remain saved."
                )
            } catch (e: Exception) {
                _offlineDownloadProgress.value = _offlineDownloadProgress.value.copy(
                    isDownloading = false,
                    statusMessage = "Download interrupted: ${e.localizedMessage ?: "Check internet connection"}"
                )
            }
        }
    }

    fun cancelOfflineQuranDownload() {
        offlineDownloadJob?.cancel()
        offlineDownloadJob = null
    }

    fun clearOfflineAudioCache() {
        cancelOfflineQuranDownload()
        quranRepo.clearOfflineAudioCache(appContext)
        _offlineDownloadProgress.value = _offlineDownloadProgress.value.copy(
            downloadedAudioFiles = 0,
            statusMessage = "Offline audio MP3 cache cleared. Surah texts remain cached."
        )
    }

    fun setAutoPlayNextAyah(enabled: Boolean) {
        viewModelScope.launch {
            prefsStore.setAutoPlayNextAyah(enabled)
        }
    }

    fun setAudioPlaybackMode(mode: AudioPlaybackMode) {
        audioPlayer.setMode(mode)
        mediaController.syncForegroundService()
        viewModelScope.launch {
            prefsStore.setAudioPlaybackMode(mode.name)
        }
    }

    fun setReciter(reciter: QuranReciter) {
        audioPlayer.setReciter(reciter)
        mediaController.syncForegroundService()
        viewModelScope.launch {
            prefsStore.setReciterId(reciter.editionId)
        }
    }

    fun setRecitationVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        audioPlayer.setRecitationVolume(clamped)
        viewModelScope.launch {
            prefsStore.setRecitationVolume(clamped)
        }
    }

    fun updateAmbientSound(
        enabled: Boolean = mediaController.ambientState.value.isEnabled,
        birds: Float = mediaController.ambientState.value.birdsVolume,
        rain: Float = mediaController.ambientState.value.rainVolume,
        air: Float = mediaController.ambientState.value.airVolume
    ) {
        val next = AmbientSoundState(
            isEnabled = enabled,
            birdsVolume = birds.coerceIn(0f, 1f),
            rainVolume = rain.coerceIn(0f, 1f),
            airVolume = air.coerceIn(0f, 1f)
        )
        mediaController.updateAmbientState(next)
        viewModelScope.launch {
            prefsStore.setAmbientSound(next.isEnabled, next.birdsVolume, next.rainVolume, next.airVolume)
        }
    }

    override fun onCleared() {
        super.onCleared()
        offlineDownloadJob?.cancel()
    }

    // --- PRAYER TIMES & CALENDAR ---
    fun refreshPrayerSchedule() {
        viewModelScope.launch {
            val prefs = prefsStore.preferencesFlow.first()
            val snap = uiState.value
            _prayerSchedule.value = prayerRepo.getTodayPrayerSchedule(
                prefs = prefs,
                completedIds = snap.completedIdsToday,
                missedIds = snap.missedPrayerIdsToday
            )
        }
    }

    fun updateLocation(lat: Double, lng: Double, name: String) {
        viewModelScope.launch {
            prefsStore.setLocation(lat, lng, name)
            refreshPrayerSchedule()
            _snackbarMessage.value = "Location updated: $name"
        }
    }

    fun updateCalculationMethod(methodId: Int) {
        viewModelScope.launch {
            prefsStore.setCalculationMethod(methodId)
            refreshPrayerSchedule()
        }
    }

    fun updatePrayerAdjustments(fajr: Int, dhuhr: Int, asr: Int, maghrib: Int, isha: Int) {
        viewModelScope.launch {
            prefsStore.setPrayerAdjustments(fajr, dhuhr, asr, maghrib, isha)
            refreshPrayerSchedule()
            _snackbarMessage.value = "Prayer minute adjustments saved"
        }
    }

    fun setMissedPrayersEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsStore.setMissedPrayersEnabled(enabled) }
    }

    fun loadCalendarMonth(year: Int, month1Based: Int) {
        _calendarMonthYear.value = year to month1Based
        viewModelScope.launch {
            _calendarCells.value = prayerRepo.getMonthlyCalendar(year, month1Based)
        }
    }

    // --- PREFERENCES & REMINDERS ---
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            prefsStore.setLanguage(language)
            val prefs = prefsStore.preferencesFlow.first()
            syncRepo.syncPreferencesToCloud(currentUserId, prefs, uiState.value.habitStats.currentStreakDays, uiState.value.todayDateKey)
        }
    }

    fun setFontScales(arabic: Float, translation: Float) {
        viewModelScope.launch { prefsStore.setFontScales(arabic, translation) }
    }

    fun setDarkReadingMode(enabled: Boolean) {
        viewModelScope.launch { prefsStore.setDarkReadingMode(enabled) }
    }

    fun saveReminder(reminder: LocalReminderEntity) {
        viewModelScope.launch {
            syncRepo.saveReminder(currentUserId, reminder)
            _snackbarMessage.value = "${reminder.title} reminder ${if (reminder.enabled) "enabled" else "paused"}"
        }
    }

    // --- AI ASSISTANT ---
    fun askIslamicAssistant(question: String) {
        val clean = question.trim()
        if (clean.isBlank()) return
        val userMsg = AiChatMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            text = clean
        )
        _aiMessages.value = _aiMessages.value + userMsg
        val cited = aiRepo.retrieveVerifiedSourcesForQuery(clean)

        viewModelScope.launch {
            _aiStreamState.value = AiStreamState.Loading
            var finalReply = ""
            aiRepo.askAssistantStream(clean).collect { result ->
                result.fold(
                    onSuccess = { partial ->
                        finalReply = partial
                        _aiStreamState.value = AiStreamState.Streaming(partial)
                    },
                    onFailure = { err ->
                        val fallbackMsg = if (cited.isNotEmpty()) {
                            "AI service is currently unavailable (${err.localizedMessage}). Here are verified matches from the offline Islamic database:\n\n" +
                                cited.joinToString("\n\n")
                        } else {
                            "Unable to reach Gemini AI right now (${err.localizedMessage}). Please check your internet connection or browse the Sunnah, Quran, Hadith, and Azkar sections offline."
                        }
                        _aiStreamState.value = AiStreamState.Error(err.localizedMessage ?: "AI request failed")
                        _aiMessages.value = _aiMessages.value + AiChatMessage(
                            id = UUID.randomUUID().toString(),
                            isUser = false,
                            text = fallbackMsg,
                            citedSources = cited
                        )
                    }
                )
            }
            if (finalReply.isNotBlank()) {
                _aiMessages.value = _aiMessages.value + AiChatMessage(
                    id = UUID.randomUUID().toString(),
                    isUser = false,
                    text = finalReply,
                    citedSources = cited
                )
                _aiStreamState.value = AiStreamState.Idle
            }
        }
    }

    // --- OCR & ADMIN ---
    fun runOcrExtraction(bitmap: Bitmap) {
        viewModelScope.launch {
            _ocrBusy.value = true
            val res = aiRepo.extractAndVerifyOcr(bitmap)
            res.fold(
                onSuccess = { _ocrResult.value = it },
                onFailure = { err ->
                    _snackbarMessage.value = "OCR error: ${err.localizedMessage ?: "Unable to extract text"}"
                }
            )
            _ocrBusy.value = false
        }
    }

    fun submitOcrForApproval(
        imageUri: String,
        title: String,
        category: String,
        contentType: String,
        ocrText: String,
        verificationSummary: String,
        referenceSource: String
    ) {
        viewModelScope.launch {
            val res = syncRepo.submitPendingOcrContent(
                userId = currentUserId,
                imageUri = imageUri,
                title = title,
                category = category,
                contentType = contentType,
                ocrText = ocrText,
                verificationSummary = verificationSummary,
                referenceSource = referenceSource
            )
            _snackbarMessage.value = if (res.isSuccess) {
                "Submitted for human Admin verification (Pending Approval)"
            } else {
                "Saved locally; sign in to submit to cloud Admin queue."
            }
            _ocrResult.value = null
        }
    }

    fun adminSaveOrReview(
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
    ) {
        viewModelScope.launch {
            val res = syncRepo.adminSaveOrReviewContent(
                contentId = contentId,
                contentType = contentType,
                category = category,
                title = title,
                arabicText = arabicText,
                romanUrdu = romanUrdu,
                urduTranslation = urduTranslation,
                englishTranslation = englishTranslation,
                referenceSource = referenceSource,
                grading = grading,
                verificationNotes = verificationNotes,
                status = status
            )
            _snackbarMessage.value = if (res.isSuccess) {
                "Content updated ($status)"
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Admin action failed"
            }
        }
    }

    suspend fun exportUserDataJson(): String {
        val snap = uiState.value
        return syncRepo.exportUserDataJson(
            userId = currentUserId,
            prefs = snap.prefs,
            checklists = snap.checklistHistory,
            bookmarks = snap.bookmarks,
            customActivities = snap.customActivities
        )
    }

    fun deleteAccountAndData(onComplete: () -> Unit) {
        viewModelScope.launch {
            val res = syncRepo.deleteAccountAndData(currentUserId)
            if (res.isSuccess) {
                prefsStore.setGuestModeActive(false)
                _snackbarMessage.value = "Account and personal data deleted."
                onComplete()
            } else {
                _snackbarMessage.value = res.exceptionOrNull()?.localizedMessage
                    ?: "Re-authentication may be required before deleting account."
            }
        }
    }

    private fun computeHabitStats(
        todayKey: String,
        completedTodayCount: Int,
        customCount: Int,
        history: List<LocalChecklistEntity>
    ): HabitReportStats {
        val totalToday = (SunnahRoutineRepository.coreChecklistActivities.size + customCount).coerceAtLeast(1)
        val todayPct = ((completedTodayCount * 100) / totalToday).coerceIn(0, 100)
        val last7 = history.take(7)
        val last30 = history.take(30)
        val weeklyAvg = if (last7.isEmpty()) todayPct else last7.map { it.completionPercent }.average().toInt()
        val monthlyAvg = if (last30.isEmpty()) todayPct else last30.map { it.completionPercent }.average().toInt()

        var streak = 0
        val sortedDates = history.filter { it.completionPercent >= 30 }.map { it.dateKey }.toSet()
        val cal = Calendar.getInstance()
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (i in 0 until 60) {
            val key = fmt.format(cal.time)
            if (key in sortedDates || (i == 0 && todayPct >= 30)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else if (i == 0) {
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        val fastingDays = last30.count { it.fastingCompleted }
        return HabitReportStats(
            todayPercent = todayPct,
            weeklyAveragePercent = weeklyAvg,
            monthlyAveragePercent = monthlyAvg,
            currentStreakDays = streak,
            totalDaysLogged = history.size,
            fastingDaysThisMonth = fastingDays
        )
    }

    private fun formatCountdown(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return "%02dh %02dm %02ds".format(h, m, s)
    }

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)

    private fun currentYearMonth(): Pair<Int, Int> {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.YEAR) to (cal.get(Calendar.MONTH) + 1)
    }
}
