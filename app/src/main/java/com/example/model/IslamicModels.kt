package com.example.model

data class WordMeaning(
    val arabicWord: String,
    val transliteration: String,
    val englishMeaning: String,
    val urduMeaning: String,
    val rootWord: String = "",
    val grammarInfo: String = ""
)

enum class SunnahTimeSlot(
    val key: String,
    val titleEn: String,
    val titleUr: String,
    val titleRoman: String,
    val timeWindow: String
) {
    WAKING("waking", "Waking Up", "بیدار ہونے کی سنتیں", "Subah Bedar Hona", "Pre-Fajr / Dawn"),
    MORNING("morning", "Morning Routine", "صبح کا معمول", "Subah Ka Mamool", "Fajr to Ishraq"),
    LEAVING_HOME("leaving", "Leaving & Entering Home", "گھر سے نکلنا اور داخل ہونا", "Ghar Se Nikalna Aur Aana", "Transitions"),
    DAILY_ACTIVITIES("daily", "Daily Etiquette & Work", "روزمرہ آداب، کھانا، کام", "Rozmarrah Aadaab o Kaam", "Throughout the Day"),
    PRAYER("prayer", "Salah & Purification", "نماز اور وضو کا طریقہ", "Namaz Aur Wudu", "5 Daily Prayers"),
    EVENING("evening", "Evening Routine", "شام کے اذکار و معمولات", "Shaam Ke Azkar", "Asr to Maghrib"),
    SLEEPING("sleeping", "Before Sleeping", "سونے سے پہلے کی سنتیں", "Sone Se Pehle Ki Sunnatein", "Night / Bedtime");

    fun label(lang: AppLanguage): String = localized(lang, titleEn, titleUr, titleRoman)
}

enum class WorshipClassification(
    val titleEn: String,
    val titleUr: String,
    val titleRoman: String
) {
    OBLIGATORY("Obligatory (Fard)", "فرض عبادت", "Farz Ibadat"),
    SUNNAH_MUAKKADAH("Emphasized Sunnah", "سنتِ مؤکدہ", "Sunnat-e-Muakkadah"),
    RECOMMENDED_NAFL("Recommended / Voluntary", "مستحب / نفل", "Mustahab / Nafl"),
    GOOD_DEED_ADAB("Sunnah Etiquette", "مسنون آداب", "Masnoon Aadaab");

    fun label(lang: AppLanguage): String = localized(lang, titleEn, titleUr, titleRoman)
}

data class SunnahActivity(
    val id: String,
    val timeSlot: SunnahTimeSlot,
    val classification: WorshipClassification,
    val titleEn: String,
    val titleUr: String,
    val titleRoman: String,
    val subCategory: String,
    val arabicText: String,
    val romanUrdu: String,
    val urduTranslation: String,
    val englishTranslation: String,
    val wordMeanings: List<WordMeaning>,
    val reference: String,
    val explanationEn: String,
    val explanationUr: String,
    val howToPerformEn: String,
    val howToPerformUr: String,
    val recommendedTime: String,
    val recommendedCount: Int = 1,
    val isCoreChecklist: Boolean = true
)

data class SurahInfo(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameRoman: String,
    val meaningEn: String,
    val meaningUr: String,
    val revelationType: String,
    val ayahCount: Int
)

data class AyahData(
    val surahNumber: Int,
    val ayahNumber: Int,
    val globalAyahNumber: Int,
    val arabicUthmani: String,
    val englishTranslation: String,
    val urduTranslation: String,
    val romanUrdu: String,
    val translatorEn: String,
    val translatorUr: String,
    val audioUrl: String,
    val words: List<WordMeaning> = emptyList()
)

enum class HadithAuthenticity(val label: String, val badgeNote: String) {
    SAHIH("Sahih", "Authentic narration meeting strict chain & text criteria"),
    AGREED_UPON("Muttafaqun Alayh", "Agreed upon by Imam al-Bukhari & Imam Muslim"),
    HASAN("Hasan", "Good / reliable chain verified by Hadith scholars"),
    DAIF("Da'if", "Weak narration — shared for scholarly awareness only"),
    DISPUTED("Disputed", "Scholars differ on grading — verify before relying on ruling")
}

data class HadithEntry(
    val id: String,
    val bookName: String,
    val hadithNumber: String,
    val chapter: String,
    val arabicText: String,
    val englishTranslation: String,
    val urduTranslation: String,
    val romanUrdu: String,
    val authenticity: HadithAuthenticity,
    val gradingSource: String,
    val explanation: String,
    val practicalLesson: String,
    val relatedSunnahId: String?,
    val verificationSource: String,
    val verifiedDate: String
)

enum class DuaCategory(
    val titleEn: String,
    val titleUr: String,
    val titleRoman: String
) {
    MORNING_AZKAR("Morning Azkar", "صبح کے اذکار", "Subah Ke Azkar"),
    EVENING_AZKAR("Evening Azkar", "شام کے اذکار", "Shaam Ke Azkar"),
    AFTER_SALAH("After Salah Azkar", "نماز کے بعد کے اذکار", "Namaz Ke Baad Azkar"),
    SLEEPING("Sleeping Duas", "سونے کی دعائیں", "Sone Ki Duain"),
    WAKING_UP("Waking Up Duas", "جاگنے کی دعائیں", "Jagne Ki Duain"),
    FOOD("Food & Drink Duas", "کھانے پینے کی دعائیں", "Khane Peene Ki Duain"),
    TRAVEL("Travel Duas", "سفر کی دعائیں", "Safar Ki Duain"),
    PROTECTION("Protection Duas", "حفاظت کی دعائیں", "Hifazat Ki Duain"),
    FORGIVENESS("Forgiveness & Istighfar", "استغفار و توبہ", "Istighfar o Taubah"),
    ANXIETY_DIFFICULTY("Anxiety & Difficulty", "پریشانی و مشکل کی دعائیں", "Pareshani Ki Duain"),
    RAIN("Rain & Weather Duas", "بارش کی دعائیں", "Barish Ki Duain"),
    MOSQUE("Mosque Duas", "مسجد کی دعائیں", "Masjid Ki Duain"),
    PARENTS("Parents & Family Duas", "والدین کے لیے دعائیں", "Walidain Ki Duain"),
    KNOWLEDGE("Knowledge & Wisdom", "علمِ نافع کی دعائیں", "Ilm Ki Duain");

    fun label(lang: AppLanguage): String = localized(lang, titleEn, titleUr, titleRoman)
}

data class DuaEntry(
    val id: String,
    val category: DuaCategory,
    val titleEn: String,
    val titleUr: String,
    val titleRoman: String,
    val arabicText: String,
    val romanUrdu: String,
    val urduTranslation: String,
    val englishTranslation: String,
    val pronunciationGuide: String,
    val recommendedCount: Int,
    val reference: String,
    val whenToRead: String,
    val audioUrl: String? = null
)

data class PrayerTimeInfo(
    val id: String,
    val nameEn: String,
    val nameArabic: String,
    val time24: String,
    val timeFormatted: String,
    val epochSeconds: Long,
    val isCompleted: Boolean = false,
    val isMissed: Boolean = false
)

data class PrayerDaySchedule(
    val dateGregorian: String,
    val hijriDay: Int,
    val hijriMonthNumber: Int,
    val hijriMonthEn: String,
    val hijriMonthAr: String,
    val hijriYear: Int,
    val hijriFormatted: String,
    val methodName: String,
    val locationName: String,
    val prayers: List<PrayerTimeInfo>,
    val sunriseTime: String,
    val tahajjudWindow: String,
    val israqWindow: String,
    val isOfflineCalculated: Boolean
)

data class IslamicOccasion(
    val hijriMonth: Int,
    val hijriDay: Int,
    val titleEn: String,
    val titleUr: String,
    val description: String,
    val fastingRecommended: Boolean
)

data class CalendarDayCell(
    val gregorianDay: Int,
    val gregorianDateIso: String,
    val hijriDay: Int,
    val hijriMonthEn: String,
    val hijriMonthNumber: Int,
    val hijriYear: Int,
    val isToday: Boolean,
    val isWhiteDay: Boolean,
    val occasion: IslamicOccasion? = null
)

enum class CalculationMethodOption(val apiId: Int, val displayName: String) {
    KARACHI(1, "Univ. of Islamic Sciences, Karachi"),
    ISNA(2, "Islamic Society of North America (ISNA)"),
    MWL(3, "Muslim World League"),
    MAKKAH(4, "Umm Al-Qura University, Makkah"),
    EGYPT(5, "Egyptian General Authority of Survey"),
    DUBAI(16, "Dubai / Gulf Region"),
    SINGAPORE(11, "Majlis Ugama Islam Singapura")
}

data class MediaGalleryItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val drawableResId: Int? = null,
    val imageUri: String? = null,
    val verifiedReference: String
)

data class AiChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val citedSources: List<String> = emptyList(),
    val timestampMillis: Long = System.currentTimeMillis()
)
