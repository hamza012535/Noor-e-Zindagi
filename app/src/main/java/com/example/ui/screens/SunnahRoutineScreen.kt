package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.corpus.SunnahRoutineRepository
import com.example.model.SunnahActivity
import com.example.model.SunnahTimeSlot
import com.example.model.localized
import com.example.ui.components.ArabicScriptText
import com.example.ui.components.ClassificationBadge
import com.example.ui.components.TrilingualTextBlock
import com.example.ui.components.copyIslamicText
import com.example.ui.components.shareIslamicText
import com.example.ui.viewmodel.NoorUiSnapshot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SunnahRoutineScreen(
    uiState: NoorUiSnapshot,
    onToggleChecklist: (String) -> Unit,
    onToggleBookmark: (String, String, String, String, String) -> Unit,
    onAddCustomActivity: (String, String, String, String) -> Unit,
    onDeleteCustomActivity: (String) -> Unit
) {
    val context = LocalContext.current
    val lang = uiState.prefs.language
    var selectedSlot by remember { mutableStateOf<SunnahTimeSlot?>(null) }
    var inspectedActivity by remember { mutableStateOf<SunnahActivity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredActivities = remember(selectedSlot) {
        if (selectedSlot == null) SunnahRoutineRepository.allActivities
        else SunnahRoutineRepository.allActivities.filter { it.timeSlot == selectedSlot }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_custom_sunnah_fab")
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add custom Sunnah habit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Daily Sunnah Routine", style = MaterialTheme.typography.headlineMedium)
                            Text(
                                "Today's Progress: ${uiState.habitStats.todayPercent}% • Clear Fard / Sunnah / Adab distinction",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedSlot == null,
                                onClick = { selectedSlot = null },
                                label = { Text("All Day") }
                            )
                        }
                        items(SunnahTimeSlot.entries) { slot ->
                            FilterChip(
                                selected = selectedSlot == slot,
                                onClick = { selectedSlot = slot },
                                label = { Text(slot.label(lang)) }
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
                if (uiState.customActivities.isNotEmpty() && selectedSlot == null) {
                    item {
                        Text("Your Custom Sunnah Habits", style = MaterialTheme.typography.titleMedium)
                    }
                    items(uiState.customActivities, key = { it.id }) { custom ->
                        val checked = custom.id in uiState.completedIdsToday
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleChecklist(custom.id) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (checked) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = "Toggle custom activity",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(custom.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "${custom.timeSlot} • ${custom.recommendedTime} ${if (custom.note.isNotBlank()) "— ${custom.note}" else ""}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteCustomActivity(custom.id) },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete custom habit")
                                }
                            }
                        }
                    }
                }

                items(filteredActivities, key = { it.id }) { act ->
                    val checked = act.id in uiState.completedIdsToday
                    val isBookmarked = uiState.bookmarks.any { it.itemType == "sunnah" && it.refId == act.id }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(
                            1.dp,
                            if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { inspectedActivity = act }
                            .testTag("sunnah_card_${act.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ClassificationBadge(act.classification, lang)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            onToggleBookmark(
                                                "sunnah",
                                                act.id,
                                                act.titleEn,
                                                act.arabicText,
                                                act.reference
                                            )
                                        },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                            contentDescription = "Bookmark Sunnah",
                                            tint = if (isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = { onToggleChecklist(act.id) },
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("sunnah_check_${act.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (checked) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                            contentDescription = if (checked) "Completed" else "Mark done",
                                            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Text(
                                text = localized(lang, act.titleEn, act.titleUr, act.titleRoman),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "${act.subCategory} • ${act.recommendedTime}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (act.arabicText.isNotBlank()) {
                                ArabicScriptText(
                                    arabic = act.arabicText,
                                    fontScale = uiState.prefs.arabicFontScale * 0.9f
                                )
                            }
                            TrilingualTextBlock(
                                language = lang,
                                romanUrdu = act.romanUrdu,
                                urduTranslation = act.urduTranslation,
                                englishTranslation = act.englishTranslation,
                                fontScale = uiState.prefs.translationFontScale,
                                showAllLanguages = false
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = act.reference,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(
                                        text = "Guide & Words",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Full Activity Detail Modal Sheet
    val detail = inspectedActivity
    if (detail != null) {
        val checked = detail.id in uiState.completedIdsToday
        val isBookmarked = uiState.bookmarks.any { it.itemType == "sunnah" && it.refId == detail.id }
        ModalBottomSheet(onDismissRequest = { inspectedActivity = null }) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ClassificationBadge(detail.classification, lang)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = localized(lang, detail.titleEn, detail.titleUr, detail.titleRoman),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Recommended Time: ${detail.recommendedTime} • Count: ${detail.recommendedCount}x",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (detail.arabicText.isNotBlank()) {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ArabicScriptText(
                                arabic = detail.arabicText,
                                fontScale = uiState.prefs.arabicFontScale,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
                item {
                    TrilingualTextBlock(
                        language = lang,
                        romanUrdu = detail.romanUrdu,
                        urduTranslation = detail.urduTranslation,
                        englishTranslation = detail.englishTranslation,
                        fontScale = uiState.prefs.translationFontScale,
                        showAllLanguages = true
                    )
                }
                if (detail.wordMeanings.isNotEmpty()) {
                    item {
                        Text("Word-by-Word Arabic Meanings", style = MaterialTheme.typography.titleMedium)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            detail.wordMeanings.forEach { w ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("${w.transliteration} — ${w.englishMeaning}", style = MaterialTheme.typography.bodyMedium)
                                            Text("${w.urduMeaning} (${w.rootWord})", style = MaterialTheme.typography.labelSmall)
                                        }
                                        Text(w.arabicWord, style = MaterialTheme.typography.titleLarge)
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    HorizontalDivider()
                    Text("Explanation of the Sunnah", style = MaterialTheme.typography.titleMedium)
                    Text(detail.explanationEn, style = MaterialTheme.typography.bodyMedium)
                    Text(detail.explanationUr, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item {
                    Text("How to Perform", style = MaterialTheme.typography.titleMedium)
                    Text(detail.howToPerformEn, style = MaterialTheme.typography.bodyMedium)
                    Text(detail.howToPerformUr, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Authentic Reference: ${detail.reference}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onToggleChecklist(detail.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (checked) "Completed Today" else "Mark Complete")
                        }
                        IconButton(
                            onClick = {
                                onToggleBookmark("sunnah", detail.id, detail.titleEn, detail.arabicText, detail.reference)
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                contentDescription = "Bookmark"
                            )
                        }
                        IconButton(
                            onClick = {
                                copyIslamicText(context, detail.titleEn, "${detail.arabicText}\n\n${detail.englishTranslation}\n\nRef: ${detail.reference}")
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy")
                        }
                        IconButton(
                            onClick = {
                                shareIslamicText(context, detail.titleEn, "${detail.arabicText}\n\n${detail.englishTranslation}\n\nRef: ${detail.reference}")
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Rounded.Share, contentDescription = "Share")
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var slot by remember { mutableStateOf(SunnahTimeSlot.DAILY_ACTIVITIES) }
        var recTime by remember { mutableStateOf("After Asr") }
        var note by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Custom Sunnah Habit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Activity Title (e.g., 2 Rak'ah Ishraq)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = recTime,
                        onValueChange = { recTime = it },
                        label = { Text("Recommended Time") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Intention / Reference Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(SunnahTimeSlot.entries) { itemSlot ->
                            FilterChip(
                                selected = slot == itemSlot,
                                onClick = { slot = itemSlot },
                                label = { Text(itemSlot.titleEn) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddCustomActivity(title, slot.key, recTime, note)
                        showAddDialog = false
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
