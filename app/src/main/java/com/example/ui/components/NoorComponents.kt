package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.audio.AudioPlaybackState
import com.example.model.AppLanguage
import com.example.model.HadithAuthenticity
import com.example.model.WorshipClassification
import com.example.ui.theme.arabicTextStyle
import com.example.ui.theme.urduTextStyle
import kotlin.math.cos
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
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!audioState.isPlaying && !audioState.isBuffering && audioState.errorMessage == null) return

    Card(
        modifier = modifier
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
                        ?: if (audioState.isBuffering) "Buffering verified audio..." else "Playing Mishary Rashid Alafasy",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
