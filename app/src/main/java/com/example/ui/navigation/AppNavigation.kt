package com.example.ui.navigation

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.audio.AudioPlaybackMode
import com.example.audio.NoorMediaController
import com.example.data.local.NoorDatabase
import com.example.data.local.UserPreferencesDataStore
import com.example.data.local.UserPreferencesState
import com.example.data.repository.IslamicAiRepository
import com.example.data.repository.PrayerCalendarRepository
import com.example.data.repository.QuranRepository
import com.example.data.repository.UserSyncRepository
import com.example.firebase.attemptAutoSignIn
import com.example.firebase.authStateFlow
import com.example.firebase.onGoogleSignInClicked
import com.example.firebase.signOutUser
import com.example.notifications.ReminderScheduler
import com.example.ui.auth.AuthScreen
import com.example.ui.components.AudioAndAmbienceSheet
import com.example.ui.components.AudioPlaybackBanner
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AzkarDuaScreen
import com.example.ui.screens.HadithLibraryScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.IslamicCalendarScreen
import com.example.ui.screens.MediaOcrLibraryScreen
import com.example.ui.screens.PrayerTimesScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.QuranReaderScreen
import com.example.ui.screens.SunnahRoutineScreen
import com.example.ui.theme.NoorEZindagiTheme
import com.example.ui.viewmodel.NoorViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

object NoorRoutes {
    const val HOME = "home"
    const val QURAN = "quran"
    const val SUNNAH = "sunnah"
    const val AZKAR = "azkar"
    const val PROFILE = "profile"
    const val PRAYER_TIMES = "prayer_times"
    const val HADITH = "hadith"
    const val CALENDAR = "calendar"
    const val AI_ASSISTANT = "ai_assistant"
    const val MEDIA_OCR = "media_ocr"
    const val ADMIN = "admin"
}

private data class TopLevelTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val topLevelTabs = listOf(
    TopLevelTab(NoorRoutes.HOME, "Home", Icons.Rounded.Home, Icons.Outlined.Home, "nav_home"),
    TopLevelTab(NoorRoutes.QURAN, "Quran", Icons.Rounded.AutoStories, Icons.Outlined.AutoStories, "nav_quran"),
    TopLevelTab(NoorRoutes.SUNNAH, "Sunnah", Icons.Rounded.Checklist, Icons.Outlined.Checklist, "nav_sunnah"),
    TopLevelTab(NoorRoutes.AZKAR, "Azkar", Icons.Rounded.Favorite, Icons.Outlined.FavoriteBorder, "nav_azkar"),
    TopLevelTab(NoorRoutes.PROFILE, "Profile", Icons.Rounded.Person, Icons.Outlined.Person, "nav_profile")
)

@Composable
fun NoorRootApp() {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val prefsStore = remember { UserPreferencesDataStore(app) }
    val prefs by prefsStore.preferencesFlow.collectAsStateWithLifecycle(initialValue = UserPreferencesState())
    val firebaseReady = remember(app) { FirebaseApp.getApps(app).isNotEmpty() }
    val currentUser by remember(firebaseReady) {
        if (firebaseReady) Firebase.auth.authStateFlow() else flowOf(null)
    }.collectAsStateWithLifecycle(initialValue = if (firebaseReady) Firebase.auth.currentUser else null)

    val credentialManager = remember { CredentialManager.create(context) }
    val scope = rememberCoroutineScope()
    var isSigningIn by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                scope.launch { prefsStore.setGuestModeActive(false) }
            },
            onUnauthenticated = {},
            scope = scope
        )
    }

    NoorEZindagiTheme(darkTheme = prefs.darkReadingMode) {
        if (currentUser == null && !prefs.guestModeActive) {
            AuthScreen(
                isSigningIn = isSigningIn,
                authError = authError,
                onGoogleSignIn = {
                    isSigningIn = true
                    authError = null
                    onGoogleSignInClicked(
                        context = context,
                        credentialManager = credentialManager,
                        onAuthSuccess = {
                            isSigningIn = false
                            scope.launch { prefsStore.setGuestModeActive(false) }
                        },
                        onAuthError = { msg ->
                            isSigningIn = false
                            authError = msg
                        },
                        onAuthCancelled = {
                            isSigningIn = false
                        },
                        scope = scope
                    )
                },
                onContinueAsGuest = {
                    scope.launch { prefsStore.setGuestModeActive(true) }
                }
            )
        } else {
            val sessionKey = currentUser?.uid ?: "guest"
            key(sessionKey) {
                val noorDao = remember { NoorDatabase.getInstance(app).noorDao() }
                val syncRepo = remember { UserSyncRepository(app, noorDao) }
                val quranRepo = remember { QuranRepository(noorDao) }
                val prayerRepo = remember { PrayerCalendarRepository() }
                val aiRepo = remember { IslamicAiRepository(noorDao) }
                val mediaController = remember { NoorMediaController.getInstance(app) }

                val factory = remember(sessionKey) {
                    object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return NoorViewModel(
                                appContext = app,
                                currentUserId = currentUser?.uid,
                                userDisplayName = currentUser?.displayName ?: "Believer",
                                userEmail = currentUser?.email ?: "",
                                prefsStore = prefsStore,
                                syncRepo = syncRepo,
                                quranRepo = quranRepo,
                                prayerRepo = prayerRepo,
                                aiRepo = aiRepo,
                                mediaController = mediaController
                            ) as T
                        }
                    }
                }
                val viewModel: NoorViewModel = viewModel(key = sessionKey, factory = factory)

                MainAppShell(
                    viewModel = viewModel,
                    userDisplayName = currentUser?.displayName ?: "Guest Believer",
                    userEmail = currentUser?.email ?: "Offline Local Mode",
                    isSignedIn = currentUser != null,
                    onSignInFromGuest = {
                        onGoogleSignInClicked(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = {
                                scope.launch { prefsStore.setGuestModeActive(false) }
                            },
                            onAuthError = { msg ->
                                authError = msg
                            },
                            scope = scope
                        )
                    },
                    onSignOut = {
                        signOutUser(credentialManager, scope) {
                            scope.launch { prefsStore.setGuestModeActive(false) }
                        }
                    },
                    onExitGuestToSignIn = {
                        scope.launch { prefsStore.setGuestModeActive(false) }
                    },
                    onStopAudio = {
                        mediaController.audioPlayer.stop()
                        mediaController.syncForegroundService()
                    },
                    onTogglePlayPauseAudio = viewModel::togglePlayPauseAudio
                )
            }
        }
    }
}

@Composable
private fun MainAppShell(
    viewModel: NoorViewModel,
    userDisplayName: String,
    userEmail: String,
    isSignedIn: Boolean,
    onSignInFromGuest: () -> Unit,
    onSignOut: () -> Unit,
    onExitGuestToSignIn: () -> Unit,
    onStopAudio: () -> Unit,
    onTogglePlayPauseAudio: () -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: NoorRoutes.HOME

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    val ambientState by viewModel.ambientState.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAudioStudioSheet by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        val msg = snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    LaunchedEffect(uiState.reminders) {
        uiState.reminders.forEach { reminder ->
            ReminderScheduler.scheduleReminder(context, reminder)
        }
    }

    val isTopLevelRoute = topLevelTabs.any { it.route == currentRoute }

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 700.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (!useRail && isTopLevelRoute) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        topLevelTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTopLevel(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail && isTopLevelRoute) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        topLevelTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopLevel(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val syncBanner = uiState.syncErrorBanner
                    if (!syncBanner.isNullOrBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = syncBanner,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }

                    AudioPlaybackBanner(
                        audioState = audioState,
                        ambientState = ambientState,
                        onStop = onStopAudio,
                        onTogglePlayPause = onTogglePlayPauseAudio,
                        onCycleMode = {
                            val entries = AudioPlaybackMode.entries
                            val next = entries[(audioState.mode.ordinal + 1) % entries.size]
                            viewModel.setAudioPlaybackMode(next)
                        },
                        onOpenAudioStudio = { showAudioStudioSheet = true },
                        onStopAmbient = { viewModel.updateAmbientSound(enabled = false) }
                    )

                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        NavHost(
                            navController = navController,
                            startDestination = NoorRoutes.HOME
                        ) {
                            composable(NoorRoutes.HOME) {
                                HomeDashboardScreen(
                                    uiState = uiState,
                                    userDisplayName = userDisplayName,
                                    audioState = audioState,
                                    ambientState = ambientState,
                                    onToggleChecklist = viewModel::toggleChecklistActivity,
                                    onPlayAudioWithUrdu = viewModel::toggleAudioWithUrdu,
                                    onNavigateTab = ::navigateTopLevel,
                                    onOpenPrayerTimes = { navController.navigate(NoorRoutes.PRAYER_TIMES) },
                                    onOpenHadith = { navController.navigate(NoorRoutes.HADITH) },
                                    onOpenCalendar = { navController.navigate(NoorRoutes.CALENDAR) },
                                    onOpenAiAssistant = { navController.navigate(NoorRoutes.AI_ASSISTANT) },
                                    onOpenMediaOcr = { navController.navigate(NoorRoutes.MEDIA_OCR) },
                                    onOpenAudioStudio = { showAudioStudioSheet = true }
                                )
                            }
                            composable(NoorRoutes.QURAN) {
                                QuranReaderScreen(
                                    viewModel = viewModel,
                                    uiState = uiState,
                                    audioState = audioState,
                                    ambientState = ambientState,
                                    onOpenAudioStudio = { showAudioStudioSheet = true }
                                )
                            }
                            composable(NoorRoutes.SUNNAH) {
                                SunnahRoutineScreen(
                                    uiState = uiState,
                                    audioState = audioState,
                                    onPlayAudioWithUrdu = viewModel::toggleAudioWithUrdu,
                                    onToggleChecklist = viewModel::toggleChecklistActivity,
                                    onToggleBookmark = viewModel::toggleBookmark,
                                    onAddCustomActivity = viewModel::addCustomActivity,
                                    onDeleteCustomActivity = viewModel::deleteCustomActivity
                                )
                            }
                            composable(NoorRoutes.AZKAR) {
                                AzkarDuaScreen(
                                    uiState = uiState,
                                    audioState = audioState,
                                    ambientState = ambientState,
                                    onPlayAudioWithUrdu = viewModel::toggleAudioWithUrdu,
                                    onSetAudioMode = viewModel::setAudioPlaybackMode,
                                    onToggleBookmark = viewModel::toggleBookmark,
                                    onOpenAudioStudio = { showAudioStudioSheet = true }
                                )
                            }
                            composable(NoorRoutes.PROFILE) {
                                ProfileSettingsScreen(
                                    uiState = uiState,
                                    userDisplayName = userDisplayName,
                                    userEmail = userEmail,
                                    isSignedIn = isSignedIn,
                                    onSetLanguage = viewModel::setLanguage,
                                    onSetDarkMode = viewModel::setDarkReadingMode,
                                    onSaveReminder = viewModel::saveReminder,
                                    onOpenMediaOcr = { navController.navigate(NoorRoutes.MEDIA_OCR) },
                                    onOpenAdmin = { navController.navigate(NoorRoutes.ADMIN) },
                                    onSignInFromGuest = onSignInFromGuest,
                                    onExitGuestToSignIn = onExitGuestToSignIn,
                                    onSignOut = onSignOut,
                                    onExportData = viewModel::exportUserDataJson,
                                    onDeleteAccount = { viewModel.deleteAccountAndData(onSignOut) }
                                )
                            }
                            composable(NoorRoutes.PRAYER_TIMES) {
                                PrayerTimesScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onTogglePrayer = viewModel::togglePrayerStatus,
                                    onSetMissedEnabled = viewModel::setMissedPrayersEnabled,
                                    onUpdateLocation = viewModel::updateLocation,
                                    onUpdateMethod = viewModel::updateCalculationMethod,
                                    onUpdateAdjustments = viewModel::updatePrayerAdjustments,
                                    onOpenPostPrayerAzkar = { navigateTopLevel(NoorRoutes.AZKAR) }
                                )
                            }
                            composable(NoorRoutes.HADITH) {
                                HadithLibraryScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onToggleBookmark = viewModel::toggleBookmark,
                                    onOpenRelatedSunnah = { navigateTopLevel(NoorRoutes.SUNNAH) }
                                )
                            }
                            composable(NoorRoutes.CALENDAR) {
                                IslamicCalendarScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onLoadMonth = viewModel::loadCalendarMonth,
                                    onToggleFasting = viewModel::toggleFastingToday
                                )
                            }
                            composable(NoorRoutes.AI_ASSISTANT) {
                                val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
                                val aiStreamState by viewModel.aiStreamState.collectAsStateWithLifecycle()
                                AiAssistantScreen(
                                    messages = aiMessages,
                                    streamState = aiStreamState,
                                    onBack = { navController.popBackStack() },
                                    onSendQuestion = viewModel::askIslamicAssistant
                                )
                            }
                            composable(NoorRoutes.MEDIA_OCR) {
                                val ocrResult by viewModel.ocrResult.collectAsStateWithLifecycle()
                                val ocrBusy by viewModel.ocrBusy.collectAsStateWithLifecycle()
                                MediaOcrLibraryScreen(
                                    uiState = uiState,
                                    ocrResult = ocrResult,
                                    ocrBusy = ocrBusy,
                                    onBack = { navController.popBackStack() },
                                    onRunOcr = viewModel::runOcrExtraction,
                                    onSubmitOcrForApproval = viewModel::submitOcrForApproval
                                )
                            }
                            composable(NoorRoutes.ADMIN) {
                                AdminDashboardScreen(
                                    uiState = uiState,
                                    onBack = { navController.popBackStack() },
                                    onSaveOrReview = viewModel::adminSaveOrReview
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showAudioStudioSheet) {
            AudioAndAmbienceSheet(
                audioState = audioState,
                ambientState = ambientState,
                autoPlayNextAyah = uiState.prefs.autoPlayNextAyah,
                onSelectReciter = viewModel::setReciter,
                onSelectAudioMode = viewModel::setAudioPlaybackMode,
                onSetRecitationVolume = viewModel::setRecitationVolume,
                onToggleAutoPlayNextAyah = viewModel::setAutoPlayNextAyah,
                onUpdateAmbientSound = viewModel::updateAmbientSound,
                onDismiss = { showAudioStudioSheet = false }
            )
        }
    }
}
