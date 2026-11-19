package com.example.data.corpus

import com.example.model.DuaCategory
import com.example.model.DuaEntry

object AzkarDuaData {
    val allDuas: List<DuaEntry> = listOf(
        // 1. Morning Azkar
        DuaEntry(
            id = "dua_morning_sayyid_istighfar",
            category = DuaCategory.MORNING_AZKAR,
            titleEn = "Sayyid-ul-Istighfar (Master Supplication for Forgiveness)",
            titleUr = "سید الاستغفار",
            titleRoman = "Sayyid-ul-Istighfar",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَٰهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَىٰ عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            romanUrdu = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'oodhu bika min sharri ma sana'tu, aboo'u laka bini'matika 'alayya, wa aboo'u bidhanbi faghfir li fa'innahu la yaghfirudh-dhunooba illa Anta",
            urduTranslation = "اے اللہ! تو ہی میرا رب ہے، تیرے سوا کوئی معبود نہیں، تو نے مجھے پیدا کیا اور میں تیرا بندہ ہوں، میں اپنی طاقت کے مطابق تیرے عہد اور وعدے پر قائم ہوں۔ میں اپنے برے اعمال کے شر سے تیری پناہ مانگتا ہوں، مجھ پر تیری نعمتوں کا اقرار کرتا ہوں اور اپنے گناہوں کا اعتراف کرتا ہوں، پس مجھے بخش دے کیونکہ تیرے سوا گناہ کوئی نہیں بخش سکتا۔",
            englishTranslation = "O Allah, You are my Lord, none has the right to be worshipped except You. You created me and I am Your servant, and I abide to Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me and I acknowledge my sin, so forgive me, for verily none can forgive sin except You.",
            pronunciationGuide = "Pause gently at commas; emphasize 'Anta Rabbi' with sincerity.",
            recommendedCount = 1,
            reference = "Sahih al-Bukhari 6306",
            whenToRead = "Once every morning after Fajr and once every evening after Asr."
        ),
        DuaEntry(
            id = "dua_morning_protection",
            category = DuaCategory.MORNING_AZKAR,
            titleEn = "Bismillahilladhi La Yadurru (Morning Protection)",
            titleUr = "صبح و شام ہر نقصان سے حفاظت کی دعا",
            titleRoman = "Subah o Shaam Hifazat Ki Dua",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            romanUrdu = "Bismillahil-ladhi la yadurru ma'asmihi shay'un fil-ardi wa la fis-sama'i wa huwas-Samee'ul-'Aleem",
            urduTranslation = "اس اللہ کے نام سے جس کے نام کے ساتھ زمین و آسمان کی کوئی چیز نقصان نہیں پہنچا سکتی اور وہ خوب سننے والا، سب کچھ جاننے والا ہے۔",
            englishTranslation = "In the Name of Allah, with whose Name nothing on earth or in heaven can cause harm, and He is the All-Hearing, All-Knowing.",
            pronunciationGuide = "Recite 3 times clearly with heart present.",
            recommendedCount = 3,
            reference = "Sunan Abi Dawud 5088; Jami' at-Tirmidhi 3388 (Hasan Sahih)",
            whenToRead = "3 times in the morning and 3 times in the evening."
        ),
        // 2. Evening Azkar
        DuaEntry(
            id = "dua_evening_hasbiyallah",
            category = DuaCategory.EVENING_AZKAR,
            titleEn = "Hasbiyallahu La Ilaha Illa Huwa (Surah At-Tawbah 9:129)",
            titleUr = "حَسْبِيَ اللَّهُ (صبح و شام ۷ مرتبہ)",
            titleRoman = "Hasbiyallahu La Ilaha Illa Huwa",
            arabicText = "حَسْبِيَ اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ ۖ عَلَيْهِ تَوَكَّلْتُ ۖ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
            romanUrdu = "Hasbiyallahu la ilaha illa Huwa, 'alayhi tawakkaltu wa Huwa Rabbul-'Arshil-'Azeem",
            urduTranslation = "میرے لیے اللہ ہی کافی ہے، اس کے سوا کوئی معبود نہیں، میں نے اسی پر بھروسہ کیا اور وہ عرشِ عظیم کا مالک ہے۔",
            englishTranslation = "Sufficient for me is Allah; there is no deity except Him. On Him I have relied, and He is the Lord of the Great Throne.",
            pronunciationGuide = "Recite calmly 7 times; lengthen 'Arshil-Azeem'.",
            recommendedCount = 7,
            reference = "Surah At-Tawbah 9:129; Sunan Abi Dawud 5081 (Mawquf/Sahih chain to Abu Darda RA)",
            whenToRead = "7 times in the morning and 7 times in the evening.",
            audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1364.mp3"
        ),
        // 3. After Salah Azkar
        DuaEntry(
            id = "dua_after_salah_astaghfirullah",
            category = DuaCategory.AFTER_SALAH,
            titleEn = "Post-Salah Istighfar & Allahumma Antas-Salam",
            titleUr = "سلام پھیرنے کے بعد کے اذکار",
            titleRoman = "Salam Pherhne Ke Baad Ki Dua",
            arabicText = "أَسْتَغْفِرُ اللَّهَ (٣) • اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            romanUrdu = "Astaghfirullah (3x) • Allahumma Antas-Salamu wa minkas-salamu, tabarakta ya Dhal-Jalali wal-Ikram",
            urduTranslation = "میں اللہ سے بخشش مانگتا ہوں (۳ بار) • اے اللہ! تو ہی سلامتی والا ہے اور تیری ہی طرف سے سلامتی ہے، تو بڑی برکت والا ہے اے عظمت و اکرام والے۔",
            englishTranslation = "I seek the forgiveness of Allah (3x) • O Allah, You are Peace and from You comes peace. Blessed are You, O Possessor of Majesty and Honor.",
            pronunciationGuide = "Say Astaghfirullah 3 times immediately after Salam, then Antas-Salam once.",
            recommendedCount = 3,
            reference = "Sahih Muslim 591",
            whenToRead = "Immediately after concluding every obligatory prayer with Salam."
        ),
        DuaEntry(
            id = "dua_after_salah_tasbih",
            category = DuaCategory.AFTER_SALAH,
            titleEn = "Post-Salah Tasbih (33 • 33 • 33 + 1)",
            titleUr = "نماز کے بعد کی تسبیح (۳۳، ۳۳، ۳۳)",
            titleRoman = "Namaz Ke Baad Tasbih-e-Fatimi",
            arabicText = "سُبْحَانَ اللَّهِ (٣٣) • الْحَمْدُ لِلَّهِ (٣٣) • اللَّهُ أَكْبَرُ (٣٣) • لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            romanUrdu = "SubhanAllah (33x), Alhamdulillah (33x), Allahu Akbar (33x), then complete 100 with La ilaha illallah...",
            urduTranslation = "۳۳ مرتبہ سبحان اللہ، ۳۳ مرتبہ الحمدللہ، ۳۳ مرتبہ اللہ اکبر، اور ۱۰۰ پورا کرنے کے لیے لا الہ الا اللہ وحدہ لا شریک لہ...",
            englishTranslation = "Glory be to Allah (33x), Praise be to Allah (33x), Allah is the Greatest (33x), and complete 100 with the Tawheed testimony.",
            pronunciationGuide = "Count gently on the joints of your right hand fingers (Sunnah).",
            recommendedCount = 33,
            reference = "Sahih Muslim 597",
            whenToRead = "After every Fard prayer; minor sins are forgiven even if like the foam of the sea."
        ),
        // 4. Sleeping Duas
        DuaEntry(
            id = "dua_sleeping_bismika",
            category = DuaCategory.SLEEPING,
            titleEn = "Sleeping Dua (Bismikallahumma)",
            titleUr = "سونے کی دعا",
            titleRoman = "Sone Ki Dua",
            arabicText = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
            romanUrdu = "Bismikallahumma amootu wa ahya",
            urduTranslation = "اے اللہ! تیرے ہی نام کے ساتھ میں مرتا ہوں (سوتا ہوں) اور زندہ ہوتا ہوں۔",
            englishTranslation = "In Your Name, O Allah, I die and I live.",
            pronunciationGuide = "Recite softly while lying on your right side.",
            recommendedCount = 1,
            reference = "Sahih al-Bukhari 6312, 6324",
            whenToRead = "When lying down to sleep."
        ),
        // 5. Waking Up Duas
        DuaEntry(
            id = "dua_waking_alhamdulillah",
            category = DuaCategory.WAKING_UP,
            titleEn = "Waking Up Gratitude Dua",
            titleUr = "جاگنے کی دعا",
            titleRoman = "Jagne Ki Dua",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            romanUrdu = "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushoor",
            urduTranslation = "تمام تعریفیں اللہ کے لیے ہیں جس نے ہمیں موت دینے کے بعد زندہ کیا اور اسی کی طرف لوٹ کر جانا ہے۔",
            englishTranslation = "All praise is for Allah who gave us life after having taken it from us, and unto Him is the resurrection.",
            pronunciationGuide = "Sit up and recite right upon waking.",
            recommendedCount = 1,
            reference = "Sahih al-Bukhari 6312",
            whenToRead = "Immediately upon waking from sleep."
        ),
        // 6. Food Duas
        DuaEntry(
            id = "dua_food_before",
            category = DuaCategory.FOOD,
            titleEn = "Before & After Eating",
            titleUr = "کھانے سے پہلے اور بعد کی دعا",
            titleRoman = "Khane Se Pehle Aur Baad Ki Dua",
            arabicText = "بِسْمِ اللَّهِ • الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَٰذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
            romanUrdu = "Bismillah (before) • Alhamdu lillahil-ladhi at'amani hadha wa razaqanihi min ghayri hawlin minni wa la quwwah (after)",
            urduTranslation = "اللہ کے نام سے • تمام تعریف اللہ کے لیے ہے جس نے مجھے یہ کھانا کھلایا اور بغیر میری طاقت کے مجھے عطا فرمایا۔",
            englishTranslation = "In the name of Allah (before) • All praise is for Allah who fed me this and provided it without any might or power from me (after).",
            pronunciationGuide = "If you forget Bismillah at the start, say: Bismillahi fi awwalihi wa akhirih.",
            recommendedCount = 1,
            reference = "Sahih al-Bukhari 5376; Jami' at-Tirmidhi 3458",
            whenToRead = "Before beginning and right after finishing any meal."
        ),
        // 7. Travel Duas
        DuaEntry(
            id = "dua_travel",
            category = DuaCategory.TRAVEL,
            titleEn = "Dua When Starting a Journey (Surah Az-Zukhruf 43:13-14)",
            titleUr = "سفر کی دعا",
            titleRoman = "Safar Ki Dua",
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
            romanUrdu = "Subhanal-ladhi sakhkhara lana hadha wa ma kunna lahu muqrineen, wa inna ila Rabbina lamunqaliboon",
            urduTranslation = "پاک ہے وہ ذات جس نے ہمارے لیے اس سواری کو مسخر کیا حالانکہ ہم اسے قابو کرنے والے نہ تھے، اور بے شک ہم اپنے رب ہی کی طرف لوٹنے والے ہیں۔",
            englishTranslation = "Exalted is He who has subjected this to us, and we could not have [otherwise] subdued it. And indeed we, to our Lord, will [surely] return.",
            pronunciationGuide = "Recite after saying Allahu Akbar 3 times upon boarding your vehicle.",
            recommendedCount = 1,
            reference = "Surah Az-Zukhruf 43:13-14; Sahih Muslim 1342",
            whenToRead = "When mounting a vehicle or beginning any journey.",
            audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/4338.mp3"
        ),
        // 8. Protection Duas
        DuaEntry(
            id = "dua_protection_ayatulkursi",
            category = DuaCategory.PROTECTION,
            titleEn = "Ayat-ul-Kursi (Verse of the Throne)",
            titleUr = "آیۃ الکرسی (حفاظت کا عظیم حصار)",
            titleRoman = "Ayat-ul-Kursi",
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ...",
            romanUrdu = "Allahu la ilaha illa huwal-Hayyul-Qayyum...",
            urduTranslation = "اللہ وہ ہے جس کے سوا کوئی معبود نہیں، وہ زندہ اور سب کا تھامنے والا ہے...",
            englishTranslation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of existence...",
            pronunciationGuide = "Full verse available in Quran tab (2:255) with word-by-word meanings.",
            recommendedCount = 1,
            reference = "Surah Al-Baqarah 2:255; Sahih al-Bukhari 2311",
            whenToRead = "After every Fard Salah, in the morning, evening, and before sleep.",
            audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3"
        ),
        // 9. Forgiveness Duas
        DuaEntry(
            id = "dua_forgiveness_yunus",
            category = DuaCategory.FORGIVENESS,
            titleEn = "Dua of Prophet Yunus (AS) — Surah Al-Anbiya 21:87",
            titleUr = "دعائے یونسؑ (استغفار و نجات)",
            titleRoman = "Dua-e-Yunus (AS)",
            arabicText = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            romanUrdu = "La ilaha illa Anta Subhanaka inni kuntu minaz-zalimeen",
            urduTranslation = "تیرے سوا کوئی معبود نہیں، تو پاک ہے، بے شک میں ہی قصورواروں میں سے تھا۔",
            englishTranslation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            pronunciationGuide = "Recite with humility and complete certainty in Allah's mercy.",
            recommendedCount = 33,
            reference = "Surah Al-Anbiya 21:87; Jami' at-Tirmidhi 3505 (Sahih)",
            whenToRead = "During repentance, distress, or daily Istighfar.",
            audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/2570.mp3"
        ),
        // 10. Anxiety & Difficulty Duas
        DuaEntry(
            id = "dua_anxiety_hamm",
            category = DuaCategory.ANXIETY_DIFFICULTY,
            titleEn = "Dua for Relief from Anxiety & Sorrow",
            titleUr = "غم اور پریشانی دور کرنے کی دعا",
            titleRoman = "Gham Aur Pareshani Se Nijat Ki Dua",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
            romanUrdu = "Allahumma inni a'oodhu bika minal-hammi wal-hazan, wal-'ajzi wal-kasal, wal-bukhli wal-jubn, wa dala'id-dayni wa ghalabatir-rijal",
            urduTranslation = "اے اللہ! میں غم اور حزن، عاجزی اور سستی، بخل اور بزدلی، قرض کے بوجھ اور لوگوں کے غلبے سے تیری پناہ مانگتا ہوں۔",
            englishTranslation = "O Allah, I seek refuge in You from anxiety and sorrow, weakness and laziness, miserliness and cowardice, the burden of debts and being overpowered by men.",
            pronunciationGuide = "Pause between each pair of attributes for reflection.",
            recommendedCount = 1,
            reference = "Sahih al-Bukhari 6369",
            whenToRead = "Whenever feeling overwhelmed, anxious, or burdened."
        ),
        // 11. Rain Duas
        DuaEntry(
            id = "dua_rain",
            category = DuaCategory.RAIN,
            titleEn = "Dua When It Rains",
            titleUr = "بارش کے وقت کی دعا",
            titleRoman = "Barish Ke Waqt Ki Dua",
            arabicText = "اللَّهُمَّ صَيِّبًا نَافِعًا",
            romanUrdu = "Allahumma sayyiban nafi'a",
            urduTranslation = "اے اللہ! اسے نفع بخش اور رحمت والی بارش بنا۔",
            englishTranslation = "O Allah, make it a beneficial downpour.",
            pronunciationGuide = "Double the 'y' in 'sayyiban'.",
            recommendedCount = 1,
            reference = "Sahih al-Bukhari 1032",
            whenToRead = "When rain begins falling (a blessed time when duas are accepted)."
        ),
        // 12. Mosque Duas
        DuaEntry(
            id = "dua_mosque_enter_exit",
            category = DuaCategory.MOSQUE,
            titleEn = "Entering & Leaving the Mosque",
            titleUr = "مسجد میں داخل ہونے اور نکلنے کی دعائیں",
            titleRoman = "Masjid Mein Dakhil Hone Aur Nikalne Ki Dua",
            arabicText = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ • اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
            romanUrdu = "Allahummaf-tah li abwaba rahmatik (entering, right foot) • Allahumma inni as'aluka min fadlik (leaving, left foot)",
            urduTranslation = "اے اللہ! میرے لیے اپنی رحمت کے دروازے کھول دے • (نکلتے وقت) اے اللہ! میں تجھ سے تیرے فضل کا سوال کرتا ہوں۔",
            englishTranslation = "O Allah, open the gates of Your mercy for me (entering) • O Allah, I ask You from Your bounty (leaving).",
            pronunciationGuide = "Send Salawat on the Prophet ﷺ first, then recite the dua.",
            recommendedCount = 1,
            reference = "Sahih Muslim 713",
            whenToRead = "Upon entering (right foot) and exiting (left foot) the mosque."
        ),
        // 13. Parents Duas
        DuaEntry(
            id = "dua_parents",
            category = DuaCategory.PARENTS,
            titleEn = "Quranic Dua for Parents (Surah Al-Isra 17:24)",
            titleUr = "والدین کے لیے قرآنی دعا",
            titleRoman = "Walidain Ke Liye Qurani Dua",
            arabicText = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            romanUrdu = "Rabbir-hamhuma kama rabbayani sagheera",
            urduTranslation = "اے میرے رب! ان دونوں (والدین) پر رحم فرما جیسا کہ انہوں نے بچپن میں میری پرورش کی۔",
            englishTranslation = "My Lord, have mercy upon them as they brought me up [when I was] small.",
            pronunciationGuide = "Make this a regular part of Sujood and post-Salah supplications.",
            recommendedCount = 1,
            reference = "Surah Al-Isra 17:24",
            whenToRead = "Daily after Salah and whenever remembering your parents.",
            audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/2053.mp3"
        ),
        // 14. Knowledge Duas
        DuaEntry(
            id = "dua_knowledge",
            category = DuaCategory.KNOWLEDGE,
            titleEn = "Quranic Dua for Increase in Knowledge (Surah Ta-Ha 20:114)",
            titleUr = "علم میں اضافے کی دعا",
            titleRoman = "Ilm Mein Izafay Ki Dua",
            arabicText = "رَّبِّ زِدْنِي عِلْمًا",
            romanUrdu = "Rabbi zidni 'ilma",
            urduTranslation = "اے میرے رب! میرے علم میں اضافہ فرما۔",
            englishTranslation = "My Lord, increase me in knowledge.",
            pronunciationGuide = "Pause softly on 'ilma'.",
            recommendedCount = 3,
            reference = "Surah Ta-Ha 20:114",
            whenToRead = "Before studying, reading Quran, or starting work.",
            audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/2462.mp3"
        )
    )

    fun dailyDua(dayOfYear: Int): DuaEntry = allDuas[dayOfYear % allDuas.size]
}
