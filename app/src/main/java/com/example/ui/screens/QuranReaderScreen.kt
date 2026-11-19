package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.ListAlt
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.AudioPlaybackState
import com.example.data.corpus.QuranCatalogData
import com.example.model.AyahData
import com.example.model.WordMeaning
import com.example.ui.components.ArabicScriptText
import com.example.ui.components.TrilingualTextBlock
import com.example.ui.components.copyIslamicText
import com.example.ui.components.shareIslamicText
import com.example.ui.theme.arabicTextStyle
import com.example.ui.theme.urduTextStyle
import com.example.ui.viewmodel.NoorUiSnapshot
import com.example.ui.viewmodel.NoorViewModel
import com.example.ui.viewmodel.QuranReaderState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuranReaderScreen(
    viewModel: NoorViewModel,
    uiState: NoorUiSnapshot,
    audioState: AudioPlaybackState
) {
    val context = LocalContext.current
    val selectedSurahNum by viewModel.selectedSurah.collectAsStateWithLifecycle()
    val quranState by viewModel.quranState.collectAsStateWithLifecycle()
    val searchResults by viewModel.quranSearchResults.collectAsStateWithLifecycle()

    val surahInfo = remember(selectedSurahNum) { QuranCatalogData.getSurahInfo(selectedSurahNum) }
    var showSurahPicker by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedWord by remember { mutableStateOf<WordMeaning?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showSurahPicker = true }
                            .testTag("quran_surah_picker_trigger")
                    ) {
                        Text(
                            text = "${surahInfo.number}. ${surahInfo.nameEnglish} (${surahInfo.nameArabic})",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${surahInfo.meaningEn} • ${surahInfo.revelationType} • ${surahInfo.ayahCount} Ayahs (Tap to switch)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row {
                        IconButton(
                            onClick = { showSearchMode = !showSearchMode },
                            modifier = Modifier.minimumInteractiveComponentSize().testTag("quran_search_toggle")
                        ) {
                            Icon(Icons.Rounded.Search, contentDescription = "Search Quran verses")
                        }
                        IconButton(
                            onClick = { showSurahPicker = true },
                            modifier = Modifier.minimumInteractiveComponentSize().testTag("quran_surah_list_btn")
                        ) {
                            Icon(Icons.Rounded.ListAlt, contentDescription = "Choose Surah")
                        }
                        IconButton(
                            onClick = { showSettingsSheet = true },
                            modifier = Modifier.minimumInteractiveComponentSize().testTag("quran_reader_settings_btn")
                        ) {
                            Icon(Icons.Rounded.Tune, contentDescription = "Font size and reading mode")
                        }
                    }
                }

                // Last-read resume bar
                if (uiState.prefs.lastReadSurah != selectedSurahNum) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectSurah(uiState.prefs.lastReadSurah) }
                    ) {
                        Text(
                            text = "Resume Last-Read: Surah ${QuranCatalogData.getSurahInfo(uiState.prefs.lastReadSurah).nameEnglish} (${uiState.prefs.lastReadSurah}:${uiState.prefs.lastReadAyah})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                if (showSearchMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.searchQuranVerses(it)
                        },
                        label = { Text("Search Surah name or cached Ayah text") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("quran_search_input")
                    )
                }
            }
        }

        // Main Content Area
        if (showSearchMode && searchQuery.isNotBlank()) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "Search Results (${searchResults.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(searchResults, key = { "${it.surahNumber}_${it.ayahNumber}" }) { ayah ->
                    AyahCard(
                        ayah = ayah,
                        uiState = uiState,
                        audioState = audioState,
                        onWordTap = { selectedWord = it },
                        onPlayAudio = {
                            viewModel.toggleAudio(
                                "ayah_${ayah.surahNumber}_${ayah.ayahNumber}",
                                "Surah ${ayah.surahNumber}:${ayah.ayahNumber}",
                                ayah.audioUrl
                            )
                        },
                        onBookmark = {
                            viewModel.toggleBookmark(
                                itemType = "quran",
                                refId = "${ayah.surahNumber}:${ayah.ayahNumber}",
                                title = "Quran ${ayah.surahNumber}:${ayah.ayahNumber}",
                                arabicText = ayah.arabicUthmani,
                                subtitle = ayah.englishTranslation
                            )
                        },
                        onMarkLastRead = { viewModel.markLastRead(ayah.surahNumber, ayah.ayahNumber) },
                        onCopy = {
                            copyIslamicText(
                                context,
                                "Quran ${ayah.surahNumber}:${ayah.ayahNumber}",
                                "${ayah.arabicUthmani}\n\n${ayah.englishTranslation}\n\n${ayah.urduTranslation}"
                            )
                        },
                        onShare = {
                            shareIslamicText(
                                context,
                                "Quran ${ayah.surahNumber}:${ayah.ayahNumber}",
                                "${ayah.arabicUthmani}\n\n${ayah.englishTranslation}\n\n${ayah.urduTranslation}"
                            )
                        }
                    )
                }
            }
        } else {
            when (val state = quranState) {
                is QuranReaderState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Loading Surah ${surahInfo.nameEnglish}...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                is QuranReaderState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(state.message, style = MaterialTheme.typography.bodyLarge)
                            Button(onClick = { viewModel.loadSurah(selectedSurahNum) }) {
                                Text("Retry Download")
                            }
                        }
                    }
                }
                is QuranReaderState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Edition Attribution & Bismillah Header
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${surahInfo.nameEnglish} • ${surahInfo.nameArabic}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    if (surahInfo.number != 9 && surahInfo.number != 1) {
                                        ArabicScriptText(
                                            arabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                            fontScale = uiState.prefs.arabicFontScale,
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                    Text(
                                        text = QuranCatalogData.EDITION_ATTRIBUTION,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }

                        items(state.ayahs, key = { "${it.surahNumber}_${it.ayahNumber}" }) { ayah ->
                            AyahCard(
                                ayah = ayah,
                                uiState = uiState,
                                audioState = audioState,
                                onWordTap = { selectedWord = it },
                                onPlayAudio = {
                                    viewModel.toggleAudio(
                                        "ayah_${ayah.surahNumber}_${ayah.ayahNumber}",
                                        "Surah ${ayah.surahNumber}:${ayah.ayahNumber}",
                                        ayah.audioUrl
                                    )
                                },
                                onBookmark = {
                                    viewModel.toggleBookmark(
                                        itemType = "quran",
                                        refId = "${ayah.surahNumber}:${ayah.ayahNumber}",
                                        title = "Quran ${ayah.surahNumber}:${ayah.ayahNumber}",
                                        arabicText = ayah.arabicUthmani,
                                        subtitle = ayah.englishTranslation
                                    )
                                },
                                onMarkLastRead = { viewModel.markLastRead(ayah.surahNumber, ayah.ayahNumber) },
                                onCopy = {
                                    copyIslamicText(
                                        context,
                                        "Quran ${ayah.surahNumber}:${ayah.ayahNumber}",
                                        "${ayah.arabicUthmani}\n\n${ayah.englishTranslation}\n\n${ayah.urduTranslation}"
                                    )
                                },
                                onShare = {
                                    shareIslamicText(
                                        context,
                                        "Quran ${ayah.surahNumber}:${ayah.ayahNumber}",
                                        "${ayah.arabicUthmani}\n\n${ayah.englishTranslation}\n\n${ayah.urduTranslation}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Word-by-Word Details Bottom Sheet
    val wordToInspect = selectedWord
    if (wordToInspect != null) {
        ModalBottomSheet(onDismissRequest = { selectedWord = null }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Word-by-Word Meaning", style = MaterialTheme.typography.titleLarge)
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = wordToInspect.arabicWord,
                                style = arabicTextStyle(34f),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        if (wordToInspect.transliteration.isNotBlank()) {
                            Text(
                                text = wordToInspect.transliteration,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Text("English Meaning: ${wordToInspect.englishMeaning}", style = MaterialTheme.typography.bodyLarge)
                if (wordToInspect.urduMeaning.isNotBlank()) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = "اردو معنی: ${wordToInspect.urduMeaning}",
                            style = urduTextStyle(17f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if (wordToInspect.rootWord.isNotBlank()) {
                    Text("Arabic Root: ${wordToInspect.rootWord}", style = MaterialTheme.typography.bodyMedium)
                }
                if (wordToInspect.grammarInfo.isNotBlank()) {
                    Text("Grammar / Position: ${wordToInspect.grammarInfo}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // 114 Surah Picker Bottom Sheet
    if (showSurahPicker) {
        var surahFilter by remember { mutableStateOf("") }
        val filteredSurahs = remember(surahFilter) {
            if (surahFilter.isBlank()) QuranCatalogData.allSurahs
            else QuranCatalogData.allSurahs.filter {
                it.nameEnglish.contains(surahFilter, true) ||
                    it.meaningEn.contains(surahFilter, true) ||
                    it.number.toString() == surahFilter.trim()
            }
        }
        ModalBottomSheet(onDismissRequest = { showSurahPicker = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text("Select Surah (All 114 Surahs)", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = surahFilter,
                    onValueChange = { surahFilter = it },
                    label = { Text("Filter by number or name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.height(420.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredSurahs, key = { it.number }) { s ->
                        Surface(
                            color = if (s.number == selectedSurahNum) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectSurah(s.number)
                                    showSurahPicker = false
                                }
                                .testTag("surah_item_${s.number}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${s.number}. ${s.nameEnglish}", style = MaterialTheme.typography.titleMedium)
                                    Text("${s.meaningEn} • ${s.ayahCount} Ayahs", style = MaterialTheme.typography.labelSmall)
                                }
                                Text(s.nameArabic, style = arabicTextStyle(20f))
                            }
                        }
                    }
                }
            }
        }
    }

    // Font Size & Dark Reading Settings Sheet
    if (showSettingsSheet) {
        ModalBottomSheet(onDismissRequest = { showSettingsSheet = false }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Quran Reading Preferences", style = MaterialTheme.typography.titleLarge)
                Text("Arabic Font Scale: ${"%.1fx".format(uiState.prefs.arabicFontScale)}")
                Slider(
                    value = uiState.prefs.arabicFontScale,
                    onValueChange = { viewModel.setFontScales(it, uiState.prefs.translationFontScale) },
                    valueRange = 0.8f..1.9f
                )
                Text("Translation Font Scale: ${"%.1fx".format(uiState.prefs.translationFontScale)}")
                Slider(
                    value = uiState.prefs.translationFontScale,
                    onValueChange = { viewModel.setFontScales(uiState.prefs.arabicFontScale, it) },
                    valueRange = 0.8f..1.6f
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.DarkMode, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dark Reading Mode", style = MaterialTheme.typography.titleMedium)
                    }
                    Switch(
                        checked = uiState.prefs.darkReadingMode,
                        onCheckedChange = viewModel::setDarkReadingMode,
                        modifier = Modifier.testTag("quran_dark_mode_switch")
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AyahCard(
    ayah: AyahData,
    uiState: NoorUiSnapshot,
    audioState: AudioPlaybackState,
    onWordTap: (WordMeaning) -> Unit,
    onPlayAudio: () -> Unit,
    onBookmark: () -> Unit,
    onMarkLastRead: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val isBookmarked = uiState.bookmarks.any { it.itemType == "quran" && it.refId == "${ayah.surahNumber}:${ayah.ayahNumber}" }
    val audioId = "ayah_${ayah.surahNumber}_${ayah.ayahNumber}"
    val isPlaying = audioState.activeId == audioId && audioState.isPlaying

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
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = "Ayah ${ayah.surahNumber}:${ayah.ayahNumber}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Row {
                    IconButton(onClick = onPlayAudio, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                            contentDescription = "Play Ayah audio"
                        )
                    }
                    IconButton(onClick = onBookmark, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = "Bookmark Ayah",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onMarkLastRead, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Rounded.PushPin, contentDescription = "Set last read position")
                    }
                }
            }

            ArabicScriptText(
                arabic = ayah.arabicUthmani,
                fontScale = uiState.prefs.arabicFontScale
            )

            // Interactive Word-by-Word Chips
            if (ayah.words.isNotEmpty()) {
                Text(
                    text = "Tap any Arabic word for meaning & root:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ayah.words.take(18).forEach { word ->
                            FilterChip(
                                selected = false,
                                onClick = { onWordTap(word) },
                                label = {
                                    Text(
                                        text = word.arabicWord,
                                        style = arabicTextStyle(18f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            TrilingualTextBlock(
                language = uiState.prefs.language,
                romanUrdu = ayah.romanUrdu,
                urduTranslation = ayah.urduTranslation,
                englishTranslation = ayah.englishTranslation,
                fontScale = uiState.prefs.translationFontScale
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${ayah.translatorEn} | ${ayah.translatorUr}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onCopy, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy Ayah", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Rounded.Share, contentDescription = "Share Ayah", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
