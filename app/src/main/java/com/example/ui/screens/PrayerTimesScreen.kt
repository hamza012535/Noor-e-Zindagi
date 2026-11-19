package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.core.content.ContextCompat
import com.example.model.CalculationMethodOption
import com.example.ui.components.NoorScreenHeader
import com.example.ui.theme.arabicTextStyle
import com.example.ui.viewmodel.NoorUiSnapshot
import com.google.android.gms.location.LocationServices

private data class CityPreset(val name: String, val lat: Double, val lng: Double)

private val cityPresets = listOf(
    CityPreset("Karachi, Pakistan", 24.8607, 67.0011),
    CityPreset("Lahore, Pakistan", 31.5204, 74.3587),
    CityPreset("Islamabad, Pakistan", 33.6844, 73.0479),
    CityPreset("Makkah, Saudi Arabia", 21.4225, 39.8262),
    CityPreset("Madinah, Saudi Arabia", 24.4672, 39.6111),
    CityPreset("London, UK", 51.5072, -0.1276),
    CityPreset("New York, USA", 40.7128, -74.0060),
    CityPreset("Kuala Lumpur, Malaysia", 3.1390, 101.6869)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrayerTimesScreen(
    uiState: NoorUiSnapshot,
    onBack: () -> Unit,
    onTogglePrayer: (String, Boolean) -> Unit,
    onSetMissedEnabled: (Boolean) -> Unit,
    onUpdateLocation: (Double, Double, String) -> Unit,
    onUpdateMethod: (Int) -> Unit,
    onUpdateAdjustments: (Int, Int, Int, Int, Int) -> Unit,
    onOpenPostPrayerAzkar: () -> Unit
) {
    val context = LocalContext.current
    val schedule = uiState.prayerSchedule
    var showAdjustDialog by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }
    var locationStatusNote by remember { mutableStateOf<String?>(null) }

    @SuppressLint("MissingPermission")
    fun fetchCoarseLocation() {
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                onUpdateLocation(
                    loc.latitude,
                    loc.longitude,
                    "Current GPS (${"%.2f".format(loc.latitude)}, ${"%.2f".format(loc.longitude)})"
                )
                locationStatusNote = null
            } else {
                locationStatusNote = "GPS location unavailable right now; pick a city preset below."
            }
        }.addOnFailureListener {
            locationStatusNote = "Unable to read location; pick a city preset below."
        }
    }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchCoarseLocation()
        else locationStatusNote = "Location permission denied; using selected city coordinates."
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NoorScreenHeader(
            title = "Salah & Prayer Times",
            subtitle = schedule?.hijriFormatted ?: uiState.prefs.locationName,
            onBack = onBack,
            trailing = {
                IconButton(
                    onClick = { showAdjustDialog = true },
                    modifier = Modifier.minimumInteractiveComponentSize().testTag("prayer_adjust_btn")
                ) {
                    Icon(Icons.Rounded.Tune, contentDescription = "Manual prayer minute adjustments")
                }
            }
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Next Prayer Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("NEXT SALAH COUNTDOWN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondaryContainer)
                        Text(uiState.nextPrayerLabel, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary)
                        Text(
                            uiState.nextPrayerCountdown,
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                        Text(
                            "${uiState.prefs.locationName} • ${schedule?.methodName ?: ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        fetchCoarseLocation()
                                    } else {
                                        locationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                                    }
                                },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Rounded.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Use GPS", color = MaterialTheme.colorScheme.onPrimary)
                            }
                            OutlinedButton(
                                onClick = { showCityDialog = true },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("City Preset", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        if (!locationStatusNote.isNullOrBlank()) {
                            Text(locationStatusNote!!, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondaryContainer)
                        }
                    }
                }
            }

            // 5 Obligatory Prayers + Completed/Missed tracking
            if (schedule != null) {
                items(schedule.prayers, key = { it.id }) { prayer ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(
                            1.dp,
                            if (prayer.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(prayer.nameEn, style = MaterialTheme.typography.titleLarge)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(prayer.nameArabic, style = arabicTextStyle(18f), color = MaterialTheme.colorScheme.primary)
                                }
                                Text(
                                    text = if (prayer.isCompleted) "Completed" else if (prayer.isMissed) "Marked Missed (Qaza needed)" else "Pending",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (prayer.isMissed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = prayer.timeFormatted,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { onTogglePrayer(prayer.id, false) },
                                    modifier = Modifier.minimumInteractiveComponentSize().testTag("prayer_complete_${prayer.id}")
                                ) {
                                    Icon(
                                        imageVector = if (prayer.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                        contentDescription = "Mark ${prayer.nameEn} completed",
                                        tint = if (prayer.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (uiState.prefs.missedPrayersEnabled) {
                                    IconButton(
                                        onClick = { onTogglePrayer(prayer.id, true) },
                                        modifier = Modifier.minimumInteractiveComponentSize().testTag("prayer_missed_${prayer.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "Toggle missed Qaza marker",
                                            tint = if (prayer.isMissed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Voluntary / Night Windows: Sunrise, Ishraq, Tahajjud, Witr
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Sunnah & Voluntary Prayer Windows", style = MaterialTheme.typography.titleMedium)
                            Text("Sunrise (End of Fajr): ${schedule.sunriseTime}", style = MaterialTheme.typography.bodyMedium)
                            Text("Ishraq / Duha Window: ${schedule.israqWindow}", style = MaterialTheme.typography.bodyMedium)
                            Text("Tahajjud (Last 1/3 Night): ${schedule.tahajjudWindow}", style = MaterialTheme.typography.bodyMedium)
                            Text("Witr Prayer: After Isha until Fajr (1, 3, or more odd rak'ahs)", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Calculation Method & Missed Prayer Toggle
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
                            Text("Track Missed Prayers (Qaza)", style = MaterialTheme.typography.titleMedium)
                            Switch(
                                checked = uiState.prefs.missedPrayersEnabled,
                                onCheckedChange = onSetMissedEnabled
                            )
                        }
                        Text("Calculation Method", style = MaterialTheme.typography.titleMedium)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CalculationMethodOption.entries.forEach { opt ->
                                FilterChip(
                                    selected = uiState.prefs.calculationMethodId == opt.apiId,
                                    onClick = { onUpdateMethod(opt.apiId) },
                                    label = { Text(opt.displayName) }
                                )
                            }
                        }
                    }
                }
            }

            // Wudu, Salah Guide & Post-Prayer Azkar Link
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Wudu & Salah Method Summary", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "1. Wudu: Intention, Bismillah, wash hands 3x, rinse mouth & nose 3x, wash face 3x, arms to elbows 3x, wipe head & ears 1x, wash feet to ankles 3x.\n" +
                                "2. Salah: Face Qiblah, Takbiratul Ihram, Qiyam (Al-Fatihah + Surah), calm Ruku, upright Qawmah, two peaceful Sujood, Tashahhud, Durood Ibrahim, Taslim.\n" +
                                "3. Sunnah Muakkadah: 2 before Fajr, 4 before & 2 after Dhuhr, 2 after Maghrib, 2 after Isha.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = onOpenPostPrayerAzkar, modifier = Modifier.fillMaxWidth()) {
                            Text("Open Post-Prayer Azkar")
                        }
                    }
                }
            }
        }
    }

    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("Select City for Prayer Times") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    cityPresets.forEach { city ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = {
                                    onUpdateLocation(city.lat, city.lng, city.name)
                                    showCityDialog = false
                                }
                            ) {
                                Text(city.name)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showCityDialog = false }) { Text("Close") } }
        )
    }

    if (showAdjustDialog) {
        var f by remember { mutableStateOf(uiState.prefs.fajrAdjustMin.toString()) }
        var d by remember { mutableStateOf(uiState.prefs.dhuhrAdjustMin.toString()) }
        var a by remember { mutableStateOf(uiState.prefs.asrAdjustMin.toString()) }
        var m by remember { mutableStateOf(uiState.prefs.maghribAdjustMin.toString()) }
        var i by remember { mutableStateOf(uiState.prefs.ishaAdjustMin.toString()) }

        AlertDialog(
            onDismissRequest = { showAdjustDialog = false },
            title = { Text("Manual Minute Adjustments (-30 to +30)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = f, onValueChange = { f = it }, label = { Text("Fajr (min)") }, singleLine = true)
                    OutlinedTextField(value = d, onValueChange = { d = it }, label = { Text("Dhuhr (min)") }, singleLine = true)
                    OutlinedTextField(value = a, onValueChange = { a = it }, label = { Text("Asr (min)") }, singleLine = true)
                    OutlinedTextField(value = m, onValueChange = { m = it }, label = { Text("Maghrib (min)") }, singleLine = true)
                    OutlinedTextField(value = i, onValueChange = { i = it }, label = { Text("Isha (min)") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    onUpdateAdjustments(
                        f.toIntOrNull() ?: 0,
                        d.toIntOrNull() ?: 0,
                        a.toIntOrNull() ?: 0,
                        m.toIntOrNull() ?: 0,
                        i.toIntOrNull() ?: 0
                    )
                    showAdjustDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAdjustDialog = false }) { Text("Cancel") } }
        )
    }
}
