package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.local.LocalReminderEntity
import com.example.model.AppLanguage
import com.example.notifications.ReminderScheduler
import com.example.ui.components.copyIslamicText
import com.example.ui.components.shareIslamicText
import com.example.ui.viewmodel.NoorUiSnapshot
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSettingsScreen(
    uiState: NoorUiSnapshot,
    userDisplayName: String,
    userEmail: String,
    isSignedIn: Boolean,
    onSetLanguage: (AppLanguage) -> Unit,
    onSetDarkMode: (Boolean) -> Unit,
    onSaveReminder: (LocalReminderEntity) -> Unit,
    onOpenMediaOcr: () -> Unit,
    onOpenAdmin: () -> Unit,
    onSignInFromGuest: () -> Unit,
    onExitGuestToSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onExportData: suspend () -> String,
    onDeleteAccount: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var editingReminder by remember { mutableStateOf<LocalReminderEntity?>(null) }
    var exportedJson by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var pendingPermissionAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val action = pendingPermissionAction
            pendingPermissionAction = null
            if (action != null) {
                action()
            } else {
                ReminderScheduler.showTestNotification(
                    context,
                    "Noor-e-Zindagi Reminders Enabled",
                    "Worship and Sunnah reminders will now gently notify you on schedule."
                )
            }
        } else {
            pendingPermissionAction = null
        }
    }

    fun requestNotifPermissionIfNeeded(onReady: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            pendingPermissionAction = onReady
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onReady()
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Account Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondaryContainer
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(userDisplayName, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                            Text(userEmail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                        }
                    }
                    if (isSignedIn) {
                        Text(
                            "Cloud Sync Active • Your habits, bookmarks, and preferences sync securely to Firebase.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                        OutlinedButton(
                            onClick = onSignOut,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)),
                            modifier = Modifier.testTag("profile_signout_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign Out", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else {
                        Text(
                            "Guest Mode (Offline Local Storage). Sign in with Google to back up & sync across devices.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onSignInFromGuest,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                modifier = Modifier.testTag("profile_google_signin_btn")
                            ) {
                                Text("Sign in with Google")
                            }
                            TextButton(onClick = onExitGuestToSignIn) {
                                Text("Auth Screen", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }
        }

        // 2. Language & Appearance
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Language & Reading Mode", style = MaterialTheme.typography.titleLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppLanguage.entries.forEach { lang ->
                            FilterChip(
                                selected = uiState.prefs.language == lang,
                                onClick = { onSetLanguage(lang) },
                                label = { Text("${lang.displayName} (${lang.nativeLabel})") },
                                modifier = Modifier.testTag("lang_chip_${lang.code}")
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dark Reading Mode", style = MaterialTheme.typography.titleMedium)
                            Text("Low-glare forest night palette for Quran & Tahajjud", style = MaterialTheme.typography.labelSmall)
                        }
                        Switch(
                            checked = uiState.prefs.darkReadingMode,
                            onCheckedChange = onSetDarkMode,
                            modifier = Modifier.testTag("profile_dark_mode_switch")
                        )
                    }
                }
            }
        }

        // 3. Weekly & Monthly Habit Reports
        item {
            val stats = uiState.habitStats
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Weekly & Monthly Worship Reports", style = MaterialTheme.typography.titleLarge)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatTile("Today", "${stats.todayPercent}%", Modifier.weight(1f))
                        StatTile("7-Day Avg", "${stats.weeklyAveragePercent}%", Modifier.weight(1f))
                        StatTile("30-Day Avg", "${stats.monthlyAveragePercent}%", Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatTile("Streak", "${stats.currentStreakDays} days", Modifier.weight(1f))
                        StatTile("Days Logged", "${stats.totalDaysLogged}", Modifier.weight(1f))
                        StatTile("Fasts (30d)", "${stats.fastingDaysThisMonth}", Modifier.weight(1f))
                    }
                }
            }
        }

        // 4. Bookmarks Vault
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saved Bookmarks (${uiState.bookmarks.size})", style = MaterialTheme.typography.titleLarge)
                    }
                    if (uiState.bookmarks.isEmpty()) {
                        Text(
                            "Bookmark any Quran ayah, Sunnah practice, Hadith, or Dua to access it quickly here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        uiState.bookmarks.take(8).forEach { b ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("${b.itemType.uppercase()} • ${b.title}", style = MaterialTheme.typography.labelLarge)
                                    if (b.subtitle.isNotBlank()) {
                                        Text(b.subtitle, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Customizable Reminders & Notifications
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Worship Reminders", style = MaterialTheme.typography.titleLarge)
                        }
                        Row {
                            TextButton(
                                onClick = {
                                    requestNotifPermissionIfNeeded {
                                        ReminderScheduler.showTestNotification(
                                            context,
                                            "Assalamu Alaikum — Noor-e-Zindagi",
                                            "Remember Allah with morning/evening Azkar and sincere Sunnah intentions.",
                                            soundType = "chime"
                                        )
                                    }
                                },
                                modifier = Modifier.testTag("test_notification_btn")
                            ) {
                                Text("Test Alert")
                            }
                            TextButton(
                                onClick = {
                                    requestNotifPermissionIfNeeded {
                                        ReminderScheduler.showTestNotification(
                                            context,
                                            "Salah Time — Custom Adhan Alert",
                                            "Hayya 'alas-Salah — Time for prayer in Noor-e-Zindagi.",
                                            soundType = "adhan"
                                        )
                                    }
                                },
                                modifier = Modifier.testTag("test_adhan_btn")
                            ) {
                                Text("Test Adhan")
                            }
                        }
                    }

                    uiState.reminders.forEach { rem ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rem.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "%02d:%02d • %s • %s".format(rem.hour, rem.minute, rem.frequency, rem.soundType),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextButton(onClick = { editingReminder = rem }) { Text("Edit") }
                            Switch(
                                checked = rem.enabled,
                                onCheckedChange = { enabled ->
                                    requestNotifPermissionIfNeeded {
                                        onSaveReminder(rem.copy(enabled = enabled))
                                    }
                                }
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    }
                }
            }
        }

        // 6. Visual OCR Scanner & Admin Dashboard + Data Controls
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Tools, Verification & Data Controls", style = MaterialTheme.typography.titleLarge)

                    OutlinedButton(
                        onClick = onOpenMediaOcr,
                        modifier = Modifier.fillMaxWidth().testTag("profile_open_ocr_btn")
                    ) {
                        Icon(Icons.Rounded.DocumentScanner, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Visual Islamic Library & OCR Scanner")
                    }

                    if (uiState.isAdminUser) {
                        Button(
                            onClick = onOpenAdmin,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                            modifier = Modifier.fillMaxWidth().testTag("profile_open_admin_btn")
                        ) {
                            Icon(Icons.Rounded.AdminPanelSettings, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Admin Verification Dashboard (${uiState.adminQueueContent.count { it.status == "pending" }} pending)")
                        }
                    } else {
                        Text(
                            "Scholar/Admin verification dashboard unlocks only when signed in with the verified Admin Google account.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch { exportedJson = onExportData() }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("export_data_btn")
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export My Worship Logs & Bookmarks (JSON)")
                    }

                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth().testTag("delete_account_btn")
                    ) {
                        Icon(Icons.Rounded.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete Account & Personal Data", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    // Edit Reminder Dialog
    val remToEdit = editingReminder
    if (remToEdit != null) {
        var hourStr by remember { mutableStateOf(remToEdit.hour.toString()) }
        var minStr by remember { mutableStateOf(remToEdit.minute.toString()) }
        var freq by remember { mutableStateOf(remToEdit.frequency) }
        var sound by remember { mutableStateOf(remToEdit.soundType) }

        AlertDialog(
            onDismissRequest = { editingReminder = null },
            title = { Text("Configure ${remToEdit.title}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = hourStr,
                            onValueChange = { hourStr = it.filter(Char::isDigit).take(2) },
                            label = { Text("Hour (0-23)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minStr,
                            onValueChange = { minStr = it.filter(Char::isDigit).take(2) },
                            label = { Text("Min (0-59)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text("Frequency", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("daily", "weekdays", "fridays").forEach { f ->
                            FilterChip(selected = freq == f, onClick = { freq = f }, label = { Text(f) })
                        }
                    }
                    Text("Notification Sound", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("adhan", "chime", "silent").forEach { s ->
                            FilterChip(selected = sound == s, onClick = { sound = s }, label = { Text(s) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val h = (hourStr.toIntOrNull() ?: remToEdit.hour).coerceIn(0, 23)
                    val m = (minStr.toIntOrNull() ?: remToEdit.minute).coerceIn(0, 59)
                    onSaveReminder(remToEdit.copy(hour = h, minute = m, frequency = freq, soundType = sound))
                    editingReminder = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editingReminder = null }) { Text("Cancel") }
            }
        )
    }

    // Export JSON Dialog
    val json = exportedJson
    if (json != null) {
        AlertDialog(
            onDismissRequest = { exportedJson = null },
            title = { Text("Exported Data (JSON)") },
            text = {
                OutlinedTextField(
                    value = json,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().height(220.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    shareIslamicText(context, "Noor-e-Zindagi Data Export", json)
                    exportedJson = null
                }) { Text("Share") }
            },
            dismissButton = {
                TextButton(onClick = {
                    copyIslamicText(context, "Noor-e-Zindagi JSON", json)
                    exportedJson = null
                }) { Text("Copy") }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account & Data?") },
            text = { Text("This permanently deletes your local & cloud checklists, bookmarks, custom habits, and Firebase user record.") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteAccount()
                    }
                ) { Text("Delete Permanently") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}
