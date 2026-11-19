package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiNature
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.audio.AmbientSoundState
import com.example.audio.AudioPlaybackMode
import com.example.audio.AudioPlaybackState
import com.example.audio.QuranReciter
import com.example.model.AppLanguage
import com.example.model.HadithAuthenticity
import com.example.model.WorshipClassification
import com.example.ui.theme.arabicTextStyle
import com.example.ui.theme.urduTextStyle
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Subtle repeating 8-point Islamic geometric star lattice rendered natively on Canvas.
 */
fun Modifier.drawIslamicGeometricPattern(
    tint: Color,
    alpha: Float = 0.055f,
    cellSizeDp: Float = 64f
): Modifier = drawBehind {
    val step = cellSizeDp * density
    if (step <= 4f) return@drawBehind
    val stroke = Stroke(width = 1.1f * density)
    val strokeColor = tint.copy(alpha = alpha)

    val cols = (size.width / step).toInt() + 2
    val rows = (size.height / step).toInt() + 2
    val rOuter = step * 0.34f
    val rInner = step * 0.15f

    for (row in 0..rows) {
        for (col in 0..cols) {
            val cx = col * step
            val cy = row * step
            val path = Path()
            val points = 16
            for (i in 0 until points) {
                val angle = (Math.PI / 8.0) * i
                val r = if (i % 2 == 0) rOuter else rInner
                val x = cx + (r * cos(angle)).toFloat()
                val y = cy + (r * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path = path, color = strokeColor, style = stroke)
            drawCircle(
                color = strokeColor,
                radius = rInner * 0.45f,
                center = Offset(cx, cy),
                style = stroke
            )
        }
    }
}

@Composable
fun NoorScreenHeader(
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Navigate back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        trailing?.invoke()
    }
}

@Composable
fun ArabicScriptText(
    arabic: String,
    fontScale: Float = 1.1f,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.End
) {
    if (arabic.isBlank()) return
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Text(
            text = arabic,
            style = arabicTextStyle(24f * fontScale),
            color = color,
            textAlign = textAlign,
            modifier = modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TrilingualTextBlock(
    language: AppLanguage,
    romanUrdu: String,
    urduTranslation: String,
    englishTranslation: String,
    fontScale: Float = 1.0f,
    showAllLanguages: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        when (language) {
            AppLanguage.URDU -> {
                if (urduTranslation.isNotBlank()) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = urduTranslation,
                            style = urduTextStyle(16f * fontScale),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if (showAllLanguages && romanUrdu.isNotBlank()) {
                    Text(
                        text = romanUrdu,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontScale
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (showAllLanguages && englishTranslation.isNotBlank()) {
                    Text(
                        text = englishTranslation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontScale
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            AppLanguage.ROMAN_URDU -> {
                if (romanUrdu.isNotBlank()) {
                    Text(
                        text = romanUrdu,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize * fontScale
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (showAllLanguages && urduTranslation.isNotBlank()) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = urduTranslation,
                            style = urduTextStyle(15f * fontScale),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if (showAllLanguages && englishTranslation.isNotBlank()) {
                    Text(
                        text = englishTranslation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontScale
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            AppLanguage.ENGLISH -> {
                if (englishTranslation.isNotBlank()) {
                    Text(
                        text = englishTranslation,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize * fontScale
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (showAllLanguages && romanUrdu.isNotBlank()) {
                    Text(
                        text = romanUrdu,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontScale
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (showAllLanguages && urduTranslation.isNotBlank()) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = urduTranslation,
                            style = urduTextStyle(15f * fontScale),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClassificationBadge(
    classification: WorshipClassification,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (classification) {
        WorshipClassification.OBLIGATORY ->
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        WorshipClassification.SUNNAH_MUAKKADAH ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        WorshipClassification.RECOMMENDED_NAFL ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        WorshipClassification.GOOD_DEED_ADAB ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(999.dp),
        modifier = modifier
    ) {
        Text(
            text = classification.label(language),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AuthenticityBadge(
    authenticity: HadithAuthenticity,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (authenticity) {
        HadithAuthenticity.AGREED_UPON, HadithAuthenticity.SAHIH ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        HadithAuthenticity.HASAN ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        HadithAuthenticity.DAIF, HadithAuthenticity.DISPUTED ->
            MaterialTheme.colorScheme.error.copy(alpha = 0.14f) to MaterialTheme.colorScheme.error
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(999.dp),
        modifier = modifier
    ) {
        Text(
            text = authenticity.label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AudioPlaybackBanner(
    audioState: AudioPlaybackState,
    ambientState: AmbientSoundState = AmbientSoundState(),
    onStop: () -> Unit,
    onTogglePlayPause: (() -> Unit)? = null,
    onCycleMode: (() -> Unit)? = null,
    onOpenAudioStudio: (() -> Unit)? = null,
    onStopAmbient: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val hasRecitationBanner = audioState.isPlaying || audioState.isPaused || audioState.isBuffering || audioState.errorMessage != null
    val hasAmbientBanner = ambientState.hasActiveSound
    if (!hasRecitationBanner && !hasAmbientBanner) return

    Column(modifier = modifier.fillMaxWidth()) {
        if (hasRecitationBanner) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (audioState.isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        Icon(
                            imageVector = if (audioState.isPlaying) Icons.Rounded.GraphicEq else Icons.Rounded.Pause,
                            contentDescription = "Audio recitation status",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = audioState.activeTitle.ifBlank { "Recitation Audio" },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = audioState.errorMessage
                                ?: if (audioState.isBuffering) "Buffering (${audioState.reciter.displayName}): ${audioState.activePhaseLabel}"
                                else if (audioState.isPaused) "Paused • ${audioState.activePhaseLabel.ifBlank { audioState.mode.label }} • ${audioState.reciter.displayName}"
                                else "${audioState.activePhaseLabel.ifBlank { audioState.mode.label }} • ${audioState.reciter.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.88f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (onCycleMode != null) {
                        Surface(
                            onClick = onCycleMode,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier.padding(horizontal = 4.dp).testTag("audio_mode_cycle_btn")
                        ) {
                            Text(
                                text = audioState.mode.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (onTogglePlayPause != null && (audioState.isPlaying || audioState.isPaused)) {
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("audio_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (audioState.isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                                contentDescription = if (audioState.isPaused) "Resume audio" else "Pause audio",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    if (onOpenAudioStudio != null) {
                        IconButton(
                            onClick = onOpenAudioStudio,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("audio_studio_banner_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Tune,
                                contentDescription = "Reciter and nature ambience mixer",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    IconButton(
                        onClick = onStop,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("audio_stop_button")
                    ) {
                        Icon(
                            imageVector = if (audioState.errorMessage != null) Icons.Rounded.Close else Icons.Rounded.Stop,
                            contentDescription = "Stop audio",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        if (hasAmbientBanner) {
            val activeParts = buildList {
                if (ambientState.birdsVolume > 0.01f) add("Birds ${(ambientState.birdsVolume * 100).roundToInt()}%")
                if (ambientState.rainVolume > 0.01f) add("Rain ${(ambientState.rainVolume * 100).roundToInt()}%")
                if (ambientState.airVolume > 0.01f) add("Air ${(ambientState.airVolume * 100).roundToInt()}%")
            }.joinToString(" • ")

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable(enabled = onOpenAudioStudio != null) { onOpenAudioStudio?.invoke() }
                    .testTag("ambient_active_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiNature,
                        contentDescription = "Nature ambience active",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nature Ambience: $activeParts",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (onOpenAudioStudio != null) {
                        TextButton(
                            onClick = onOpenAudioStudio,
                            modifier = Modifier.testTag("ambient_banner_adjust_btn")
                        ) {
                            Text("Mixer", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    if (onStopAmbient != null) {
                        IconButton(
                            onClick = onStopAmbient,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("ambient_banner_stop_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.VolumeOff,
                                contentDescription = "Turn off nature sounds",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AudioAndAmbienceSheet(
    audioState: AudioPlaybackState,
    ambientState: AmbientSoundState,
    autoPlayNextAyah: Boolean = true,
    onSelectReciter: (QuranReciter) -> Unit,
    onSelectAudioMode: (AudioPlaybackMode) -> Unit,
    onSetRecitationVolume: (Float) -> Unit,
    onToggleAutoPlayNextAyah: (Boolean) -> Unit = {},
    onUpdateAmbientSound: (enabled: Boolean, birds: Float, rain: Float, air: Float) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reciter & Sound Ambience",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Select your preferred Qari and mix peaceful nature sounds alongside recitation or quiet reading.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close audio mixer")
                }
            }

            // 1. Quran Reciter Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.RecordVoiceOver,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quran Reciter (Qari)",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuranReciter.entries.forEach { reciter ->
                        val selected = audioState.reciter == reciter
                        Surface(
                            onClick = { onSelectReciter(reciter) },
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(
                                1.dp,
                                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reciter_option_${reciter.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reciter.displayName,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = reciter.styleNote,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = "Selected reciter",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            // 2. Audio Language Mode & Recitation Volume
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Audio Language & Volume", style = MaterialTheme.typography.titleMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AudioPlaybackMode.entries.forEach { mode ->
                        FilterChip(
                            selected = audioState.mode == mode,
                            onClick = { onSelectAudioMode(mode) },
                            label = { Text(mode.label) },
                            modifier = Modifier.testTag("studio_mode_${mode.name.lowercase()}")
                        )
                    }
                }

                AmbientVolumeSliderRow(
                    icon = Icons.Rounded.VolumeUp,
                    label = "Recitation & Urdu Volume",
                    volume = audioState.recitationVolume,
                    sliderTag = "recitation_volume_slider",
                    onVolumeChange = onSetRecitationVolume
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Play Next Ayah", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Automatically start the next verse when current Ayah finishes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = autoPlayNextAyah,
                        onCheckedChange = onToggleAutoPlayNextAyah,
                        modifier = Modifier.testTag("studio_auto_next_switch")
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            // 3. Background Nature Sounds Mixer (Birds, Rain, Air)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Background Nature Sounds",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Works offline • Mix Birds, Rain & Gentle Air",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = ambientState.isEnabled,
                            onCheckedChange = { enabled ->
                                onUpdateAmbientSound(
                                    enabled,
                                    ambientState.birdsVolume,
                                    ambientState.rainVolume,
                                    ambientState.airVolume
                                )
                            },
                            modifier = Modifier.testTag("ambient_master_switch")
                        )
                    }

                    AmbientVolumeSliderRow(
                        icon = Icons.Rounded.EmojiNature,
                        label = "Birds Chirping",
                        volume = ambientState.birdsVolume,
                        sliderTag = "birds_volume_slider",
                        onVolumeChange = { v ->
                            onUpdateAmbientSound(
                                if (v > 0.01f) true else ambientState.isEnabled,
                                v,
                                ambientState.rainVolume,
                                ambientState.airVolume
                            )
                        }
                    )

                    AmbientVolumeSliderRow(
                        icon = Icons.Rounded.WaterDrop,
                        label = "Soft Rain",
                        volume = ambientState.rainVolume,
                        sliderTag = "rain_volume_slider",
                        onVolumeChange = { v ->
                            onUpdateAmbientSound(
                                if (v > 0.01f) true else ambientState.isEnabled,
                                ambientState.birdsVolume,
                                v,
                                ambientState.airVolume
                            )
                        }
                    )

                    AmbientVolumeSliderRow(
                        icon = Icons.Rounded.Air,
                        label = "Gentle Air / Breeze",
                        volume = ambientState.airVolume,
                        sliderTag = "air_volume_slider",
                        onVolumeChange = { v ->
                            onUpdateAmbientSound(
                                if (v > 0.01f) true else ambientState.isEnabled,
                                ambientState.birdsVolume,
                                ambientState.rainVolume,
                                v
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun AmbientVolumeSliderRow(
    icon: ImageVector,
    label: String,
    volume: Float,
    sliderTag: String,
    onVolumeChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (volume > 0.01f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = "${(volume * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth().testTag(sliderTag)
        )
    }
}

fun copyIslamicText(context: Context, label: String, content: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, content))
    Toast.makeText(context, "Copied $label to clipboard", Toast.LENGTH_SHORT).show()
}

fun shareIslamicText(context: Context, title: String, body: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$title\n\n$body\n\n— Shared from Noor-e-Zindagi")
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}
