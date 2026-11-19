package com.example.data.repository

import com.example.data.local.UserPreferencesState
import com.example.data.remote.AlAdhanApiFactory
import com.example.data.remote.OfflineAstronomyHelper
import com.example.model.CalculationMethodOption
import com.example.model.CalendarDayCell
import com.example.model.PrayerDaySchedule
import com.example.model.PrayerTimeInfo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PrayerCalendarRepository {

    suspend fun getTodayPrayerSchedule(
        prefs: UserPreferencesState,
        completedIds: Set<String>,
        missedIds: Set<String>
    ): PrayerDaySchedule {
        val cal = Calendar.getInstance()
        val apiDateStr = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(cal.time)
        val displayGregorian = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US).format(cal.time)
        val tune = "0,${prefs.fajrAdjustMin},0,${prefs.dhuhrAdjustMin},${prefs.asrAdjustMin},${prefs.maghribAdjustMin},0,${prefs.ishaAdjustMin},0"
        val methodOption = CalculationMethodOption.entries.firstOrNull { it.apiId == prefs.calculationMethodId }
            ?: CalculationMethodOption.KARACHI

        return try {
            val res = AlAdhanApiFactory.api.getTimings(
                dateStr = apiDateStr,
                latitude = prefs.latitude,
                longitude = prefs.longitude,
                method = prefs.calculationMethodId,
                tune = tune
            )
            val data = res.data ?: throw IllegalStateException("Empty AlAdhan response")
            val timings = data.timings ?: throw IllegalStateException("Missing timings")
            val hijri = data.date?.hijri

            val fajr = cleanTime(timings.Fajr)
            val sunrise = cleanTime(timings.Sunrise)
            val dhuhr = cleanTime(timings.Dhuhr)
            val asr = cleanTime(timings.Asr)
            val maghrib = cleanTime(timings.Maghrib)
            val isha = cleanTime(timings.Isha)
            val lastThird = cleanTime(timings.Lastthird).ifBlank { "03:30" }

            val hDay = hijri?.day?.toIntOrNull() ?: 1
            val hMonthNum = hijri?.month?.number ?: 1
            val hMonthEn = hijri?.month?.en ?: "Muharram"
            val hMonthAr = hijri?.month?.ar ?: ""
            val hYear = hijri?.year?.toIntOrNull() ?: 1448

            PrayerDaySchedule(
                dateGregorian = displayGregorian,
                hijriDay = hDay,
                hijriMonthNumber = hMonthNum,
                hijriMonthEn = hMonthEn,
                hijriMonthAr = hMonthAr,
                hijriYear = hYear,
                hijriFormatted = "$hDay $hMonthEn $hYear AH",
                methodName = methodOption.displayName,
                locationName = prefs.locationName,
                prayers = buildPrayerList(fajr, dhuhr, asr, maghrib, isha, completedIds, missedIds),
                sunriseTime = format12h(sunrise),
                tahajjudWindow = "From ~${format12h(lastThird)} until ${format12h(fajr)}",
                israqWindow = "Starts ${addMinutes(sunrise, 18)} (18m after Sunrise)",
                isOfflineCalculated = false
            )
        } catch (_: Exception) {
            val offline = OfflineAstronomyHelper.computeOfflineTimings(
                cal, prefs.latitude, prefs.longitude, prefs.calculationMethodId
            )
            val fajr = addMinutes24(offline["Fajr"] ?: "05:15", prefs.fajrAdjustMin)
            val sunrise = offline["Sunrise"] ?: "06:35"
            val dhuhr = addMinutes24(offline["Dhuhr"] ?: "12:20", prefs.dhuhrAdjustMin)
            val asr = addMinutes24(offline["Asr"] ?: "15:40", prefs.asrAdjustMin)
            val maghrib = addMinutes24(offline["Maghrib"] ?: "17:55", prefs.maghribAdjustMin)
            val isha = addMinutes24(offline["Isha"] ?: "19:15", prefs.ishaAdjustMin)

            val h = OfflineAstronomyHelper.gregorianToHijri(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
            PrayerDaySchedule(
                dateGregorian = displayGregorian,
                hijriDay = h.day,
                hijriMonthNumber = h.monthNumber,
                hijriMonthEn = h.monthEn,
                hijriMonthAr = h.monthAr,
                hijriYear = h.year,
                hijriFormatted = "${h.day} ${h.monthEn} ${h.year} AH",
                methodName = "${methodOption.displayName} (Offline Calc)",
                locationName = prefs.locationName,
                prayers = buildPrayerList(fajr, dhuhr, asr, maghrib, isha, completedIds, missedIds),
                sunriseTime = format12h(sunrise),
                tahajjudWindow = "Last 1/3 of night until ${format12h(fajr)}",
                israqWindow = "Starts ${addMinutes(sunrise, 18)}",
                isOfflineCalculated = true
            )
        }
    }

    suspend fun getMonthlyCalendar(year: Int, month1Based: Int): List<CalendarDayCell> {
        val todayCal = Calendar.getInstance()
        val todayIso = "%04d-%02d-%02d".format(
            todayCal.get(Calendar.YEAR),
            todayCal.get(Calendar.MONTH) + 1,
            todayCal.get(Calendar.DAY_OF_MONTH)
        )

        return try {
            val res = AlAdhanApiFactory.api.getGregorianToHijriCalendar(month1Based, year)
            if (res.data.isEmpty()) throw IllegalStateException("Empty calendar")
            res.data.mapIndexed { idx, item ->
                val gDay = item.date?.gregorian?.day?.toIntOrNull() ?: (idx + 1)
                val iso = "%04d-%02d-%02d".format(year, month1Based, gDay)
                val hDay = item.date?.hijri?.day?.toIntOrNull() ?: 1
                val hMonthNum = item.date?.hijri?.month?.number ?: 1
                val hMonthEn = item.date?.hijri?.month?.en ?: ""
                val hYear = item.date?.hijri?.year?.toIntOrNull() ?: 1448
                CalendarDayCell(
                    gregorianDay = gDay,
                    gregorianDateIso = iso,
                    hijriDay = hDay,
                    hijriMonthEn = hMonthEn,
                    hijriMonthNumber = hMonthNum,
                    hijriYear = hYear,
                    isToday = iso == todayIso,
                    isWhiteDay = hDay in listOf(13, 14, 15),
                    occasion = OfflineAstronomyHelper.getOccasion(hMonthNum, hDay)
                )
            }
        } catch (_: Exception) {
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month1Based - 1)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            (1..daysInMonth).map { day ->
                val iso = "%04d-%02d-%02d".format(year, month1Based, day)
                val h = OfflineAstronomyHelper.gregorianToHijri(year, month1Based, day)
                CalendarDayCell(
                    gregorianDay = day,
                    gregorianDateIso = iso,
                    hijriDay = h.day,
                    hijriMonthEn = h.monthEn,
                    hijriMonthNumber = h.monthNumber,
                    hijriYear = h.year,
                    isToday = iso == todayIso,
                    isWhiteDay = h.day in listOf(13, 14, 15),
                    occasion = OfflineAstronomyHelper.getOccasion(h.monthNumber, h.day)
                )
            }
        }
    }

    private fun buildPrayerList(
        fajr: String,
        dhuhr: String,
        asr: String,
        maghrib: String,
        isha: String,
        completedIds: Set<String>,
        missedIds: Set<String>
    ): List<PrayerTimeInfo> {
        val entries = listOf(
            Triple("prayer_fajr", "Fajr" to "الفجر", fajr),
            Triple("prayer_dhuhr", "Dhuhr" to "الظهر", dhuhr),
            Triple("prayer_asr", "Asr" to "العصر", asr),
            Triple("prayer_maghrib", "Maghrib" to "المغرب", maghrib),
            Triple("prayer_isha", "Isha" to "العشاء", isha)
        )
        return entries.map { (id, names, t24) ->
            PrayerTimeInfo(
                id = id,
                nameEn = names.first,
                nameArabic = names.second,
                time24 = t24,
                timeFormatted = format12h(t24),
                epochSeconds = toTodayEpochSeconds(t24),
                isCompleted = id in completedIds,
                isMissed = id in missedIds
            )
        }
    }

    private fun cleanTime(raw: String): String =
        raw.substringBefore(" ").trim().take(5)

    private fun toTodayEpochSeconds(hhmm: String): Long {
        val parts = hhmm.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
            set(Calendar.SECOND, 0)
        }
        return cal.timeInMillis / 1000L
    }

    private fun format12h(hhmm: String): String {
        val parts = hhmm.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: return hhmm
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val amPm = if (h < 12) "AM" else "PM"
        val h12 = when {
            h == 0 -> 12
            h > 12 -> h - 12
            else -> h
        }
        return "%d:%02d %s".format(h12, m, amPm)
    }

    private fun addMinutes(hhmm: String, deltaMin: Int): String = format12h(addMinutes24(hhmm, deltaMin))

    private fun addMinutes24(hhmm: String, deltaMin: Int): String {
        val parts = hhmm.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 6
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val total = ((h * 60 + m + deltaMin) % 1440 + 1440) % 1440
        return "%02d:%02d".format(total / 60, total % 60)
    }
}
