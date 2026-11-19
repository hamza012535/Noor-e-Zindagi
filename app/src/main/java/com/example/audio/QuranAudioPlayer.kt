package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.Locale
import java.util.UUID

enum class AudioPlaybackMode(val label: String) {
    ARABIC_AND_URDU("Arabic + Urdu"),
    ARABIC_ONLY("Arabic Only"),
    URDU_ONLY("Urdu Only");

    companion object {
        fun fromName(name: String?): AudioPlaybackMode =
            entries.firstOrNull { it.name == name } ?: ARABIC_AND_URDU
    }
}

enum class QuranReciter(
    val editionId: String,
    val displayName: String,
    val styleNote: String
) {
    ALAFASY("ar.alafasy", "Mishary Rashid Alafasy", "Murattal • Kuwait"),
    ABDUL_BASIT("ar.abdulbasitmurattal", "Abdul Basit Abdus Samad", "Murattal • Egypt"),
    HUSARY("ar.husary", "Mahmoud Khalil Al-Husary", "Classic Tajweed • Egypt"),
    MINSHAWI("ar.minshawi", "Mohamed Siddiq El-Minshawi", "Reflective Murattal • Egypt"),
    SUDAIS("ar.abdurrahmaansudais", "Abdur-Rahman As-Sudais", "Masjid al-Haram • Makkah");

    companion object {
        fun fromEditionId(id: String?): QuranReciter =
            entries.firstOrNull { it.editionId == id } ?: ALAFASY
    }
}

data class AudioPlaybackState(
    val activeId: String? = null,
    val activeTitle: String = "",
    val activePhaseLabel: String = "",
    val mode: AudioPlaybackMode = AudioPlaybackMode.ARABIC_AND_URDU,
    val reciter: QuranReciter = QuranReciter.ALAFASY,
    val recitationVolume: Float = 1.0f,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val isBuffering: Boolean = false,
    val errorMessage: String? = null
)

class QuranAudioPlayer(private val appContext: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var ttsReady: Boolean = false
    private var currentMode: AudioPlaybackMode = AudioPlaybackMode.ARABIC_AND_URDU
    private var currentReciter: QuranReciter = QuranReciter.ALAFASY
    private var currentVolume: Float = 1.0f

    private val _state = MutableStateFlow(AudioPlaybackState())
    val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

    private val mainHandler = Handler(Looper.getMainLooper())
    private var pendingArabicUrl: String? = null
    private var pendingUrduUrl: String? = null
    private var pendingUrduText: String = ""
    private var pendingId: String? = null
    private var pendingTitle: String = ""
    private var pendingOnFinished: (() -> Unit)? = null
    private var activePausedTts: Boolean = false
    var onSkipNext: (() -> Unit)? = null

    fun setMode(mode: AudioPlaybackMode) {
        currentMode = mode
        _state.value = _state.value.copy(mode = mode)
    }

    fun setReciter(reciter: QuranReciter) {
        currentReciter = reciter
        _state.value = _state.value.copy(reciter = reciter)
    }

    fun setRecitationVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        try {
            mediaPlayer?.setVolume(currentVolume, currentVolume)
        } catch (_: Exception) {
        }
        _state.value = _state.value.copy(recitationVolume = currentVolume)
    }

    fun togglePlay(id: String, title: String, url: String) {
        togglePlayWithUrdu(id = id, title = title, arabicUrl = url, urduUrl = null, urduText = "")
    }

    fun togglePlayWithUrdu(
        id: String,
        title: String,
        arabicUrl: String?,
        urduUrl: String?,
        urduText: String,
        onFinished: (() -> Unit)? = null
    ) {
        val current = _state.value
        if (current.activeId == id && (current.isPlaying || current.isBuffering)) {
            stop()
            return
        }
        startPlayback(id, title, resolveReciterUrl(arabicUrl), urduUrl, urduText, onFinished)
    }

    private fun resolveReciterUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return url.replace("/ar.alafasy/", "/${currentReciter.editionId}/")
    }

    private fun resolvePlayableDataSource(url: String): String {
        val match = AUDIO_URL_REGEX.find(url)
        if (match != null) {
            val edition = match.groupValues[1]
            val fileName = match.groupValues[2]
            val localFile = File(appContext.filesDir, "quran_audio/$edition/$fileName")
            if (localFile.exists() && localFile.length() > 512L) {
                return localFile.absolutePath
            }
        }
        return url
    }

    private fun startPlayback(
        id: String,
        title: String,
        arabicUrl: String?,
        urduUrl: String?,
        urduText: String,
        onFinished: (() -> Unit)?
    ) {
        stopInternal()
        pendingId = id
        pendingTitle = title
        pendingArabicUrl = arabicUrl?.takeIf { it.isNotBlank() }
        pendingUrduUrl = urduUrl?.takeIf { it.isNotBlank() }
        pendingUrduText = urduText.trim()
        pendingOnFinished = onFinished

        when (currentMode) {
            AudioPlaybackMode.ARABIC_ONLY -> {
                val ar = pendingArabicUrl
                if (ar != null) {
                    playStreamUrl(
                        id = id,
                        title = title,
                        phaseLabel = "Arabic (${currentReciter.displayName})",
                        url = ar,
                        onDone = { completeTrack(id) },
                        onStreamError = {
                            _state.value = AudioPlaybackState(
                                activeId = id,
                                activeTitle = title,
                                mode = currentMode,
                                reciter = currentReciter,
                                recitationVolume = currentVolume,
                                errorMessage = "Arabic audio unavailable offline. Connect to internet."
                            )
                        }
                    )
                } else {
                    _state.value = AudioPlaybackState(
                        activeId = id,
                        activeTitle = title,
                        mode = currentMode,
                        reciter = currentReciter,
                        recitationVolume = currentVolume,
                        errorMessage = "Arabic MP3 is available for Quran verses; switch to Arabic + Urdu or Urdu Only for spoken Urdu translation."
                    )
                }
            }

            AudioPlaybackMode.URDU_ONLY -> {
                playUrduPhase(id, title)
            }

            AudioPlaybackMode.ARABIC_AND_URDU -> {
                val ar = pendingArabicUrl
                if (ar != null) {
                    playStreamUrl(
                        id = id,
                        title = title,
                        phaseLabel = "1/2 Arabic (${currentReciter.displayName})",
                        url = ar,
                        onDone = {
                            if (pendingId == id) {
                                playUrduPhase(id, title)
                            }
                        },
                        onStreamError = {
                            if (pendingUrduText.isNotBlank()) {
                                speakUrduText(id, title, "Urdu Translation (Offline Voice)")
                            } else {
                                _state.value = AudioPlaybackState(
                                    activeId = id,
                                    activeTitle = title,
                                    mode = currentMode,
                                    reciter = currentReciter,
                                    recitationVolume = currentVolume,
                                    errorMessage = "Audio stream unavailable offline. Connect to internet."
                                )
                            }
                        }
                    )
                } else {
                    playUrduPhase(id, title)
                }
            }
        }
    }

    private fun playUrduPhase(id: String, title: String) {
        val uUrl = pendingUrduUrl
        if (uUrl != null) {
            playStreamUrl(
                id = id,
                title = title,
                phaseLabel = if (currentMode == AudioPlaybackMode.ARABIC_AND_URDU) {
                    "2/2 Urdu Translation (Shamshad Ali Khan)"
                } else {
                    "Urdu Translation (Shamshad Ali Khan)"
                },
                url = uUrl,
                onDone = { completeTrack(id) },
                onStreamError = {
                    if (pendingUrduText.isNotBlank()) {
                        speakUrduText(id, title, "Urdu Translation (Device Voice)")
                    } else {
                        _state.value = AudioPlaybackState(
                            activeId = id,
                            activeTitle = title,
                            mode = currentMode,
                            reciter = currentReciter,
                            recitationVolume = currentVolume,
                            errorMessage = "Urdu audio stream unavailable offline."
                        )
                    }
                }
            )
        } else if (pendingUrduText.isNotBlank()) {
            speakUrduText(id, title, "Urdu Translation (Spoken Audio)")
        } else {
            completeTrack(id)
        }
    }

    private fun playStreamUrl(
        id: String,
        title: String,
        phaseLabel: String,
        url: String,
        onDone: () -> Unit,
        onStreamError: () -> Unit
    ) {
        releasePlayer()
        _state.value = AudioPlaybackState(
            activeId = id,
            activeTitle = title,
            activePhaseLabel = phaseLabel,
            mode = currentMode,
            reciter = currentReciter,
            recitationVolume = currentVolume,
            isPlaying = false,
            isBuffering = true,
            errorMessage = null
        )
        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(resolvePlayableDataSource(url))
                setOnPreparedListener { mp ->
                    if (pendingId != id) {
                        releasePlayer()
                        return@setOnPreparedListener
                    }
                    mp.setVolume(currentVolume, currentVolume)
                    mp.start()
                    _state.value = AudioPlaybackState(
                        activeId = id,
                        activeTitle = title,
                        activePhaseLabel = phaseLabel,
                        mode = currentMode,
                        reciter = currentReciter,
                        recitationVolume = currentVolume,
                        isPlaying = true,
                        isBuffering = false,
                        errorMessage = null
                    )
                }
                setOnCompletionListener {
                    releasePlayer()
                    onDone()
                }
                setOnErrorListener { _, _, _ ->
                    releasePlayer()
                    onStreamError()
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (_: Exception) {
            releasePlayer()
            onStreamError()
        }
    }

    private fun speakUrduText(id: String, title: String, phaseLabel: String) {
        val text = pendingUrduText
        if (text.isBlank()) {
            resetState()
            return
        }
        _state.value = AudioPlaybackState(
            activeId = id,
            activeTitle = title,
            activePhaseLabel = phaseLabel,
            mode = currentMode,
            reciter = currentReciter,
            recitationVolume = currentVolume,
            isPlaying = true,
            isBuffering = false,
            errorMessage = null
        )

        val existing = tts
        if (existing != null && ttsReady) {
            dispatchTts(existing, id, text)
            return
        }

        try {
            tts?.shutdown()
        } catch (_: Exception) {
        }
        ttsReady = false
        tts = TextToSpeech(appContext) { status ->
            val engine = tts ?: return@TextToSpeech
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                val urPk = Locale.forLanguageTag("ur-PK")
                val urIn = Locale.forLanguageTag("ur-IN")
                val res = engine.setLanguage(urPk)
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    val alt = engine.setLanguage(urIn)
                    if (alt == TextToSpeech.LANG_MISSING_DATA || alt == TextToSpeech.LANG_NOT_SUPPORTED) {
                        _state.value = AudioPlaybackState(
                            activeId = id,
                            activeTitle = title,
                            mode = currentMode,
                            reciter = currentReciter,
                            recitationVolume = currentVolume,
                            errorMessage = "Urdu device voice not installed. Enable Urdu TTS in Android Settings or connect to internet for Quran Urdu audio."
                        )
                        return@TextToSpeech
                    }
                }
                engine.setSpeechRate(0.92f)
                if (pendingId == id) {
                    dispatchTts(engine, id, text)
                }
            } else {
                _state.value = AudioPlaybackState(
                    activeId = id,
                    activeTitle = title,
                    mode = currentMode,
                    reciter = currentReciter,
                    recitationVolume = currentVolume,
                    errorMessage = "Speech engine unavailable on this device."
                )
            }
        }
    }

    private fun dispatchTts(engine: TextToSpeech, id: String, text: String) {
        val utteranceId = UUID.randomUUID().toString()
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(uid: String?) {}
            override fun onDone(uid: String?) {
                mainHandler.post { completeTrack(id) }
            }
            @Deprecated("Deprecated in Java")
            override fun onError(uid: String?) {
                mainHandler.post {
                    if (pendingId == id) {
                        pendingOnFinished = null
                        _state.value = AudioPlaybackState(
                            activeId = id,
                            activeTitle = pendingTitle,
                            mode = currentMode,
                            reciter = currentReciter,
                            recitationVolume = currentVolume,
                            errorMessage = "Unable to play spoken Urdu audio."
                        )
                    }
                }
            }
        })
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, currentVolume)
        }
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    private fun completeTrack(id: String) {
        if (pendingId != id) return
        val callback = pendingOnFinished
        pendingId = null
        pendingOnFinished = null
        resetState()
        callback?.invoke()
    }

    fun pause() {
        val current = _state.value
        if (!current.isPlaying) return
        val mp = mediaPlayer
        if (mp != null) {
            try {
                if (mp.isPlaying) mp.pause()
                activePausedTts = false
                _state.value = current.copy(isPlaying = false, isPaused = true)
            } catch (_: Exception) {
            }
        } else {
            try {
                tts?.stop()
            } catch (_: Exception) {
            }
            activePausedTts = true
            _state.value = current.copy(isPlaying = false, isPaused = true)
        }
    }

    fun resume() {
        val current = _state.value
        if (!current.isPaused) return
        val mp = mediaPlayer
        if (mp != null && !activePausedTts) {
            try {
                mp.setVolume(currentVolume, currentVolume)
                mp.start()
                _state.value = current.copy(isPlaying = true, isPaused = false)
            } catch (_: Exception) {
            }
        } else {
            val id = pendingId
            if (id != null && pendingUrduText.isNotBlank()) {
                activePausedTts = false
                speakUrduText(id, pendingTitle, current.activePhaseLabel.ifBlank { "Urdu Translation" })
            }
        }
    }

    fun togglePlayPause() {
        val current = _state.value
        if (current.isPlaying) pause()
        else if (current.isPaused) resume()
    }

    fun skipNext() {
        val id = pendingId
        val finishedCallback = pendingOnFinished
        val skipCallback = onSkipNext
        if (id != null && finishedCallback != null) {
            stopInternal()
            resetState()
            finishedCallback.invoke()
        } else {
            skipCallback?.invoke()
        }
    }

    fun stop() {
        stopInternal()
        resetState()
    }

    fun release() {
        stop()
        try {
            tts?.shutdown()
        } catch (_: Exception) {
        }
        tts = null
        ttsReady = false
    }

    private fun stopInternal() {
        pendingId = null
        pendingOnFinished = null
        activePausedTts = false
        releasePlayer()
        try {
            tts?.stop()
        } catch (_: Exception) {
        }
    }

    private fun resetState() {
        _state.value = AudioPlaybackState(
            mode = currentMode,
            reciter = currentReciter,
            recitationVolume = currentVolume
        )
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {
        }
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
    }

    companion object {
        private val AUDIO_URL_REGEX = Regex("https://cdn\\.islamic\\.network/quran/audio/\\d+/([^/]+)/(\\d+\\.mp3)")
    }
}
