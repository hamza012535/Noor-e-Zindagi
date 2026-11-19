package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.audio.AudioPlaybackState
import com.example.data.corpus.AzkarDuaData
import com.example.model.DuaCategory
import com.example.model.localized
import com.example.ui.components.ArabicScriptText
import com.example.ui.components.TrilingualTextBlock
import com.example.ui.components.copyIslamicText
import com.example.ui.components.shareIslamicText
import com.example.ui.viewmodel.NoorUiSnapshot

@Composable
fun AzkarDuaScreen(
    uiState: NoorUiSnapshot,
    audioState: AudioPlaybackState,
    onPlayAudio: (String, String, String) -> Unit,
    onToggleBookmark: (String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    val lang = uiState.prefs.language
    var selectedCategory by remember { mutableStateOf<DuaCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val tasbihCounts = remember { mutableStateMapOf<String, Int>() }

    val filteredDuas = remember(selectedCategory, searchQuery) {
        AzkarDuaData.allDuas.filter { dua ->
            val catOk = selectedCategory == null || dua.category == selectedCategory
            val q = searchQuery.trim()
            val queryOk = q.isBlank() ||
                dua.titleEn.contains(q, true) ||
                dua.englishTranslation.contains(q, true) ||
                dua.romanUrdu.contains(q, true) ||
                dua.whenToRead.contains(q, true)
            catOk && queryOk
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Duas, Azkar & Tasbih", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "14 authentic categories • Tap the counter on each card to track recitations",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search dua, need, or translation") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("azkar_search_input")
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All (${AzkarDuaData.allDuas.size})") }
                        )
                    }
                    items(DuaCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label(lang)) }
                        )
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredDuas, key = { it.id }) { dua ->
                val count = tasbihCounts[dua.id] ?: 0
                val completedCount = count >= dua.recommendedCount
                val isBookmarked = uiState.bookmarks.any { it.itemType == "dua" && it.refId == dua.id }
                val audioId = "dua_${dua.id}"
                val isPlaying = audioState.activeId == audioId && audioState.isPlaying

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        1.dp,
                        if (completedCount) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = "${dua.category.label(lang)} • ${dua.recommendedCount}x",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Row {
                                if (!dua.audioUrl.isNullOrBlank()) {
                                    IconButton(
                                        onClick = { onPlayAudio(audioId, dua.titleEn, dua.audioUrl) },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                                            contentDescription = "Play audio recitation"
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        onToggleBookmark("dua", dua.id, dua.titleEn, dua.arabicText, dua.reference)
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                        contentDescription = "Bookmark dua",
                                        tint = if (isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Text(
                            text = localized(lang, dua.titleEn, dua.titleUr, dua.titleRoman),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )

                        ArabicScriptText(
                            arabic = dua.arabicText,
                            fontScale = uiState.prefs.arabicFontScale
                        )

                        TrilingualTextBlock(
                            language = lang,
                            romanUrdu = dua.romanUrdu,
                            urduTranslation = dua.urduTranslation,
                            englishTranslation = dua.englishTranslation,
                            fontScale = uiState.prefs.translationFontScale
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Pronunciation Guide: ${dua.pronunciationGuide}", style = MaterialTheme.typography.labelSmall)
                                Text("When to read: ${dua.whenToRead}", style = MaterialTheme.typography.labelSmall)
                                Text("Reference: ${dua.reference}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { tasbihCounts[dua.id] = count + 1 },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (completedCount) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                        contentColor = if (completedCount) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.testTag("tasbih_btn_${dua.id}")
                                ) {
                                    Text(
                                        text = if (completedCount) "Completed ($count/${dua.recommendedCount})"
                                        else "Recite Tap ($count / ${dua.recommendedCount})"
                                    )
                                }
                                if (count > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { tasbihCounts[dua.id] = 0 },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Rounded.Refresh, contentDescription = "Reset counter")
                                    }
                                }
                            }
                            Row {
                                IconButton(
                                    onClick = {
                                        copyIslamicText(
                                            context,
                                            dua.titleEn,
                                            "${dua.arabicText}\n\n${dua.romanUrdu}\n\n${dua.englishTranslation}\n\nRef: ${dua.reference}"
                                        )
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy dua", modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = {
                                        shareIslamicText(
                                            context,
                                            dua.titleEn,
                                            "${dua.arabicText}\n\n${dua.romanUrdu}\n\n${dua.englishTranslation}\n\nRef: ${dua.reference}"
                                        )
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Rounded.Share, contentDescription = "Share dua", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
