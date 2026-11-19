package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.noorPrefsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "noor_user_prefs"
)

data class UserPreferencesState(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val arabicFontScale: Float = 1.1f,
    val translationFontScale: Float = 1.0f,
    val darkReadingMode: Boolean = false,
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    val calculationMethodId: Int = 1,
    val latitude: Double = 24.8607,
    val longitude: Double = 67.0011,
    val locationName: String = "Karachi, Pakistan",
    val fajrAdjustMin: Int = 0,
    val dhuhrAdjustMin: Int = 0,
    val asrAdjustMin: Int = 0,
    val maghribAdjustMin: Int = 0,
    val ishaAdjustMin: Int = 0,
    val missedPrayersEnabled: Boolean = true,
    val guestModeActive: Boolean = false
)

class UserPreferencesDataStore(private val context: Context) {
    private object Keys {
        val LANGUAGE = stringPreferencesKey("language_code")
        val ARABIC_SCALE = floatPreferencesKey("arabic_font_scale")
        val TRANSLATION_SCALE = floatPreferencesKey("translation_font_scale")
        val DARK_READING = booleanPreferencesKey("dark_reading_mode")
        val LAST_SURAH = intPreferencesKey("last_read_surah")
        val LAST_AYAH = intPreferencesKey("last_read_ayah")
        val CALC_METHOD = intPreferencesKey("calculation_method")
        val LAT = doublePreferencesKey("latitude")
        val LNG = doublePreferencesKey("longitude")
        val LOCATION_NAME = stringPreferencesKey("location_name")
        val FAJR_ADJ = intPreferencesKey("fajr_adj")
        val DHUHR_ADJ = intPreferencesKey("dhuhr_adj")
        val ASR_ADJ = intPreferencesKey("asr_adj")
        val MAGHRIB_ADJ = intPreferencesKey("maghrib_adj")
        val ISHA_ADJ = intPreferencesKey("isha_adj")
        val MISSED_PRAYERS_ENABLED = booleanPreferencesKey("missed_prayers_enabled")
        val GUEST_MODE = booleanPreferencesKey("guest_mode_active")
    }

    val preferencesFlow: Flow<UserPreferencesState> = context.noorPrefsDataStore.data.map { prefs ->
        UserPreferencesState(
            language = AppLanguage.fromCode(prefs[Keys.LANGUAGE]),
            arabicFontScale = prefs[Keys.ARABIC_SCALE] ?: 1.1f,
            translationFontScale = prefs[Keys.TRANSLATION_SCALE] ?: 1.0f,
            darkReadingMode = prefs[Keys.DARK_READING] ?: false,
            lastReadSurah = prefs[Keys.LAST_SURAH] ?: 1,
            lastReadAyah = prefs[Keys.LAST_AYAH] ?: 1,
            calculationMethodId = prefs[Keys.CALC_METHOD] ?: 1,
            latitude = prefs[Keys.LAT] ?: 24.8607,
            longitude = prefs[Keys.LNG] ?: 67.0011,
            locationName = prefs[Keys.LOCATION_NAME] ?: "Karachi, Pakistan",
            fajrAdjustMin = prefs[Keys.FAJR_ADJ] ?: 0,
            dhuhrAdjustMin = prefs[Keys.DHUHR_ADJ] ?: 0,
            asrAdjustMin = prefs[Keys.ASR_ADJ] ?: 0,
            maghribAdjustMin = prefs[Keys.MAGHRIB_ADJ] ?: 0,
            ishaAdjustMin = prefs[Keys.ISHA_ADJ] ?: 0,
            missedPrayersEnabled = prefs[Keys.MISSED_PRAYERS_ENABLED] ?: true,
            guestModeActive = prefs[Keys.GUEST_MODE] ?: false
        )
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.noorPrefsDataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun setFontScales(arabicScale: Float, translationScale: Float) {
        context.noorPrefsDataStore.edit {
            it[Keys.ARABIC_SCALE] = arabicScale.coerceIn(0.8f, 2.0f)
            it[Keys.TRANSLATION_SCALE] = translationScale.coerceIn(0.8f, 1.8f)
        }
    }

    suspend fun setDarkReadingMode(enabled: Boolean) {
        context.noorPrefsDataStore.edit { it[Keys.DARK_READING] = enabled }
    }

    suspend fun setLastReadPosition(surah: Int, ayah: Int) {
        context.noorPrefsDataStore.edit {
            it[Keys.LAST_SURAH] = surah.coerceIn(1, 114)
            it[Keys.LAST_AYAH] = ayah.coerceAtLeast(1)
        }
    }

    suspend fun setCalculationMethod(methodId: Int) {
        context.noorPrefsDataStore.edit { it[Keys.CALC_METHOD] = methodId }
    }

    suspend fun setLocation(lat: Double, lng: Double, name: String) {
        context.noorPrefsDataStore.edit {
            it[Keys.LAT] = lat
            it[Keys.LNG] = lng
            it[Keys.LOCATION_NAME] = name
        }
    }

    suspend fun setPrayerAdjustments(fajr: Int, dhuhr: Int, asr: Int, maghrib: Int, isha: Int) {
        context.noorPrefsDataStore.edit {
            it[Keys.FAJR_ADJ] = fajr.coerceIn(-30, 30)
            it[Keys.DHUHR_ADJ] = dhuhr.coerceIn(-30, 30)
            it[Keys.ASR_ADJ] = asr.coerceIn(-30, 30)
            it[Keys.MAGHRIB_ADJ] = maghrib.coerceIn(-30, 30)
            it[Keys.ISHA_ADJ] = isha.coerceIn(-30, 30)
        }
    }

    suspend fun setMissedPrayersEnabled(enabled: Boolean) {
        context.noorPrefsDataStore.edit { it[Keys.MISSED_PRAYERS_ENABLED] = enabled }
    }

    suspend fun setGuestModeActive(active: Boolean) {
        context.noorPrefsDataStore.edit { it[Keys.GUEST_MODE] = active }
    }
}
