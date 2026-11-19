package com.example.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlaybackState(
    val activeId: String? = null,
    val activeTitle: String = "",
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val errorMessage: String? = null
)

class QuranAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private val _state = MutableStateFlow(AudioPlaybackState())
    val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

    fun togglePlay(id: String, title: String, url: String) {
        val current = _state.value
        if (current.activeId == id && (current.isPlaying || current.isBuffering)) {
            stop()
            return
        }
        playUrl(id, title, url)
    }

    fun playUrl(id: String, title: String, url: String) {
        releasePlayer()
        _state.value = AudioPlaybackState(
            activeId = id,
            activeTitle = title,
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
                setDataSource(url)
                setOnPreparedListener { mp ->
                    mp.start()
                    _state.value = AudioPlaybackState(
                        activeId = id,
                        activeTitle = title,
                        isPlaying = true,
                        isBuffering = false,
                        errorMessage = null
                    )
                }
                setOnCompletionListener {
                    _state.value = AudioPlaybackState()
                    releasePlayer()
                }
                setOnErrorListener { _, _, _ ->
                    _state.value = AudioPlaybackState(
                        activeId = id,
                        activeTitle = title,
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "Audio stream unavailable offline. Connect to internet to play recitation."
                    )
                    releasePlayer()
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            _state.value = AudioPlaybackState(
                activeId = id,
                activeTitle = title,
                isPlaying = false,
                isBuffering = false,
                errorMessage = e.localizedMessage ?: "Unable to start audio playback."
            )
            releasePlayer()
        }
    }

    fun stop() {
        releasePlayer()
        _state.value = AudioPlaybackState()
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
}
