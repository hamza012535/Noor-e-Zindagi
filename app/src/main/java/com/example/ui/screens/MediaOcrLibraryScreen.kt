package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.repository.OcrVerificationResult
import com.example.model.MediaGalleryItem
import com.example.ui.components.NoorScreenHeader
import com.example.ui.viewmodel.NoorUiSnapshot

private val curatedVisualGallery = listOf(
    MediaGalleryItem(
        id = "vis_mosque",
        title = "Peaceful Mosque Architecture at Dawn",
        category = "Mosque & Fajr",
        description = "Reflective courtyard welcoming believers before Fajr and morning Adhkar.",
        drawableResId = R.drawable.img_mosque_hero,
        verifiedReference = "Surah An-Nur 24:36 — Houses Allah permitted to be raised and His Name mentioned therein"
    ),
    MediaGalleryItem(
        id = "vis_prayer",
        title = "Prayer Mat & Quran Study Sanctuary",
        category = "Prayer & Quran",
        description = "Distraction-free corner prepared for daily Tilawah, Salah, and gratitude.",
        drawableResId = R.drawable.img_prayer_sanctuary,
        verifiedReference = "Surah Al-Muzzammil 73:4 — Recite the Quran with measured recitation"
    ),
    MediaGalleryItem(
        id = "vis_geometry",
        title = "Classical Islamic Geometric Harmony",
        category = "Islamic Heritage",
        description = "Symmetry and proportion inspired by classical manuscript illumination (no generated Arabic text).",
        drawableResId = R.drawable.img_islamic_geometry,
        verifiedReference = "Hadith (Muslim #91): Verily Allah is Beautiful and loves beauty"
    ),
    MediaGalleryItem(
        id = "vis_night",
        title = "Tranquil Night Courtyard for Tahajjud",
        category = "Night & Bedtime Sunnah",
        description = "Quiet reflection for Bedtime 10-Step Sunnah, Witr, and Tahajjud prayer.",
        drawableResId = R.drawable.img_night_serenity,
        verifiedReference = "Surah Al-Isra 17:79 — Rise from sleep for night prayer"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaOcrLibraryScreen(
    uiState: NoorUiSnapshot,
    ocrResult: OcrVerificationResult?,
    ocrBusy: Boolean,
    onBack: () -> Unit,
    onRunOcr: (Bitmap) -> Unit,
    onSubmitOcrForApproval: (String, String, String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Dua Poster") }
    var contentType by remember { mutableStateOf("ocr_poster") }
    var editableOcrText by remember { mutableStateOf("") }
    var editableRef by remember { mutableStateOf("") }

    LaunchedEffect(ocrResult) {
        if (ocrResult != null) {
            editableOcrText = ocrResult.extractedText
            editableRef = ocrResult.matchedReference
        }
    }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        pickedUri = uri
    }

    fun decodeScaledBitmap(uri: Uri): Bitmap? {
        return try {
            val raw = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
            val maxDim = 1280
            if (raw.width > maxDim || raw.height > maxDim) {
                val scale = maxDim.toFloat() / maxOf(raw.width, raw.height)
                Bitmap.createScaledBitmap(raw, (raw.width * scale).toInt(), (raw.height * scale).toInt(), true)
            } else {
                raw
            }
        } catch (_: Exception) {
            null
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NoorScreenHeader(
            title = "Visual Library & OCR",
            subtitle = "Curated Islamic scenes & poster verification",
            onBack = onBack
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Photo Picker + OCR Verification Workflow
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Islamic Poster / Book Page OCR Scanner", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Select an image of an Islamic dua poster or book page via the privacy-safe Photo Picker. Gemini extracts text for verification, then submits it as Pending for human Admin review.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = {
                                    picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                modifier = Modifier.testTag("pick_ocr_image_btn")
                            ) {
                                Icon(Icons.Rounded.AddPhotoAlternate, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Choose Image")
                            }
                            if (pickedUri != null) {
                                Button(
                                    onClick = {
                                        val bmp = pickedUri?.let(::decodeScaledBitmap)
                                        if (bmp != null) onRunOcr(bmp)
                                    },
                                    enabled = !ocrBusy,
                                    modifier = Modifier.testTag("run_ocr_btn")
                                ) {
                                    if (ocrBusy) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Scanning...")
                                    } else {
                                        Icon(Icons.Rounded.DocumentScanner, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Extract Arabic/Urdu")
                                    }
                                }
                            }
                        }

                        if (pickedUri != null) {
                            AsyncImage(
                                model = pickedUri,
                                contentDescription = "Selected Islamic poster image",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(210.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            )
                        }

                        if (ocrResult != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Automated Check: ${ocrResult.summary}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Submission Title") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text("Category") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("ocr_poster", "dua", "sunnah", "hadith", "quran_ref").forEach { t ->
                                    FilterChip(selected = contentType == t, onClick = { contentType = t }, label = { Text(t) })
                                }
                            }
                            OutlinedTextField(
                                value = editableOcrText,
                                onValueChange = { editableOcrText = it },
                                label = { Text("Extracted Text (Side-by-Side Editable)") },
                                modifier = Modifier.fillMaxWidth().height(120.dp)
                            )
                            OutlinedTextField(
                                value = editableRef,
                                onValueChange = { editableRef = it },
                                label = { Text("Reference Source") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    onSubmitOcrForApproval(
                                        pickedUri?.toString().orEmpty(),
                                        title.ifBlank { "Scanned Islamic Poster" },
                                        category,
                                        contentType,
                                        editableOcrText,
                                        ocrResult.summary,
                                        editableRef
                                    )
                                    title = ""
                                },
                                enabled = editableOcrText.isNotBlank(),
                                modifier = Modifier.fillMaxWidth().testTag("submit_ocr_approval_btn")
                            ) {
                                Text("Submit for Human Admin Approval")
                            }
                        }
                    }
                }
            }

            // Recent OCR Submissions
            if (uiState.localOcrSubmissions.isNotEmpty()) {
                item {
                    Text("Your OCR Submissions", style = MaterialTheme.typography.titleMedium)
                }
                items(uiState.localOcrSubmissions, key = { it.id }) { sub ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${sub.title} • Status: ${sub.status.uppercase()}", style = MaterialTheme.typography.titleMedium)
                            Text(sub.ocrText, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
                            Text(sub.referenceSource, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // 2. Curated Visual Gallery
            item {
                Text("Curated Islamic Visual Library", style = MaterialTheme.typography.titleLarge)
            }
            items(curatedVisualGallery, key = { it.id }) { art ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column {
                        if (art.drawableResId != null) {
                            Image(
                                painter = painterResource(art.drawableResId),
                                contentDescription = art.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(180.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(art.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text(art.title, style = MaterialTheme.typography.titleMedium)
                            Text(art.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(art.verifiedReference, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
}
