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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.corpus.HadithLibraryData
import com.example.model.HadithAuthenticity
import com.example.ui.components.ArabicScriptText
import com.example.ui.components.AuthenticityBadge
import com.example.ui.components.NoorScreenHeader
import com.example.ui.components.TrilingualTextBlock
import com.example.ui.components.copyIslamicText
import com.example.ui.components.shareIslamicText
import com.example.ui.viewmodel.NoorUiSnapshot

@Composable
fun HadithLibraryScreen(
    uiState: NoorUiSnapshot,
    onBack: () -> Unit,
    onToggleBookmark: (String, String, String, String, String) -> Unit,
    onOpenRelatedSunnah: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedBook by remember { mutableStateOf("All") }
    var selectedGrade by remember { mutableStateOf<HadithAuthenticity?>(null) }

    val filtered = remember(searchQuery, selectedBook, selectedGrade) {
        HadithLibraryData.allHadiths.filter { h ->
            val bookOk = selectedBook == "All" || h.bookName.contains(selectedBook, true)
            val gradeOk = selectedGrade == null || h.authenticity == selectedGrade
            val q = searchQuery.trim()
            val queryOk = q.isBlank() ||
                h.englishTranslation.contains(q, true) ||
                h.bookName.contains(q, true) ||
                h.hadithNumber.contains(q, true) ||
                h.chapter.contains(q, true) ||
                h.explanation.contains(q, true)
            bookOk && gradeOk && queryOk
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NoorScreenHeader(
            title = "Verified Hadith Library",
            subtitle = "7 major collections with transparent grading",
            onBack = onBack
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Hadith text, book, or number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("hadith_search_input")
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(HadithLibraryData.collectionNames) { book ->
                    FilterChip(
                        selected = selectedBook == book,
                        onClick = { selectedBook = book },
                        label = { Text(book) }
                    )
                }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedGrade == null,
                        onClick = { selectedGrade = null },
                        label = { Text("All Grades") }
                    )
                }
                items(HadithAuthenticity.entries) { grade ->
                    FilterChip(
                        selected = selectedGrade == grade,
                        onClick = { selectedGrade = grade },
                        label = { Text(grade.label) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered, key = { it.id }) { h ->
                val isBookmarked = uiState.bookmarks.any { it.itemType == "hadith" && it.refId == h.id }
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
                                Text("${h.bookName} #${h.hadithNumber}", style = MaterialTheme.typography.titleMedium)
                                Text(h.chapter, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            AuthenticityBadge(h.authenticity)
                        }

                        ArabicScriptText(
                            arabic = h.arabicText,
                            fontScale = uiState.prefs.arabicFontScale * 0.92f
                        )

                        TrilingualTextBlock(
                            language = uiState.prefs.language,
                            romanUrdu = h.romanUrdu,
                            urduTranslation = h.urduTranslation,
                            englishTranslation = h.englishTranslation,
                            fontScale = uiState.prefs.translationFontScale
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Grading: ${h.gradingSource}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text("Note: ${h.authenticity.badgeNote}", style = MaterialTheme.typography.labelSmall)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Text("Explanation: ${h.explanation}", style = MaterialTheme.typography.bodyMedium)
                                Text("Practical Lesson: ${h.practicalLesson}", style = MaterialTheme.typography.bodyMedium)
                                Text("Source: ${h.verificationSource} (Verified ${h.verifiedDate})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (h.relatedSunnahId != null) {
                                TextButton(onClick = onOpenRelatedSunnah) {
                                    Text("View Related Sunnah")
                                }
                            } else {
                                Spacer(modifier = Modifier)
                            }
                            Row {
                                IconButton(
                                    onClick = {
                                        onToggleBookmark("hadith", h.id, "${h.bookName} #${h.hadithNumber}", h.arabicText, h.englishTranslation)
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                        contentDescription = "Bookmark Hadith"
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        copyIslamicText(context, "${h.bookName} #${h.hadithNumber}", "${h.arabicText}\n\n${h.englishTranslation}\n\n${h.gradingSource}")
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy Hadith", modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = {
                                        shareIslamicText(context, "${h.bookName} #${h.hadithNumber}", "${h.arabicText}\n\n${h.englishTranslation}\n\n${h.gradingSource}")
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Rounded.Share, contentDescription = "Share Hadith", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
