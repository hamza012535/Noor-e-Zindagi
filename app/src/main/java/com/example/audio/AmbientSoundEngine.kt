package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.tanh
import kotlin.random.Random

data class AmbientSoundState(
    val isEnabled: Boolean = false,
    val birdsVolume: Float = 0.35f,
    val rainVolume: Float = 0.45f,
    val airVolume: Float = 0.30f
) {
    val hasActiveSound: Boolean
        get() = isEnabled && (birdsVolume > 0.01f || rainVolume > 0.01f || airVolume > 0.01f)
}

/**
 * Pure offline procedural nature sound synthesizer (Birds, Rain, Gentle Air/Breeze)
 * mixed in real time via Android AudioTrack with independent channel volumes.
 */
class AmbientSoundEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var renderJob: Job? = null

    @Volatile private var birdsGain: Float = 0f
    @Volatile private var rainGain: Float = 0f
    @Volatile private var airGain: Float = 0f
    @Volatile private var enabled: Boolean = false

    fun updateSettings(state: AmbientSoundState) {
        enabled = state.isEnabled
        birdsGain = state.birdsVolume.coerceIn(0f, 1f)
        rainGain = state.rainVolume.coerceIn(0f, 1f)
        airGain = state.airVolume.coerceIn(0f, 1f)

        if (state.hasActiveSound) {
            ensureRunning()
        } else {
            stop()
        }
    }

    @Synchronized
    private fun ensureRunning() {
        if (renderJob?.isActive == true) return
        renderJob = scope.launch {
            val sampleRate = 22050
            val minBufBytes = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            val track = try {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufBytes)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } catch (_: Exception) {
                return@launch
            }

            try {
                track.play()
                val chunkSamples = 1024
                val buffer = ShortArray(chunkSamples)

                // Pink/Brown filter states for Rain & Air
                var b0 = 0.0; var b1 = 0.0; var b2 = 0.0
                var rainLp = 0.0
                var airLp1 = 0.0; var airLp2 = 0.0
                var windPhase = 0.0

                // Water droplet plink state
                var dropSamplesLeft = 0
                var dropPhase = 0.0
                var dropFreq = 1400.0
                var dropAmp = 0.0

                // Bird chirp state
                var birdWaitSamples = sampleRate / 2
                var birdChirpSamplesLeft = 0
                var birdChirpTotal = 1
                var birdPhase = 0.0
                var birdBaseFreq = 2900.0
                var birdSweep = 700.0
                var birdSubNotes = 0

                val rng = Random(System.currentTimeMillis())

                while (isActive && enabled) {
                    val bVol = birdsGain
                    val rVol = rainGain
                    val aVol = airGain
                    if (bVol <= 0.01f && rVol <= 0.01f && aVol <= 0.01f) break

                    for (i in 0 until chunkSamples) {
                        val white = rng.nextDouble(-1.0, 1.0)

                        // 1. Rain: soft pink-ish filtered steady patter + occasional gentle droplets
                        var rainSample = 0.0
                        if (rVol > 0.01f) {
                            b0 = 0.997 * b0 + white * 0.029
                            b1 = 0.985 * b1 + white * 0.032
                            b2 = 0.950 * b2 + white * 0.048
                            val pink = (b0 + b1 + b2 + white * 0.14) * 0.45
                            rainLp = rainLp * 0.65 + pink * 0.35

                            if (dropSamplesLeft <= 0 && rng.nextInt(1400) == 0) {
                                dropSamplesLeft = rng.nextInt(260, 620)
                                dropFreq = rng.nextDouble(950.0, 2200.0)
                                dropPhase = 0.0
                                dropAmp = rng.nextDouble(0.06, 0.14)
                            }
                            var drop = 0.0
                            if (dropSamplesLeft > 0) {
                                dropPhase += 2.0 * PI * dropFreq / sampleRate
                                drop = sin(dropPhase) * dropAmp
                                dropAmp *= 0.985
                                dropFreq *= 1.0005
                                dropSamplesLeft--
                            }
                            rainSample = (rainLp + drop) * rVol * 0.85
                        }

                        // 2. Air / Breeze: smooth low-frequency wind swell
                        var airSample = 0.0
                        if (aVol > 0.01f) {
                            airLp1 = airLp1 * 0.94 + white * 0.06
                            airLp2 = airLp2 * 0.95 + airLp1 * 0.05
                            windPhase += 2.0 * PI * 0.13 / sampleRate
                            val gust = 0.55 + 0.45 * sin(windPhase)
                            airSample = airLp2 * 3.8 * gust * aVol
                        }

                        // 3. Birds: gentle melodic morning chirps spaced naturally
                        var birdSample = 0.0
                        if (bVol > 0.01f) {
                            if (birdChirpSamplesLeft > 0) {
                                val progress = 1.0 - (birdChirpSamplesLeft.toDouble() / birdChirpTotal)
                                val env = sin(PI * progress) // Smooth bell envelope
                                val freq = birdBaseFreq + birdSweep * sin(PI * progress * 1.3)
                                birdPhase += 2.0 * PI * freq / sampleRate
                                birdSample = (sin(birdPhase) * 0.72 + sin(birdPhase * 2.0) * 0.18) * env * 0.24 * bVol
                                birdChirpSamplesLeft--
                            } else if (birdWaitSamples > 0) {
                                birdWaitSamples--
                            } else {
                                if (birdSubNotes > 0) {
                                    birdSubNotes--
                                    birdChirpTotal = rng.nextInt(1400, 2900)
                                    birdChirpSamplesLeft = birdChirpTotal
                                    birdWaitSamples = rng.nextInt(900, 2400)
                                    birdBaseFreq = rng.nextDouble(2300.0, 3600.0)
                                    birdSweep = rng.nextDouble(-650.0, 900.0)
                                } else {
                                    birdSubNotes = rng.nextInt(1, 4)
                                    birdWaitSamples = rng.nextInt(sampleRate, sampleRate * 3)
                                }
                            }
                        }

                        val mixed = tanh((rainSample + airSample + birdSample) * 0.95)
                        buffer[i] = (mixed * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            .toShort()
                    }

                    track.write(buffer, 0, chunkSamples)
                }
            } catch (_: Exception) {
            } finally {
                try {
                    track.pause()
                    track.flush()
                    track.release()
                } catch (_: Exception) {
                }
            }
        }
    }

    @Synchronized
    fun stop() {
        enabled = false
        renderJob?.cancel()
        renderJob = null
    }
}
