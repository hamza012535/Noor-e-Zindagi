package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.audio.AudioPlaybackState
import com.example.data.corpus.AzkarDuaData
import com.example.data.corpus.HadithLibraryData
import com.example.data.corpus.QuranCatalogData
import com.example.data.corpus.SunnahRoutineRepository
import com.example.model.localized
import com.example.ui.components.ArabicScriptText
import com.example.ui.components.AuthenticityBadge
import com.example.ui.components.TrilingualTextBlock
import com.example.ui.components.drawIslamicGeometricPattern
import com.example.ui.navigation.NoorRoutes
import com.example.ui.viewmodel.NoorUiSnapshot
import java.util.Calendar

private data class QuickTileData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val testTag: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeDashboardScreen(
    uiState: NoorUiSnapshot,
    userDisplayName: String,
    audioState: AudioPlaybackState,
    onToggleChecklist: (String) -> Unit,
    onPlayAudio: (String, String, String) -> Unit,
    onNavigateTab: (String) -> Unit,
    onOpenPrayerTimes: () -> Unit,
    onOpenHadith: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenAiAssistant: () -> Unit,
    onOpenMediaOcr: () -> Unit
) {
    val dayOfYear = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) }
    val dailyAyah = remember(dayOfYear) { QuranCatalogData.getDailyAyah(dayOfYear) }
    val dailyHadith = remember(dayOfYear) { HadithLibraryData.dailyHadith(dayOfYear) }
    val dailyDua = remember(dayOfYear) { AzkarDuaData.dailyDua(dayOfYear) }
    val lang = uiState.prefs.language
    val schedule = uiState.prayerSchedule

    val quickTiles = listOf(
        QuickTileData("Prayer Times", "Salah & Qibla times", Icons.Rounded.AccessTime, "quick_prayer_times", onOpenPrayerTimes),
        QuickTileData("Hadith Library", "7 authentic books", Icons.AutoMirrored.Rounded.MenuBook, "quick_hadith", onOpenHadith),
        QuickTileData("Daily Duas", "14 Azkar categories", Icons.Rounded.Favorite, "quick_daily_dua") { onNavigateTab(NoorRoutes.AZKAR) },
        QuickTileData("Hijri Calendar", "Occasions & fasting", Icons.Rounded.CalendarMonth, "quick_calendar", onOpenCalendar),
        QuickTileData("Sunnah Routine", "Dawn to bedtime", Icons.Rounded.Checklist, "quick_sunnah") { onNavigateTab(NoorRoutes.SUNNAH) },
        QuickTileData("Quran Reader", "114 Surahs & words", Icons.Rounded.AutoStories, "quick_quran") { onNavigateTab(NoorRoutes.QURAN) },
        QuickTileData("Visual & OCR", "Scan & verify text", Icons.Rounded.DocumentScanner, "quick_ocr", onOpenMediaOcr),
        QuickTileData("Islamic AI", "Verified guidance", Icons.Rounded.AutoAwesome, "quick_ai", onOpenAiAssistant)
    )

    val previewChecklist = remember { SunnahRoutineRepository.coreChecklistActivities.take(5) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .drawIslamicGeometricPattern(MaterialTheme.colorScheme.primary, alpha = 0.035f),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Welcome & Prayer Countdown Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 720.dp)
                    .clip(MaterialTheme.shapes.large)
            ) {
                Image(
                    painter = painterResource(R.drawable.img_mosque_hero),
                    contentDescription = "Noor-e-Zindagi mosque banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.48f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.94f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Assalamu Alaikum, $userDisplayName",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = schedule?.hijriFormatted ?: "Calculating Hijri Date...",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            )
                            Text(
                                text = "${schedule?.dateGregorian ?: ""} • ${schedule?.locationName ?: uiState.prefs.locationName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        }

                        // Daily Progress Ring
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                            CircularProgressIndicator(
                                progress = { uiState.habitStats.todayPercent / 100f },
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                strokeWidth = 6.dp
                            )
                            Text(
                                text = "${uiState.habitStats.todayPercent}%",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.14f),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenPrayerTimes)
                            .testTag("hero_next_prayer_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "NEXT PRAYER",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                                Text(
                                    text = uiState.nextPrayerLabel,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = uiState.nextPrayerCountdown,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${uiState.habitStats.currentStreakDays}d streak",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Quick Access Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Quick Access",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    maxItemsInEachRow = 4
                ) {
                    quickTiles.forEach { tile ->
                        Card(
                            onClick = tile.onClick,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .widthIn(min = 152.dp, max = 220.dp)
                                .testTag(tile.testTag)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = tile.icon,
                                            contentDescription = tile.title,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = tile.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = tile.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Today's Sunnah Checklist Preview
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Today's Sunnah Checklist", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "${uiState.completedIdsToday.size} of ${SunnahRoutineRepository.coreChecklistActivities.size + uiState.customActivities.size} completed",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(
                            onClick = { onNavigateTab(NoorRoutes.SUNNAH) },
                            modifier = Modifier.testTag("home_open_full_checklist")
                        ) {
                            Text("Full Routine")
                        }
                    }
                    previewChecklist.forEach { act ->
                        val checked = act.id in uiState.completedIdsToday
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .clickable { onToggleChecklist(act.id) }
                                .padding(vertical = 6.dp, horizontal = 4.dp)
                                .testTag("home_check_${act.id}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (checked) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = if (checked) "Completed" else "Mark complete",
                                tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = localized(lang, act.titleEn, act.titleUr, act.titleRoman),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${act.recommendedTime} • ${act.reference}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Daily Quran Ayah
        item {
            val ayahAudioId = "daily_ayah_${dailyAyah.surahNumber}_${dailyAyah.ayahNumber}"
            val isPlaying = audioState.activeId == ayahAudioId && audioState.isPlaying
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f)),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Daily Quran Ayah", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "Surah ${QuranCatalogData.getSurahInfo(dailyAyah.surahNumber).nameEnglish} (${dailyAyah.surahNumber}:${dailyAyah.ayahNumber})",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
                                onPlayAudio(
                                    ayahAudioId,
                                    "Surah ${dailyAyah.surahNumber}:${dailyAyah.ayahNumber}",
                                    dailyAyah.audioUrl
                                )
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("home_daily_ayah_audio")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                                contentDescription = "Play Ayah audio",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    ArabicScriptText(
                        arabic = dailyAyah.arabicUthmani,
                        fontScale = uiState.prefs.arabicFontScale
                    )
                    TrilingualTextBlock(
                        language = lang,
                        romanUrdu = dailyAyah.romanUrdu,
                        urduTranslation = dailyAyah.urduTranslation,
                        englishTranslation = dailyAyah.englishTranslation,
                        fontScale = uiState.prefs.translationFontScale
                    )
                    Text(
                        text = "${dailyAyah.translatorEn} • ${dailyAyah.translatorUr}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 5. Daily Hadith
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daily Hadith", style = MaterialTheme.typography.titleLarge)
                        AuthenticityBadge(dailyHadith.authenticity)
                    }
                    ArabicScriptText(
                        arabic = dailyHadith.arabicText,
                        fontScale = uiState.prefs.arabicFontScale * 0.92f
                    )
                    TrilingualTextBlock(
                        language = lang,
                        romanUrdu = dailyHadith.romanUrdu,
                        urduTranslation = dailyHadith.urduTranslation,
                        englishTranslation = dailyHadith.englishTranslation,
                        fontScale = uiState.prefs.translationFontScale
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${dailyHadith.bookName} #${dailyHadith.hadithNumber}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        FilledTonalButton(
                            onClick = onOpenHadith,
                            modifier = Modifier.testTag("home_more_hadith_btn")
                        ) {
                            Text("Explore Hadith")
                        }
                    }
                }
            }
        }

        // 6. Daily Dua
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Daily Dua", style = MaterialTheme.typography.titleLarge)
                            Text(
                                localized(lang, dailyDua.titleEn, dailyDua.titleUr, dailyDua.titleRoman),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        TextButton(onClick = { onNavigateTab(NoorRoutes.AZKAR) }) {
                            Text("All Azkar")
                        }
                    }
                    ArabicScriptText(arabic = dailyDua.arabicText, fontScale = uiState.prefs.arabicFontScale)
                    TrilingualTextBlock(
                        language = lang,
                        romanUrdu = dailyDua.romanUrdu,
                        urduTranslation = dailyDua.urduTranslation,
                        englishTranslation = dailyDua.englishTranslation,
                        fontScale = uiState.prefs.translationFontScale
                    )
                    Text(
                        text = "${dailyDua.reference} • ${dailyDua.whenToRead}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 7. Approved Community Gems (from Firestore Admin-reviewed feed)
        if (uiState.approvedCommunityContent.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Admin-Verified Community Additions",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        uiState.approvedCommunityContent.take(3).forEach { doc ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(doc.title, style = MaterialTheme.typography.titleMedium)
                                    if (doc.arabicText.isNotBlank()) {
                                        ArabicScriptText(doc.arabicText, fontScale = 0.95f)
                                    }
                                    if (doc.englishTranslation.isNotBlank()) {
                                        Text(doc.englishTranslation, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Text(
                                        "Ref: ${doc.referenceSource} • Verified",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Inspiring Footer
        item {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"Every day, learn one Sunnah, practise it, and grow closer to Allah.\"",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
