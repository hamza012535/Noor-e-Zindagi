package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.NoorScreenHeader
import com.example.ui.viewmodel.NoorUiSnapshot

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    uiState: NoorUiSnapshot,
    onBack: () -> Unit,
    onSaveOrReview: (
        contentId: String?,
        contentType: String,
        category: String,
        title: String,
        arabicText: String,
        romanUrdu: String,
        urduTranslation: String,
        englishTranslation: String,
        referenceSource: String,
        grading: String,
        verificationNotes: String,
        status: String
    ) -> Unit
) {
    var contentType by remember { mutableStateOf("dua") }
    var category by remember { mutableStateOf("Morning Azkar") }
    var title by remember { mutableStateOf("") }
    var arabicText by remember { mutableStateOf("") }
    var romanUrdu by remember { mutableStateOf("") }
    var urduTranslation by remember { mutableStateOf("") }
    var englishTranslation by remember { mutableStateOf("") }
    var referenceSource by remember { mutableStateOf("") }
    var grading by remember { mutableStateOf("Sahih") }
    var notes by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        NoorScreenHeader(
            title = "Admin Verification Console",
            subtitle = "Zero-trust RBAC • Publish & verify religious texts",
            onBack = onBack
        )

        if (!uiState.isAdminUser) {
            Surface(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = "Access Restricted: Sign in with the verified Admin Google account to approve community/OCR submissions or publish verified content.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Pending Review Queue
            item {
                Text(
                    "Pending & Submitted Verification Queue (${uiState.adminQueueContent.size})",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            items(uiState.adminQueueContent, key = { it.id }) { doc ->
                var editArabic by remember(doc.id) { mutableStateOf(doc.arabicText.ifBlank { doc.ocrExtractedText }) }
                var editEnglish by remember(doc.id) { mutableStateOf(doc.englishTranslation) }
                var editUrdu by remember(doc.id) { mutableStateOf(doc.urduTranslation) }
                var editRef by remember(doc.id) { mutableStateOf(doc.referenceSource) }
                var editNotes by remember(doc.id) { mutableStateOf(doc.verificationNotes) }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${doc.title} (${doc.status.uppercase()})", style = MaterialTheme.typography.titleMedium)
                        Text("Type: ${doc.contentType} • Category: ${doc.category}", style = MaterialTheme.typography.labelSmall)
                        if (doc.ocrExtractedText.isNotBlank()) {
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
                                Text("Raw OCR Extracted:\n${doc.ocrExtractedText}", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp))
                            }
                        }
                        OutlinedTextField(value = editArabic, onValueChange = { editArabic = it }, label = { Text("Verified Arabic") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editEnglish, onValueChange = { editEnglish = it }, label = { Text("English Translation") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editUrdu, onValueChange = { editUrdu = it }, label = { Text("Urdu Translation") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editRef, onValueChange = { editRef = it }, label = { Text("Verified Reference") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editNotes, onValueChange = { editNotes = it }, label = { Text("Scholarly Notes") }, modifier = Modifier.fillMaxWidth())

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    onSaveOrReview(
                                        doc.id, doc.contentType, doc.category, doc.title,
                                        editArabic, doc.romanUrdu, editUrdu, editEnglish,
                                        editRef, doc.grading, editNotes, "approved"
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("Approve") }
                            OutlinedButton(
                                onClick = {
                                    onSaveOrReview(
                                        doc.id, doc.contentType, doc.category, doc.title,
                                        editArabic, doc.romanUrdu, editUrdu, editEnglish,
                                        editRef, doc.grading, editNotes, "rejected"
                                    )
                                },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f)
                            ) { Text("Reject", color = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }

            // 2. Publish New Verified Content Form
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Publish New Verified Islamic Content", style = MaterialTheme.typography.titleLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("sunnah", "dua", "quran_ref", "hadith", "reminder").forEach { t ->
                                FilterChip(selected = contentType == t, onClick = { contentType = t }, label = { Text(t) })
                            }
                        }
                        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = arabicText, onValueChange = { arabicText = it }, label = { Text("Arabic Text (with diacritics)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = romanUrdu, onValueChange = { romanUrdu = it }, label = { Text("Roman Urdu") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = urduTranslation, onValueChange = { urduTranslation = it }, label = { Text("Urdu Translation") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = englishTranslation, onValueChange = { englishTranslation = it }, label = { Text("English Translation") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = referenceSource, onValueChange = { referenceSource = it }, label = { Text("Authentic Reference (Book & Number / Surah:Ayah)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = grading, onValueChange = { grading = it }, label = { Text("Grading (Sahih / Hasan / Quran)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Verification Notes") }, modifier = Modifier.fillMaxWidth())

                        Button(
                            onClick = {
                                onSaveOrReview(
                                    null, contentType, category, title,
                                    arabicText, romanUrdu, urduTranslation, englishTranslation,
                                    referenceSource, grading, notes, "approved"
                                )
                                title = ""; arabicText = ""; romanUrdu = ""; urduTranslation = ""; englishTranslation = ""; referenceSource = ""; notes = ""
                            },
                            enabled = title.isNotBlank() && referenceSource.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth().testTag("admin_publish_btn")
                        ) {
                            Text("Verify & Publish to App")
                        }
                    }
                }
            }
        }
    }
}
