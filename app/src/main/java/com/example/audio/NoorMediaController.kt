package com.example.audio

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Application-scoped media coordinator shared between UI/ViewModel and NoorMediaPlaybackService
 * so lock-screen MediaSession controls and background playback stay synchronized.
 */
class NoorMediaController private constructor(private val appContext: Context) {

    val audioPlayer = QuranAudioPlayer(appContext)
    val ambientEngine = AmbientSoundEngine()

    private val _ambientState = MutableStateFlow(AmbientSoundState())
    val ambientState: StateFlow<AmbientSoundState> = _ambientState.asStateFlow()

    fun updateAmbientState(next: AmbientSoundState) {
        _ambientState.value = next
        ambientEngine.updateSettings(next)
        syncForegroundService()
    }

    fun syncForegroundService() {
        val audio = audioPlayer.state.value
        val shouldRun = audio.isPlaying || audio.isBuffering || audio.isPaused || _ambientState.value.hasActiveSound
        if (shouldRun) {
            try {
                ContextCompat.startForegroundService(
                    appContext,
                    Intent(appContext, NoorMediaPlaybackService::class.java)
                )
            } catch (_: Exception) {
            }
        }
    }

    fun togglePlayPauseFromMediaSession() {
        val audio = audioPlayer.state.value
        val amb = _ambientState.value
        when {
            audio.isPlaying -> audioPlayer.pause()
            audio.isPaused -> audioPlayer.resume()
            amb.hasActiveSound -> updateAmbientState(amb.copy(isEnabled = false))
            !amb.isEnabled && (amb.birdsVolume > 0.01f || amb.rainVolume > 0.01f || amb.airVolume > 0.01f) ->
                updateAmbientState(amb.copy(isEnabled = true))
        }
        syncForegroundService()
    }

    fun pauseFromMediaSession() {
        val audio = audioPlayer.state.value
        if (audio.isPlaying) {
            audioPlayer.pause()
        } else if (_ambientState.value.hasActiveSound) {
            updateAmbientState(_ambientState.value.copy(isEnabled = false))
        }
        syncForegroundService()
    }

    fun resumeFromMediaSession() {
        val audio = audioPlayer.state.value
        if (audio.isPaused) {
            audioPlayer.resume()
        } else {
            val amb = _ambientState.value
            if (!amb.isEnabled && (amb.birdsVolume > 0.01f || amb.rainVolume > 0.01f || amb.airVolume > 0.01f)) {
                updateAmbientState(amb.copy(isEnabled = true))
            }
        }
        syncForegroundService()
    }

    fun skipNextFromMediaSession() {
        audioPlayer.skipNext()
        syncForegroundService()
    }

    fun stopAllFromMediaSession() {
        audioPlayer.stop()
        if (_ambientState.value.isEnabled) {
            updateAmbientState(_ambientState.value.copy(isEnabled = false))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: NoorMediaController? = null

        fun getInstance(context: Context): NoorMediaController {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NoorMediaController(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
