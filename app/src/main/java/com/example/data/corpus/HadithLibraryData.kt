package com.example.data.corpus

import com.example.model.HadithAuthenticity
import com.example.model.HadithEntry

object HadithLibraryData {
    val allHadiths: List<HadithEntry> = listOf(
        HadithEntry(
            id = "bukhari_1",
            bookName = "Sahih al-Bukhari",
            hadithNumber = "1",
            chapter = "Book 1: Revelation (Kitab al-Wahy)",
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
            englishTranslation = "The reward of deeds depends upon the intentions, and every person will get the reward according to what he has intended.",
            urduTranslation = "اعمال کا دارومدار نیتوں پر ہے اور ہر شخص کو وہی ملے گا جس کی اس نے نیت کی۔",
            romanUrdu = "Innamal-a'malu bin-niyyat, wa innama likullim-ri'in ma nawa",
            authenticity = HadithAuthenticity.AGREED_UPON,
            gradingSource = "Sahih al-Bukhari #1 & Sahih Muslim #1907 (Muttafaqun Alayh)",
            explanation = "Sincere intention for Allah transforms ordinary daily routines—eating, working, sleeping—into rewarded worship.",
            practicalLesson = "Renew your intention before starting work, study, or any Sunnah act today.",
            relatedSunnahId = "work_study_sunnah",
            verificationSource = "Sunnah.com (Al-Bukhari 1)",
            verifiedDate = "2026-11-19"
        ),
        HadithEntry(
            id = "muslim_223",
            bookName = "Sahih Muslim",
            hadithNumber = "223",
            chapter = "Book 2: Purification (Kitab at-Taharah)",
            arabicText = "الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ",
            englishTranslation = "Purity is half of faith, and Alhamdulillah (Praise be to Allah) fills the scale.",
            urduTranslation = "پاکیزگی آدھا ایمان ہے، اور الحمدللہ میزان کو بھر دیتا ہے۔",
            romanUrdu = "At-tuhooru shatrul-iman, wal-hamdulillahi tamla'ul-meezan",
            authenticity = HadithAuthenticity.SAHIH,
            gradingSource = "Sahih Muslim #223 (Authenticated by Imam Muslim)",
            explanation = "Both physical cleanliness (Wudu, Miswak, clean clothing) and spiritual purity of heart are central pillars of Iman.",
            practicalLesson = "Maintain Wudu throughout the day and recite Alhamdulillah frequently.",
            relatedSunnahId = "wake_wudu",
            verificationSource = "Sunnah.com (Muslim 223)",
            verifiedDate = "2026-11-19"
        ),
        HadithEntry(
            id = "bukhari_528",
            bookName = "Sahih al-Bukhari",
            hadithNumber = "528",
            chapter = "Book 9: Times of the Prayers",
            arabicText = "أَرَأَيْتُمْ لَوْ أَنَّ نَهْرًا بِبَابِ أَحَدِكُمْ يَغْتَسِلُ فِيهِ كُلَّ يَوْمٍ خَمْسَ مَرَّاتٍ، هَلْ يَبْقَى مِنْ دَرَنِهِ شَيْءٌ",
            englishTranslation = "If there were a river at the door of one of you in which he washed five times a day, would any of his dirt remain? That is the example of the five prayers with which Allah blots out evil deeds.",
            urduTranslation = "بتاؤ اگر تم میں سے کسی کے دروازے پر نہر بہتی ہو اور وہ اس میں روزانہ پانچ بار نہائے تو کیا اس پر کوئی میل باقی رہے گا؟ یہی مثال پانچ نمازوں کی ہے جن کے ذریعے اللہ گناہوں کو مٹا دیتا ہے۔",
            romanUrdu = "Five daily prayers wash away spiritual sins like bathing five times a day in a flowing river.",
            authenticity = HadithAuthenticity.AGREED_UPON,
            gradingSource = "Sahih al-Bukhari #528 & Sahih Muslim #667",
            explanation = "Consistent five daily prayers continually cleanse the heart from minor sins and heedlessness.",
            practicalLesson = "Never skip or delay a prayer; treat each Salah as a spiritual cleansing.",
            relatedSunnahId = "morning_fajr_fard",
            verificationSource = "Sunnah.com (Bukhari 528)",
            verifiedDate = "2026-11-19"
        ),
        HadithEntry(
            id = "abudawud_5082",
            bookName = "Sunan Abi Dawud",
            hadithNumber = "5082",
            chapter = "Book 43: General Behavior (Kitab al-Adab)",
            arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ وَالْمُعَوِّذَتَيْنِ حِينَ تُمْسِي وَحِينَ تُصْبِحُ ثَلَاثَ مَرَّاتٍ تَكْفِيكَ مِنْ كُلِّ شَيْءٍ",
            englishTranslation = "Recite Qul Huwallahu Ahad and Al-Mu'awwidhatayn (Al-Falaq & An-Nas) three times in the morning and three times in the evening; they will suffice you against everything.",
            urduTranslation = "صبح اور شام تین تین مرتبہ قل ہو اللہ احد اور معوذتین پڑھ لیا کرو، یہ تمہیں ہر چیز (شر) سے کافی ہو جائیں گی۔",
            romanUrdu = "Subah aur shaam 3-3 baar Teen Qul parh lena har cheez se hifazat ke liye kaafi hai.",
            authenticity = HadithAuthenticity.HASAN,
            gradingSource = "Graded Hasan Sahih by Imam at-Tirmidhi (#3575) & Sahih by Al-Albani (Sahih Abi Dawud 5082)",
            explanation = "The Prophet ﷺ prescribed these short Surahs twice daily for complete spiritual protection.",
            practicalLesson = "Make the 3 Quls (3x) part of both your Morning and Evening Azkar.",
            relatedSunnahId = "morning_three_quls",
            verificationSource = "Sunnah.com (Abu Dawud 5082 / Tirmidhi 3575)",
            verifiedDate = "2026-11-19"
        ),
        HadithEntry(
            id = "tirmidhi_1956",
            bookName = "Jami' at-Tirmidhi",
            hadithNumber = "1956",
            chapter = "Book 27: Righteousness & Maintaining Relations",
            arabicText = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            englishTranslation = "Your smiling in the face of your brother is charity for you.",
            urduTranslation = "تمہارا اپنے بھائی کے چہرے پر مسکرانا بھی تمہارے لیے صدقہ ہے۔",
            romanUrdu = "Tabassumuka fi wajhi akheeka laka sadaqah",
            authenticity = HadithAuthenticity.SAHIH,
            gradingSource = "Graded Hasan by At-Tirmidhi; Sahih by Ibn Hibban #474 & Al-Albani",
            explanation = "Good character and spreading warmth to others carry the reward of charity without spending wealth.",
            practicalLesson = "Greet family, colleagues, and neighbors today with a sincere smile and Assalamu Alaykum.",
            relatedSunnahId = "social_sunnah",
            verificationSource = "Sunnah.com (Tirmidhi 1956)",
            verifiedDate = "2026-11-19"
        ),
        HadithEntry(
            id = "nasai_5",
            bookName = "Sunan an-Nasa'i",
            hadithNumber = "5",
            chapter = "Book 1: Purification (Kitab at-Taharah)",
            arabicText = "السِّوَاكُ مَطْهَرَةٌ لِلْفَمِ مَرْضَاةٌ لِلرَّبِّ",
            englishTranslation = "The Siwak (tooth-stick) is a means of purifying the mouth and pleasing the Lord.",
            urduTranslation = "مسواک منہ کی پاکیزگی اور رب کی رضامندی کا سبب ہے۔",
            romanUrdu = "As-siwaku matharatun lil-fami mardatun lir-Rabb",
            authenticity = HadithAuthenticity.SAHIH,
            gradingSource = "Sahih al-Bukhari (Mu'allaq in Kitab as-Sawm); graded Sahih by Al-Albani (Sahih an-Nasa'i 5)",
            explanation = "Oral hygiene before prayer and upon waking is elevated into an act that earns Allah's pleasure.",
            practicalLesson = "Keep a Miswak accessible near your Wudu area and bedside.",
            relatedSunnahId = "wake_miswak",
            verificationSource = "Sunnah.com (Nasa'i 5)",
            verifiedDate = "2026-11-19"
        ),
        HadithEntry(
            id = "ibnmajah_224",
            bookName = "Sunan Ibn Majah",
            hadithNumber = "224",
            chapter = "Introduction: Book of the Sunnah",
            arabicText = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
            englishTranslation = "Seeking knowledge is an obligation upon every Muslim.",
            urduTranslation = "علم حاصل کرنا ہر مسلمان پر فرض ہے۔",
            romanUrdu = "Talabul-'ilmi fareedatun 'ala kulli Muslim",
            authenticity = HadithAuthenticity.HASAN,
            gradingSource = "Graded Hasan/Sahih li-ghayrihi by Al-Mizzi, As-Suyuti & Al-Albani due to multiple supporting chains (Sahih Ibn Majah 224); note: some early scholars criticized individual chains.",
            explanation = "Every Muslim is obligated to learn essential creed, worship, and halal/haram ethics relevant to their daily life.",
            practicalLesson = "Dedicate 10 minutes daily to learning one authentic Sunnah or Quranic meaning.",
            relatedSunnahId = "work_study_sunnah",
            verificationSource = "Sunnah.com / Dorar.net",
            verifiedDate = "2026-11-19"
        ),
        HadithEntry(
            id = "muwatta_1685",
            bookName = "Muwatta Imam Malik",
            hadithNumber = "1685 (Book 47, Hadith 8)",
            chapter = "Book 47: Good Character (Husn al-Khuluq)",
            arabicText = "إِنَّمَا بُعِثْتُ لِأُتَمِّمَ حُسْنَ الْأَخْلَاقِ",
            englishTranslation = "I was only sent to perfect good character.",
            urduTranslation = "بے شک مجھے عمدہ اخلاق کی تکمیل کے لیے بھیجا گیا ہے۔",
            romanUrdu = "Innama bu'ithtu li-utammima husnal-akhlaq",
            authenticity = HadithAuthenticity.SAHIH,
            gradingSource = "Muwatta Malik; graded Sahih in Musnad Ahmad #8952 & Al-Albani (Sahih al-Jami' 2349)",
            explanation = "Noble character—honesty, mercy, patience, humility—is the crowning fruit of prophetic guidance.",
            practicalLesson = "Respond to irritation with patience and gentleness today.",
            relatedSunnahId = "social_sunnah",
            verificationSource = "Sunnah.com (Muwatta Malik 47:8)",
            verifiedDate = "2026-11-19"
        ),
        HadithEntry(
            id = "bukhari_6314",
            bookName = "Sahih al-Bukhari",
            hadithNumber = "6314",
            chapter = "Book 80: Invocations (Kitab ad-Da'awat)",
            arabicText = "كَانَ النَّبِيُّ ﷺ إِذَا أَوَى إِلَى فِرَاشِهِ قَالَ: بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
            englishTranslation = "When the Prophet ﷺ went to his bed, he used to say: 'Bismikallahumma amootu wa ahya' (In Your Name, O Allah, I die and I live).",
            urduTranslation = "نبی کریم ﷺ جب اپنے بستر پر آرام فرماتے تو فرماتے: اے اللہ! تیرے نام کے ساتھ میں سوتا (مرتا) ہوں اور جاگتا (زندہ ہوتا) ہوں۔",
            romanUrdu = "Sone se pehle: Bismikallahumma amootu wa ahya.",
            authenticity = HadithAuthenticity.SAHIH,
            gradingSource = "Sahih al-Bukhari #6314",
            explanation = "This brief, deeply meaningful invocation reminds the believer that every morning awakening is a renewed gift from Allah.",
            practicalLesson = "Recite this dua tonight as soon as you lie on your right side.",
            relatedSunnahId = "sleep_4_dua",
            verificationSource = "Sunnah.com (Bukhari 6314)",
            verifiedDate = "2026-11-19"
        ),
        HadithEntry(
            id = "educational_daif_note",
            bookName = "Jami' at-Tirmidhi (Scholarly Verification Note)",
            hadithNumber = "429 (Disputed/Da'if Example)",
            chapter = "Clarifying Authenticity Grades",
            arabicText = "مَنْ قَرَأَ يَسٓ فِي لَيْلَةٍ ابْتِغَاءَ وَجْهِ اللَّهِ غُفِرَ لَهُ",
            englishTranslation = "Scholarly Verification Guide: Always verify narrations before attributing them to the Prophet ﷺ. For example, several narrations about specific rewards of particular nights or extra phrases in common texts are graded Da'if (weak) or Disputed by Hadith critics (like Imam Ad-Daraqutni, Ibn Hajar, or Al-Albani).",
            urduTranslation = "تحقیقی رہنمائی: کسی بھی روایت کو نبی کریم ﷺ کی طرف منسوب کرنے سے پہلے اس کی سند اور درجہ (صحیح، حسن یا ضعیف) کی تصدیق ضروری ہے۔ ضعیف یا مختلف فیہ روایات کو صحیح قرار نہیں دینا چاہیے۔",
            romanUrdu = "Tahqeeqi Rahnumai: Kisi bhi Hadees ko sahih kehne se pehle uski sanad aur darja (Sahih, Hasan, Da'if) check karna zaroori hai.",
            authenticity = HadithAuthenticity.DISPUTED,
            gradingSource = "Hadith methodology transparency — distinguishes Sahih/Hasan from Da'if/Disputed per Dorar.net & Sunnah.com",
            explanation = "Noor-e-Zindagi never labels a Hadith as Sahih without a verified grading source. Users can filter by Sahih, Muttafaqun Alayh, Hasan, Da'if, or Disputed.",
            practicalLesson = "Prioritize Muttafaqun Alayh, Sahih, and Hasan narrations for daily worship and beliefs.",
            relatedSunnahId = null,
            verificationSource = "Dorar.net / Ulum al-Hadith Methodology",
            verifiedDate = "2026-11-19"
        )
    )

    val collectionNames: List<String> = listOf(
        "All",
        "Sahih al-Bukhari",
        "Sahih Muslim",
        "Sunan Abi Dawud",
        "Jami' at-Tirmidhi",
        "Sunan an-Nasa'i",
        "Sunan Ibn Majah",
        "Muwatta Imam Malik"
    )

    fun dailyHadith(dayOfYear: Int): HadithEntry {
        val authentic = allHadiths.filter {
            it.authenticity == HadithAuthenticity.SAHIH ||
                it.authenticity == HadithAuthenticity.AGREED_UPON ||
                it.authenticity == HadithAuthenticity.HASAN
        }
        return authentic[dayOfYear % authentic.size]
    }
}
