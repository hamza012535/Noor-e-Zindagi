package com.example.data.remote

import com.example.model.IslamicOccasion
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

data class HijriTriplet(
    val day: Int,
    val monthNumber: Int,
    val monthEn: String,
    val monthAr: String,
    val year: Int
)

object OfflineAstronomyHelper {
    private val hijriMonthsEn = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Ula", "Jumada al-Akhirah", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    private val hijriMonthsAr = listOf(
        "مُحَرَّم", "صَفَر", "رَبِيع الأَوَّل", "رَبِيع الثَّانِي",
        "جُمَادَىٰ الأُولَىٰ", "جُمَادَىٰ الآخِرَة", "رَجَب", "شَعْبَان",
        "رَمَضَان", "شَوَّال", "ذُو الْقَعْدَة", "ذُو الْحِجَّة"
    )

    val islamicOccasions: List<IslamicOccasion> = listOf(
        IslamicOccasion(1, 1, "Islamic New Year (1st Muharram)", "نیا اسلامی سال (یکم محرم)", "Beginning of the Hijri calendar marking the Hijrah to Madinah.", false),
        IslamicOccasion(1, 9, "Tasu'a (9th Muharram)", "تاسوعاء (۹ محرم)", "Recommended fast paired with Ashura (Sahih Muslim 1134).", true),
        IslamicOccasion(1, 10, "Day of Ashura (10th Muharram)", "یومِ عاشوراء (۱۰ محرم)", "Prophetic Sunnah fast expiating sins of the previous year (Sahih Muslim 1162).", true),
        IslamicOccasion(3, 12, "Month of Rabi' al-Awwal", "ربیع الاول", "Month of Prophet Muhammad's ﷺ birth and migration to Madinah.", false),
        IslamicOccasion(7, 27, "Isra & Mi'raj Commemoration", "معراج النبی ﷺ کی یاد", "Remembering the Night Journey and the gift of five daily prayers.", false),
        IslamicOccasion(8, 15, "Mid-Sha'ban", "نصف شعبان", "Recommended general fasting in Sha'ban per authentic Sunnah (Bukhari 1970).", true),
        IslamicOccasion(9, 1, "Start of Ramadan", "آغازِ ماہِ رمضان", "Obligatory month of fasting, Quran, Taraweeh, and charity.", true),
        IslamicOccasion(9, 27, "Odd Nights of Last 10 Days (Laylatul Qadr)", "رمضان کی طاق راتیں (شبِ قدر)", "Seek Laylatul Qadr in the odd nights of the last ten days (Bukhari 2017).", false),
        IslamicOccasion(10, 1, "Eid al-Fitr", "عید الفطر", "Day of gratitude, Eid prayer, Zakat al-Fitr, and celebration.", false),
        IslamicOccasion(12, 9, "Day of Arafah (9th Dhu al-Hijjah)", "یومِ عرفہ (۹ ذوالحجہ)", "Fasting expiates sins of the previous and coming year for non-pilgrims (Muslim 1162).", true),
        IslamicOccasion(12, 10, "Eid al-Adha", "عید الاضحیٰ", "Feast of Sacrifice commemorating Prophet Ibrahim's (AS) devotion.", false)
    )

    fun getOccasion(month: Int, day: Int): IslamicOccasion? =
        islamicOccasions.firstOrNull { it.hijriMonth == month && it.hijriDay == day }

    // Kuwaiti / Tabular Islamic Calendar conversion for offline fallback
    fun gregorianToHijri(year: Int, month: Int, day: Int): HijriTriplet {
        var m = month
        var y = year
        if (m < 3) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0).toInt()
        var b = 2 - a + floor(a / 4.0).toInt()
        if (y < 1583) b = 0
        val jd = floor(365.25 * (y + 4716)).toInt() + floor(30.6001 * (m + 1)).toInt() + day + b - 1524
        val z = jd - 1948440 + 10632
        val n = floor((z - 1) / 10631.0).toInt()
        val z2 = z - 10631 * n + 354
        val j = (floor((10985 - z2) / 5316.0).toInt()) * (floor((50 * z2) / 17719.0).toInt()) +
            (floor(z2 / 5670.0).toInt()) * (floor((43 * z2) / 15238.0).toInt())
        val z3 = z2 - (floor((30 - j) / 15.0).toInt()) * (floor((17719 * j) / 50.0).toInt()) -
            (floor(j / 16.0).toInt()) * (floor((15238 * j) / 43.0).toInt()) + 29
        val hMonth = floor((24 * z3) / 709.0).toInt().coerceIn(1, 12)
        val hDay = (z3 - floor((709 * hMonth) / 24.0).toInt()).coerceIn(1, 30)
        val hYear = 30 * n + j - 30
        return HijriTriplet(
            day = hDay,
            monthNumber = hMonth,
            monthEn = hijriMonthsEn[hMonth - 1],
            monthAr = hijriMonthsAr[hMonth - 1],
            year = hYear
        )
    }

    // Approximate astronomical prayer times in HH:mm (24h) for offline reliability
    fun computeOfflineTimings(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        methodId: Int
    ): Map<String, String> {
        val tzHours = TimeZone.getDefault().getOffset(calendar.timeInMillis) / 3600000.0
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val gamma = (2.0 * PI / 365.0) * (dayOfYear - 1)
        val eqTime = 229.18 * (0.000075 + 0.001868 * cos(gamma) - 0.032077 * sin(gamma) -
            0.014615 * cos(2 * gamma) - 0.040849 * sin(2 * gamma))
        val decl = 0.006918 - 0.399912 * cos(gamma) + 0.070257 * sin(gamma) -
            0.006758 * cos(2 * gamma) + 0.000907 * sin(2 * gamma)

        val dhuhrHours = 12.0 + tzHours - (longitude / 15.0) - (eqTime / 60.0)
        val fajrAngle = when (methodId) {
            2 -> 15.0
            else -> 18.0
        }
        val ishaAngle = when (methodId) {
            2 -> 15.0
            4 -> 18.5
            else -> 18.0
        }

        fun hourAngleForSunElevation(elevationDeg: Double): Double {
            val latRad = Math.toRadians(latitude)
            val cosH = (sin(Math.toRadians(elevationDeg)) - sin(latRad) * sin(decl)) /
                (cos(latRad) * cos(decl))
            val clamped = cosH.coerceIn(-0.999, 0.999)
            return Math.toDegrees(acos(clamped)) / 15.0
        }

        val sunriseDiff = hourAngleForSunElevation(-0.833)
        val fajrDiff = hourAngleForSunElevation(-fajrAngle)
        val ishaDiff = hourAngleForSunElevation(-ishaAngle)

        val latRad = Math.toRadians(latitude)
        val asrShadowAngle = Math.toDegrees(atan(1.0 / (1.0 + tan(kotlin.math.abs(latRad - decl)))))
        val asrDiff = hourAngleForSunElevation(asrShadowAngle)

        fun fmt(hours: Double): String {
            val normalized = ((hours % 24.0) + 24.0) % 24.0
            val h = floor(normalized).toInt()
            val m = floor((normalized - h) * 60.0).toInt().coerceIn(0, 59)
            return "%02d:%02d".format(h, m)
        }

        return mapOf(
            "Fajr" to fmt(dhuhrHours - fajrDiff),
            "Sunrise" to fmt(dhuhrHours - sunriseDiff),
            "Dhuhr" to fmt(dhuhrHours + 0.02),
            "Asr" to fmt(dhuhrHours + asrDiff),
            "Maghrib" to fmt(dhuhrHours + sunriseDiff + 0.03),
            "Isha" to fmt(dhuhrHours + ishaDiff)
        )
    }
}
