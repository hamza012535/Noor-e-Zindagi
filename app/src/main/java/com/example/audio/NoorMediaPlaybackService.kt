package com.example.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class NoorMediaPlaybackService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var noorController: NoorMediaController
    private lateinit var mediaSession: MediaSessionCompat
    private var stateJob: Job? = null

    private val artworkBitmap by lazy {
        try {
            BitmapFactory.decodeResource(resources, R.drawable.img_mosque_hero)
        } catch (_: Exception) {
            null
        }
    }

    override fun onCreate() {
        super.onCreate()
        noorController = NoorMediaController.getInstance(applicationContext)
        ensureNotificationChannel()

        mediaSession = MediaSessionCompat(this, "NoorEZindagiMediaSession").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    noorController.resumeFromMediaSession()
                }

                override fun onPause() {
                    noorController.pauseFromMediaSession()
                }

                override fun onSkipToNext() {
                    noorController.skipNextFromMediaSession()
                }

                override fun onStop() {
                    noorController.stopAllFromMediaSession()
                    stopPlaybackForeground()
                }
            })
            isActive = true
        }

        // Immediately enter foreground on creation to honor Android 8+/14+ startForegroundService contract
        val initialNotif = buildMediaNotification(
            noorController.audioPlayer.state.value,
            noorController.ambientState.value
        )
        startMediaForeground(initialNotif)

        stateJob = serviceScope.launch {
            combine(
                noorController.audioPlayer.state,
                noorController.ambientState
            ) { audio, ambient -> audio to ambient }
                .collect { (audio, ambient) ->
                    val active = audio.isPlaying || audio.isBuffering || audio.isPaused || ambient.hasActiveSound
                    if (!active) {
                        stopPlaybackForeground()
                    } else {
                        updateSessionState(audio, ambient)
                        val notification = buildMediaNotification(audio, ambient)
                        startMediaForeground(notification)
                    }
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            MediaButtonReceiver.handleIntent(mediaSession, intent)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stateJob?.cancel()
        serviceScope.cancel()
        mediaSession.isActive = false
        mediaSession.release()
        super.onDestroy()
    }

    private fun updateSessionState(audio: AudioPlaybackState, ambient: AmbientSoundState) {
        val isPlayingNow = audio.isPlaying || (!audio.isPaused && !audio.isBuffering && ambient.hasActiveSound)
        val pbState = when {
            audio.isBuffering -> PlaybackStateCompat.STATE_BUFFERING
            isPlayingNow -> PlaybackStateCompat.STATE_PLAYING
            audio.isPaused -> PlaybackStateCompat.STATE_PAUSED
            else -> PlaybackStateCompat.STATE_STOPPED
        }

        val actions = PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_PLAY_PAUSE or
            PlaybackStateCompat.ACTION_STOP or
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT

        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(actions)
                .setState(pbState, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)
                .build()
        )

        val title = when {
            audio.activeTitle.isNotBlank() -> audio.activeTitle
            ambient.hasActiveSound -> "Nature Ambience"
            else -> "Noor-e-Zindagi"
        }

        val subtitle = buildString {
            if (audio.activeTitle.isNotBlank()) {
                append(audio.reciter.displayName)
                if (audio.activePhaseLabel.isNotBlank()) {
                    append(" • ")
                    append(audio.activePhaseLabel)
                }
            }
            if (ambient.hasActiveSound) {
                if (isNotEmpty()) append(" | ")
                val parts = buildList {
                    if (ambient.birdsVolume > 0.01f) add("Birds")
                    if (ambient.rainVolume > 0.01f) add("Rain")
                    if (ambient.airVolume > 0.01f) add("Air")
                }
                append("Ambience: ${parts.joinToString(", ")}")
            }
            if (isEmpty()) append("Quran & Daily Sunnah Companion")
        }

        val metaBuilder = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, subtitle)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Noor-e-Zindagi")

        artworkBitmap?.let { bmp ->
            metaBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bmp)
        }
        mediaSession.setMetadata(metaBuilder.build())
    }

    private fun buildMediaNotification(
        audio: AudioPlaybackState,
        ambient: AmbientSoundState
    ): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            2001,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isPlayingNow = audio.isPlaying || (!audio.isPaused && !audio.isBuffering && ambient.hasActiveSound)
        val playPauseAction = NotificationCompat.Action(
            if (isPlayingNow) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
            if (isPlayingNow) "Pause" else "Play",
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this,
                PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
        )
        val nextAction = NotificationCompat.Action(
            android.R.drawable.ic_media_next,
            "Next Ayah",
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
        )
        val stopAction = NotificationCompat.Action(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Stop",
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this,
                PlaybackStateCompat.ACTION_STOP
            )
        )

        val title = audio.activeTitle.ifBlank {
            if (ambient.hasActiveSound) "Peaceful Nature Ambience" else "Noor-e-Zindagi Audio"
        }
        val contentText = buildString {
            if (audio.isBuffering) {
                append("Buffering ${audio.reciter.displayName}...")
            } else if (audio.activeTitle.isNotBlank()) {
                append("${audio.reciter.displayName} • ${audio.mode.label}")
            }
            if (ambient.hasActiveSound) {
                if (isNotEmpty()) append(" • ")
                append("Nature On")
            }
        }.ifBlank { "Listening in background" }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(artworkBitmap)
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(contentPendingIntent)
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(isPlayingNow)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .addAction(stopAction)
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .build()
    }

    private fun startMediaForeground(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                MEDIA_NOTIF_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(MEDIA_NOTIF_ID, notification)
        }
    }

    private fun stopPlaybackForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quran & Ambience Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Lock-screen and notification media controls for Quran recitation and nature sounds."
                setShowBadge(false)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "noor_media_playback_channel"
        const val MEDIA_NOTIF_ID = 4041
    }
}
