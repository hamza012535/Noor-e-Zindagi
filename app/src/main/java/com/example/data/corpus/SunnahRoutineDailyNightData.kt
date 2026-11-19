package com.example.data.corpus

import com.example.model.SunnahActivity
import com.example.model.SunnahTimeSlot
import com.example.model.WordMeaning
import com.example.model.WorshipClassification

object SunnahRoutineDailyNightData {
    val activities: List<SunnahActivity> = listOf(
        // D. DAILY ACTIVITIES - EATING & DRINKING
        SunnahActivity(
            id = "eat_before_sunnah",
            timeSlot = SunnahTimeSlot.DAILY_ACTIVITIES,
            classification = WorshipClassification.GOOD_DEED_ADAB,
            titleEn = "Sunnah of Eating: Bismillah, Right Hand & Nearest Side",
            titleUr = "کھانے کی سنتیں: بسم اللہ، دایاں ہاتھ اور سامنے سے کھانا",
            titleRoman = "Khane Ki Sunnatein: Bismillah, Daayan Haath",
            subCategory = "Eating & Drinking",
            arabicText = "يَا غُلَامُ، سَمِّ اللَّهَ، وَكُلْ بِيَمِينِكَ، وَكُلْ مِمَّا يَلِيكَ",
            romanUrdu = "Ya ghulamu, sammillaha, wa kul bi-yameenika, wa kul mimma yaleek",
            urduTranslation = "اے بچے! اللہ کا نام لو، اپنے دائیں ہاتھ سے کھاؤ اور اپنے سامنے سے کھاؤ۔",
            englishTranslation = "O young boy, mention the Name of Allah, eat with your right hand, and eat from what is directly in front of you.",
            wordMeanings = listOf(
                WordMeaning("سَمِّ اللَّهَ", "Sammillah", "Say Bismillah", "اللہ کا نام لو", "س-م-و", "Imperative"),
                WordMeaning("بِيَمِينِكَ", "Bi-yameenik", "With your right hand", "اپنے دائیں ہاتھ سے", "ي-م-ن", "Prepositional"),
                WordMeaning("مِمَّا يَلِيكَ", "Mimma yaleek", "From what is near you", "جو تمہارے سامنے ہے", "و-ل-ي", "Relative clause")
            ),
            reference = "Sahih al-Bukhari 5376; Sahih Muslim 2022; At-Tirmidhi 1858 (if forgotten: Bismillahi awwalahu wa akhirahu)",
            explanationEn = "Eating becomes an act of worship when done with gratitude, cleanliness, moderation, and prophetic manners.",
            explanationUr = "مسنون آداب کے ساتھ کھانا پینا بھی عبادت اور برکت کا باعث بن جاتا ہے۔",
            howToPerformEn = "1. Wash hands before eating.\n2. Sit modestly and say Bismillah aloud (if forgotten, say 'Bismillahi awwalahu wa akhirahu').\n3. Eat with the right hand from the portion closest to you.\n4. Drink water seated in three breaths, pausing outside the cup.",
            howToPerformUr = "1. کھانے سے پہلے ہاتھ دھوئیں۔\n2. بسم اللہ پڑھیں (بھول جائیں تو بسم اللہ اولہ و آخرہ کہیں)۔\n3. دائیں ہاتھ سے اپنے سامنے سے کھائیں۔\n4. بیٹھ کر تین سانسوں میں پانی پئیں اور برتن میں سانس نہ لیں۔",
            recommendedTime = "Every meal & drink",
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "eat_after_dua",
            timeSlot = SunnahTimeSlot.DAILY_ACTIVITIES,
            classification = WorshipClassification.RECOMMENDED_NAFL,
            titleEn = "Dua After Eating & Drinking Etiquette",
            titleUr = "کھانے کے بعد کی دعا اور پینے کے آداب",
            titleRoman = "Khane Ke Baad Ki Dua Aur Peene Ke Aadaab",
            subCategory = "Eating & Drinking",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَٰذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
            romanUrdu = "Alhamdu lillahil-ladhi at'amani hadha wa razaqanihi min ghayri hawlin minni wa la quwwah",
            urduTranslation = "تمام تعریف اللہ کے لیے ہے جس نے مجھے یہ کھانا کھلایا اور میری کسی طاقت و قوت کے بغیر مجھے یہ رزق عطا فرمایا۔",
            englishTranslation = "All praise is for Allah who fed me this and provided it for me without any might or power on my part.",
            wordMeanings = listOf(
                WordMeaning("أَطْعَمَنِي", "At'amani", "He fed me", "اس نے مجھے کھلایا", "ط-ع-م", "Form IV verb"),
                WordMeaning("وَرَزَقَنِيهِ", "Wa razaqanihi", "And provided it to me", "اور مجھے عطا کیا", "ر-ز-ق", "Past verb"),
                WordMeaning("مِنْ غَيْرِ حَوْلٍ", "Min ghayri hawlin", "Without any might", "بغیر میری طاقت کے", "ح-و-ل", "Prepositional")
            ),
            reference = "Jami' at-Tirmidhi 3458 (Hasan); Sahih Muslim 2028 (drink seated in 3 sips)",
            explanationEn = "Whoever recites this dua after eating is forgiven their past minor sins. Never criticize food; if you like it, eat it, and if not, leave it gently.",
            explanationUr = "کھانے کے بعد یہ دعا پڑھنے سے پچھلے صغیرہ گناہ معاف ہو جاتے ہیں۔ کھانے میں کبھی عیب نہ نکالیں۔",
            howToPerformEn = "1. Clean your fingers and plate without wasting food.\n2. Recite the post-meal dua.\n3. Wash hands and rinse mouth.",
            howToPerformUr = "1. برتن اور انگلیاں صاف کریں، رزق ضائع نہ کریں۔\n2. کھانے کے بعد کی دعا پڑھیں۔\n3. ہاتھ دھوئیں اور کلی کریں۔",
            recommendedTime = "After every meal",
            isCoreChecklist = false
        ),

        // D. DAILY ACTIVITIES - CLOTHING
        SunnahActivity(
            id = "clothing_dua",
            timeSlot = SunnahTimeSlot.DAILY_ACTIVITIES,
            classification = WorshipClassification.GOOD_DEED_ADAB,
            titleEn = "Wearing Clothes From the Right Side & Clothing Dua",
            titleUr = "لباس پہننے کی دعا اور دائیں طرف سے پہننا",
            titleRoman = "Kapray Pehnne Ki Dua Aur Daayein Se Shuru",
            subCategory = "Clothing",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي كَسَانِي هَٰذَا الثَّوْبَ وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
            romanUrdu = "Alhamdu lillahil-ladhi kasani hadhath-thawba wa razaqanihi min ghayri hawlin minni wa la quwwah",
            urduTranslation = "تمام تعریف اللہ کے لیے ہے جس نے مجھے یہ لباس پہنایا اور میری کسی طاقت و قوت کے بغیر مجھے عطا فرمایا۔",
            englishTranslation = "All praise is for Allah who clothed me with this garment and provided it for me without any might or power on my part.",
            wordMeanings = listOf(
                WordMeaning("كَسَانِي", "Kasani", "Clothed me", "مجھے پہنایا", "ك-س-و", "Past verb"),
                WordMeaning("الثَّوْبَ", "Ath-thawba", "The garment", "یہ لباس", "ث-و-ب", "Definite noun")
            ),
            reference = "Sunan Abi Dawud 4023; Sahih al-Bukhari 168 (start from right side)",
            explanationEn = "The Prophet ﷺ loved starting from the right when putting on shoes, garments, and purifying himself, while maintaining modesty and cleanliness.",
            explanationUr = "نبی کریم ﷺ جوتا اور لباس پہننے اور طہارت میں دائیں جانب سے آغاز پسند فرماتے تھے۔",
            howToPerformEn = "1. Put the right arm/leg in first.\n2. Recite the clothing dua.\n3. Ensure garments are clean, modest, and free of arrogance.",
            howToPerformUr = "1. پہلے دایاں بازو یا پاؤں ڈالیں۔\n2. لباس پہننے کی دعا پڑھیں۔\n3. سادگی، پاکیزگی اور حیا کا خیال رکھیں۔",
            recommendedTime = "When getting dressed",
            isCoreChecklist = false
        ),

        // D. DAILY ACTIVITIES - BATHROOM
        SunnahActivity(
            id = "bathroom_etiquette",
            timeSlot = SunnahTimeSlot.DAILY_ACTIVITIES,
            classification = WorshipClassification.GOOD_DEED_ADAB,
            titleEn = "Bathroom Duas & Cleanliness Etiquette",
            titleUr = "بیت الخلا میں داخل ہونے اور نکلنے کی دعائیں",
            titleRoman = "Washroom Jane Aur Nikalne Ki Duain",
            subCategory = "Bathroom",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ • غُفْرَانَكَ",
            romanUrdu = "Allahumma inni a'oodhu bika minal-khubuthi wal-khaba'ith (entering) • Ghufranak (leaving)",
            urduTranslation = "اے اللہ! میں ناپاک جنوں (نر و مادہ) سے تیری پناہ مانگتا ہوں • (نکلتے وقت) تیری بخشش چاہتا ہوں۔",
            englishTranslation = "O Allah, I seek refuge in You from male and female impure devils (before entering) • I seek Your forgiveness (upon leaving).",
            wordMeanings = listOf(
                WordMeaning("الْخُبُثِ وَالْخَبَائِثِ", "Al-khubuthi wal-khaba'ith", "Impure male & female devils", "خبیث جن اور جنیاں", "خ-ب-ث", "Paired plurals"),
                WordMeaning("غُفْرَانَكَ", "Ghufranak", "Your forgiveness", "تیری بخشش", "غ-ف-ر", "Verbal noun")
            ),
            reference = "Sahih al-Bukhari 142; Sunan Abi Dawud 30 (Ghufranak)",
            explanationEn = "Seeking Allah's protection before entering and seeking forgiveness afterward safeguards privacy, spiritual protection, and physical purity.",
            explanationUr = "بیت الخلا سے پہلے حفاظت کی دعا اور باہر آ کر غفرانک کہنا طہارت اور شیطانی اثرات سے حفاظت کا مسنون طریقہ ہے۔",
            howToPerformEn = "1. Recite the entering dua BEFORE stepping inside.\n2. Enter with the left foot.\n3. Maintain strict cleanliness (Istinja) with water.\n4. Exit with the right foot and say 'Ghufranak'.",
            howToPerformUr = "1. اندر داخل ہونے سے پہلے دعا پڑھیں اور بایاں قدم اندر رکھیں۔\n2. پانی سے اچھی طرح پاکیزگی حاصل کریں۔\n3. دایاں قدم باہر رکھ کر غفرانک کہیں۔",
            recommendedTime = "Every bathroom visit",
            isCoreChecklist = false
        ),

        // D. DAILY ACTIVITIES - SOCIAL LIFE
        SunnahActivity(
            id = "social_sunnah",
            timeSlot = SunnahTimeSlot.DAILY_ACTIVITIES,
            classification = WorshipClassification.GOOD_DEED_ADAB,
            titleEn = "Social Sunnah: Salam, Smiling, Kind Speech & Family Ties",
            titleUr = "معاشرتی سنتیں: سلام، مسکراہٹ، نرم گفتگو، صلہ رحمی",
            titleRoman = "Muasharti Sunnatein: Salam, Muskurahat, Husn-e-Akhlaq",
            subCategory = "Social Life",
            arabicText = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            romanUrdu = "Tabassumuka fi wajhi akheeka laka sadaqah",
            urduTranslation = "تمہارا اپنے بھائی کے سامنے مسکرانا بھی تمہارے لیے صدقہ ہے۔",
            englishTranslation = "Your smiling in the face of your brother is charity for you.",
            wordMeanings = listOf(
                WordMeaning("تَبَسُّمُكَ", "Tabassumuka", "Your smile", "تمہارا مسکرانا", "ب-س-م", "Verbal noun"),
                WordMeaning("صَدَقَةٌ", "Sadaqah", "Charity", "صدقہ", "ص-د-ق", "Indefinite noun")
            ),
            reference = "Jami' at-Tirmidhi 1956 (Hasan); Sahih al-Bukhari 6018 (good speech), 5984 (Silat-ur-Rahim)",
            explanationEn = "Islam transforms social interaction into worship: spreading Salam, smiling, speaking gently, visiting the sick, helping neighbors, upholding family ties, and guarding the tongue from lying and backbiting (49:12).",
            explanationUr = "سلام پھیلانا، مسکرانا، خوش اخلاقی سے بات کرنا، بیمار کی عیادت، رشتہ داروں سے صلہ رحمی اور جھوٹ و غیبت سے بچنا عظیم سنتیں ہیں۔",
            howToPerformEn = "1. Initiate 'Assalamu Alaykum' with a warm smile.\n2. Check on parents or relatives.\n3. Speak truthfully and avoid gossip or harshness.\n4. Help someone in need or visit a sick friend.",
            howToPerformUr = "1. مسکراتے ہوئے السلام علیکم میں پہل کریں۔\n2. والدین اور رشتہ داروں کی خیر خبر لیں۔\n3. سچ بولیں، غیبت و طعنہ زنی سے بچیں۔",
            recommendedTime = "Throughout daily interactions",
            isCoreChecklist = true
        ),

        // D. DAILY ACTIVITIES - WORK & STUDY
        SunnahActivity(
            id = "work_study_sunnah",
            timeSlot = SunnahTimeSlot.DAILY_ACTIVITIES,
            classification = WorshipClassification.GOOD_DEED_ADAB,
            titleEn = "Work & Study Ethics: Bismillah, Honesty & Beneficial Knowledge",
            titleUr = "کام اور تعلیم کے آداب: بسم اللہ، امانت داری، علمِ نافع",
            titleRoman = "Kaam Aur Taleem Ke Aadaab: Imandari o Ilm",
            subCategory = "Work & Study",
            arabicText = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
            romanUrdu = "Allahumma inni as'aluka 'ilman nafi'an, wa rizqan tayyiban, wa 'amalan mutaqabbala",
            urduTranslation = "اے اللہ! میں تجھ سے نفع بخش علم، پاکیزہ رزق اور مقبول عمل کا سوال کرتا ہوں۔",
            englishTranslation = "O Allah, I ask You for beneficial knowledge, goodly (halal) provision, and acceptable deeds.",
            wordMeanings = listOf(
                WordMeaning("عِلْمًا نَافِعًا", "'Ilman nafi'an", "Beneficial knowledge", "نفع دینے والا علم", "ع-ل-م", "Noun + adjective"),
                WordMeaning("رِزْقًا طَيِّبًا", "Rizqan tayyiban", "Pure/Halal provision", "پاکیزہ رزق", "ر-ز-ق", "Noun + adjective"),
                WordMeaning("مُتَقَبَّلًا", "Mutaqabbala", "Accepted", "قبول کیا گیا", "ق-ب-ل", "Passive participle")
            ),
            reference = "Sunan Ibn Majah 925 (Sahih); Sahih al-Bukhari 33 (fulfilling trusts & promises)",
            explanationEn = "Beginning work or study with Bismillah, keeping promises, dealing honestly, respecting colleagues and teachers, and seeking halal income turn livelihood into Ibadah.",
            explanationUr = "بسم اللہ سے کام کا آغاز، وعدے کی پابندی، دیانتداری، دوسروں کا احترام اور حلال رزق کی طلب دنیاوی کام کو عبادت بنا دیتی ہے۔",
            howToPerformEn = "1. Begin work/study with Bismillah and this dua.\n2. Fulfill deadlines and promises honestly.\n3. Treat colleagues, clients, and teachers with respect.",
            howToPerformUr = "1. کام یا مطالعہ سے پہلے بسم اللہ اور یہ دعا پڑھیں۔\n2. وقت اور وعدے کی پابندی و امانت داری کریں۔",
            recommendedTime = "Start of work or study",
            isCoreChecklist = false
        ),

        // E. PRAYER ROUTINE
        SunnahActivity(
            id = "prayer_dhuhr",
            timeSlot = SunnahTimeSlot.PRAYER,
            classification = WorshipClassification.OBLIGATORY,
            titleEn = "Dhuhr Prayer (4 Sunnah + 4 Fard + 2 Sunnah)",
            titleUr = "نمازِ ظہر (4 سنت + 4 فرض + 2 سنت)",
            titleRoman = "Namaz-e-Zuhr (4 Sunnat + 4 Farz + 2 Sunnat)",
            subCategory = "Prayer Routine",
            arabicText = "حَافِظُوا عَلَى الصَّلَوَاتِ وَالصَّلَاةِ الْوُسْطَىٰ وَقُومُوا لِلَّهِ قَانِتِينَ",
            romanUrdu = "Hafizoo 'alas-salawati was-salatil-wusta wa qoomoo lillahi qaniteen",
            urduTranslation = "تمام نمازوں کی حفاظت کرو اور خاص کر درمیانی نماز کی، اور اللہ کے سامنے ادب سے کھڑے ہو جاؤ۔",
            englishTranslation = "Maintain with care the [obligatory] prayers and [in particular] the middle prayer and stand before Allah, devoutly obedient.",
            wordMeanings = listOf(
                WordMeaning("حَافِظُوا", "Hafizoo", "Guard carefully", "حفاظت کرو", "ح-ف-ظ", "Form III imperative"),
                WordMeaning("قَانِتِينَ", "Qaniteen", "Devoutly obedient", "فرمانبرداری سے کھڑے", "ق-ن-ت", "Active participle")
            ),
            reference = "Surah Al-Baqarah 2:238; Sahih Muslim 728 (12 daily Sunnah Rawatib)",
            explanationEn = "Dhuhr brings midday spiritual renewal. Its 4 Sunnah before and 2 Sunnah after are part of the 12 Sunnah Muakkadah Rawatib.",
            explanationUr = "ظہر کی نماز دن کے وسط میں روحانی تازگی دیتی ہے۔ اس کی 4 قبل از فرض اور 2 بعد از فرض سنتیں 12 سننِ رواتب میں شامل ہیں۔",
            howToPerformEn = "1. Perform Wudu calmly.\n2. Offer 4 Rak'ahs Sunnah Muakkadah.\n3. Offer 4 Rak'ahs Fard.\n4. Offer 2 Rak'ahs Sunnah Muakkadah and post-Salah Azkar.",
            howToPerformUr = "1. وضو کریں۔\n2. 4 رکعت سنتِ مؤکدہ، پھر 4 رکعت فرض، پھر 2 رکعت سنت اور نماز کے بعد کے اذکار پڑھیں۔",
            recommendedTime = "After midday zenith until Asr",
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "prayer_asr",
            timeSlot = SunnahTimeSlot.PRAYER,
            classification = WorshipClassification.OBLIGATORY,
            titleEn = "Asr Prayer (4 Fard)",
            titleUr = "نمازِ عصر (4 فرض)",
            titleRoman = "Namaz-e-Asr (4 Farz)",
            subCategory = "Prayer Routine",
            arabicText = "مَنْ صَلَّى الْبَرْدَيْنِ دَخَلَ الْجَنَّةَ",
            romanUrdu = "Man sallal-bardayni dakhalal-jannah",
            urduTranslation = "جس نے دو ٹھنڈے وقت کی نمازیں (فجر اور عصر) پابندی سے پڑھیں وہ جنت میں داخل ہوگا۔",
            englishTranslation = "Whoever prays the two cool prayers (Fajr and Asr) will enter Paradise.",
            wordMeanings = listOf(
                WordMeaning("الْبَرْدَيْنِ", "Al-Bardayn", "The two cool prayers (Fajr & Asr)", "دو ٹھنڈے اوقات کی نمازیں", "ب-ر-د", "Dual noun"),
                WordMeaning("دَخَلَ الْجَنَّةَ", "Dakhalal-jannah", "Entered Paradise", "جنت میں داخل ہوا", "د-خ-ل", "Past verb")
            ),
            reference = "Sahih al-Bukhari 574; Sahih Muslim 635",
            explanationEn = "Many scholars identify Asr as the 'Middle Prayer' (Al-Salat al-Wusta). Four voluntary Rak'ahs before Asr are also recommended (At-Tirmidhi 430).",
            explanationUr = "جمہور علماء کے نزدیک صلاۃ الوسطیٰ سے مراد عصر کی نماز ہے۔ عصر سے پہلے 4 رکعت نفل بھی مستحب ہیں۔",
            howToPerformEn = "1. Optionally pray 4 Rak'ahs Ghayr Muakkadah before Asr.\n2. Pray 4 Rak'ahs Fard before sunset with full focus.",
            howToPerformUr = "1. ممکن ہو تو فرض سے پہلے 4 رکعت نفل پڑھیں۔\n2. غروبِ آفتاب سے پہلے 4 رکعت فرض ادا کریں۔",
            recommendedTime = "Late afternoon until Sunset",
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "prayer_maghrib",
            timeSlot = SunnahTimeSlot.PRAYER,
            classification = WorshipClassification.OBLIGATORY,
            titleEn = "Maghrib Prayer (3 Fard + 2 Sunnah)",
            titleUr = "نمازِ مغرب (3 فرض + 2 سنت)",
            titleRoman = "Namaz-e-Maghrib (3 Farz + 2 Sunnat)",
            subCategory = "Prayer Routine",
            arabicText = "سُبْحَانَ اللَّهِ • الْحَمْدُ لِلَّهِ • اللَّهُ أَكْبَرُ (33x each after Salah)",
            romanUrdu = "SubhanAllah (33x), Alhamdulillah (33x), Allahu Akbar (33x) + La ilaha illallah...",
            urduTranslation = "نماز کے بعد 33 بار سبحان اللہ، 33 بار الحمدللہ، 33 بار اللہ اکبر اور ایک بار کلمہ توحید۔",
            englishTranslation = "Glory be to Allah (33x), Praise be to Allah (33x), Allah is the Greatest (33x), completed to 100 with Tawheed.",
            wordMeanings = listOf(
                WordMeaning("سُبْحَانَ اللَّهِ", "SubhanAllah", "Glory be to Allah", "اللہ پاک ہے", "س-ب-ح", "Tasbih"),
                WordMeaning("اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allah is the Greatest", "اللہ سب سے بڑا ہے", "ك-ب-ر", "Takbir")
            ),
            reference = "Sahih Muslim 597 (Post-Salah Tasbih); Sahih Muslim 728 (2 Sunnah after Maghrib)",
            explanationEn = "Maghrib should be offered promptly after sunset, followed by 2 Rak'ahs Sunnah Muakkadah and post-prayer Azkar.",
            explanationUr = "غروبِ آفتاب کے فوراً بعد مغرب کی نماز ادا کرنی چاہیے اور اس کے بعد 2 رکعت سنتِ مؤکدہ و اذکار پڑھنے چاہئیں۔",
            howToPerformEn = "1. Pray 3 Rak'ahs Fard promptly after Maghrib entry.\n2. Pray 2 Rak'ahs Sunnah Muakkadah.\n3. Recite Ayat-ul-Kursi & Tasbih after Salah.",
            howToPerformUr = "1. غروبِ آفتاب کے فوراً بعد 3 فرض پڑھیں۔\n2. پھر 2 رکعت سنتِ مؤکدہ ادا کریں۔\n3. اس کے بعد آیۃ الکرسی اور تسبیحات پڑھیں۔",
            recommendedTime = "Immediately after Sunset",
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "prayer_isha_witr",
            timeSlot = SunnahTimeSlot.PRAYER,
            classification = WorshipClassification.OBLIGATORY,
            titleEn = "Isha Prayer (4 Fard + 2 Sunnah) & Witr",
            titleUr = "نمازِ عشاء (4 فرض + 2 سنت + وتر)",
            titleRoman = "Namaz-e-Isha (4 Farz + 2 Sunnat + Witr)",
            subCategory = "Prayer Routine",
            arabicText = "اجْعَلُوا آخِرَ صَلَاتِكُمْ بِاللَّيْلِ وِتْرًا",
            romanUrdu = "Ij'aloo akhira salatikum bil-layli witra",
            urduTranslation = "رات کی اپنی آخری نماز وتر بناؤ۔",
            englishTranslation = "Make the last of your prayers at night the Witr prayer.",
            wordMeanings = listOf(
                WordMeaning("آخِرَ صَلَاتِكُمْ", "Akhira salatikum", "The last of your prayers", "تمہاری آخری نماز", "أ-خ-ر", "Construct"),
                WordMeaning("وِتْرًا", "Witra", "Odd-numbered (Witr)", "طاق رکعت (وتر)", "و-ت-ر", "Noun")
            ),
            reference = "Sahih al-Bukhari 998; Sahih Muslim 751 (Witr)",
            explanationEn = "Isha completes the five daily Fard prayers, followed by 2 Sunnah Muakkadah and Witr (if not waking for Tahajjud).",
            explanationUr = "عشاء پانچویں فرض نماز ہے، اس کے بعد 2 سنتِ مؤکدہ اور وتر ادا کیے جاتے ہیں۔ اگر تہجد کا یقین ہو تو وتر آخر شب میں بھی پڑھے جا سکتے ہیں۔",
            howToPerformEn = "1. Offer 4 Rak'ahs Fard and 2 Rak'ahs Sunnah Muakkadah.\n2. Offer Witr (1 or 3 Rak'ahs per your Fiqh school) before sleeping if unsure of waking for Tahajjud.",
            howToPerformUr = "1. 4 فرض اور 2 سنتِ مؤکدہ ادا کریں۔\n2. سونے سے پہلے وتر ادا کریں۔",
            recommendedTime = "Night until before Fajr",
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "prayer_tahajjud",
            timeSlot = SunnahTimeSlot.PRAYER,
            classification = WorshipClassification.RECOMMENDED_NAFL,
            titleEn = "Tahajjud (Night Vigil Prayer)",
            titleUr = "نمازِ تہجد (قیام اللیل)",
            titleRoman = "Namaz-e-Tahajjud",
            subCategory = "Prayer Routine",
            arabicText = "أَفْضَلُ الصَّلَاةِ بَعْدَ الْفَرِيضَةِ صَلَاةُ اللَّيْلِ",
            romanUrdu = "Afdalus-salati ba'dal-fareedati salatul-layl",
            urduTranslation = "فرض نماز کے بعد سب سے افضل نماز رات (تہجد) کی نماز ہے۔",
            englishTranslation = "The best prayer after the obligatory prayers is the night prayer.",
            wordMeanings = listOf(
                WordMeaning("أَفْضَلُ", "Afdalu", "The most virtuous", "سب سے افضل", "ف-ض-ل", "Superlative"),
                WordMeaning("صَلَاةُ اللَّيْلِ", "Salatul-layl", "Night prayer", "رات کی نماز", "ل-ي-ل", "Idafa")
            ),
            reference = "Sahih Muslim 1163; Sahih al-Bukhari 1145 (last third of the night)",
            explanationEn = "In the last third of the night, Allah descends in a manner befitting His Majesty and asks: 'Who is calling upon Me that I may answer him?'",
            explanationUr = "رات کے آخری تہائی حصے میں اللہ کی رحمت خاص طور پر متوجہ ہوتی ہے اور دعا کرنے والوں کی دعائیں قبول کی جاتی ہیں۔",
            howToPerformEn = "1. Wake in the last third of the night before Fajr.\n2. Perform Wudu and pray 2 to 8 Rak'ahs in pairs of two.\n3. Make heartfelt personal Dua.",
            howToPerformUr = "1. فجر سے پہلے رات کے آخری تہائی حصے میں جاگیں۔\n2. دو دو رکعت کر کے نفل پڑھیں اور دل سے دعا مانگیں۔",
            recommendedTime = "Last third of night before Fajr",
            isCoreChecklist = false
        ),

        // F. EVENING ROUTINE
        SunnahActivity(
            id = "evening_azkar",
            timeSlot = SunnahTimeSlot.EVENING,
            classification = WorshipClassification.RECOMMENDED_NAFL,
            titleEn = "Evening Azkar, Ayat-ul-Kursi, 3 Quls, Istighfar & Durood",
            titleUr = "شام کے اذکار، آیۃ الکرسی، تین قل، استغفار اور درود",
            titleRoman = "Shaam Ke Azkar, Ayatul Kursi Aur 3 Qul",
            subCategory = "Evening Routine",
            arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            romanUrdu = "Amsayna wa amsal-mulku lillah, wal-hamdu lillah, la ilaha illallahu wahdahu la shareeka lah",
            urduTranslation = "ہم نے شام کی اور اللہ کی تمام بادشاہت نے شام کی، تمام تعریف اللہ کے لیے ہے، اللہ کے سوا کوئی معبود نہیں۔",
            englishTranslation = "We have reached the evening and at this very time all sovereignty belongs to Allah, and all praise is for Allah.",
            wordMeanings = listOf(
                WordMeaning("أَمْسَيْنَا", "Amsayna", "We entered the evening", "ہم نے شام کی", "م-س-ي", "Form IV verb"),
                WordMeaning("أَمْسَى الْمُلْكُ", "Amsal-mulku", "The sovereignty reached evening", "بادشاہی نے شام کی", "م-ل-ك", "Verb + subject")
            ),
            reference = "Sahih Muslim 2723; Sunan Abi Dawud 5082 (3 Quls 3x); At-Tirmidhi 3575",
            explanationEn = "Just as Morning Azkar protect through the day, Evening Azkar (read between Asr and Maghrib, or after Maghrib) shield the believer through the night.",
            explanationUr = "جس طرح صبح کے اذکار دن بھر حفاظت کرتے ہیں، اسی طرح شام کے اذکار (عصر کے بعد یا مغرب کے بعد) رات بھر کی حفاظت کا ذریعہ ہیں۔",
            howToPerformEn = "1. Recite evening remembrance (Amsayna...).\n2. Recite Ayat-ul-Kursi (1x).\n3. Recite Surah Al-Ikhlas, Al-Falaq, An-Nas (3x each).\n4. Recite Istighfar & Durood (10x+).",
            howToPerformUr = "1. شام کی دعا (امسینا) پڑھیں۔\n2. آیۃ الکرسی (1 بار) اور تین قل (3-3 بار) پڑھیں۔\n3. استغفار اور درود شریف پڑھیں۔",
            recommendedTime = "Between Asr & Maghrib (or early evening)",
            isCoreChecklist = true
        ),

        // G. BEFORE SLEEPING (10-STEP BEDTIME SUNNAH)
        SunnahActivity(
            id = "sleep_1_wudu",
            timeSlot = SunnahTimeSlot.SLEEPING,
            classification = WorshipClassification.RECOMMENDED_NAFL,
            titleEn = "1. Perform Wudu Before Sleeping",
            titleUr = "۱۔ سونے سے پہلے وضو کرنا",
            titleRoman = "1. Sone Se Pehle Wudu Karna",
            subCategory = "Bedtime Sunnah",
            arabicText = "إِذَا أَتَيْتَ مَضْجَعَكَ فَتَوَضَّأْ وُضُوءَكَ لِلصَّلَاةِ",
            romanUrdu = "Idha atayta madja'aka fatawadda wudu'aka lis-salah",
            urduTranslation = "جب تم اپنے بستر پر آؤ تو نماز جیسا وضو کرو۔",
            englishTranslation = "When you go to your bed, perform ablution like that for the prayer.",
            wordMeanings = listOf(
                WordMeaning("مَضْجَعَكَ", "Madja'ak", "Your sleeping place", "تمہارا بستر", "ض-ج-ع", "Noun of place"),
                WordMeaning("فَتَوَضَّأْ", "Fatawadda", "Then perform Wudu", "پھر وضو کرو", "و-ض-أ", "Form V imperative")
            ),
            reference = "Sahih al-Bukhari 247; Sahih Muslim 2710",
            explanationEn = "Sleeping in a state of Wudu keeps the believer pure; an angel stays by their garment and prays: 'O Allah, forgive Your servant, for he slept in purity' (Ibn Hibban).",
            explanationUr = "باوضو سونے سے فرشتہ ساری رات بندے کے لیے مغفرت کی دعا کرتا رہتا ہے۔",
            howToPerformEn = "Perform a full, calm Wudu shortly before getting into bed.",
            howToPerformUr = "بستر پر جانے سے پہلے مکمل وضو کریں۔",
            recommendedTime = "Before getting into bed",
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "sleep_2_shake_bed",
            timeSlot = SunnahTimeSlot.SLEEPING,
            classification = WorshipClassification.GOOD_DEED_ADAB,
            titleEn = "2. Shake Out / Dust the Bedding Three Times",
            titleUr = "۲۔ سونے سے پہلے بستر جھاڑنا",
            titleRoman = "2. Bistar Ko Teen Baar Jharna",
            subCategory = "Bedtime Sunnah",
            arabicText = "إِذَا أَوَىٰ أَحَدُكُمْ إِلَىٰ فِرَاشِهِ فَلْيَنْفُضْ فِرَاشَهُ بِدَاخِلَةِ إِزَارِهِ",
            romanUrdu = "Idha awa ahadukum ila firashihi falyanfud firashahu bidakhilati izarih",
            urduTranslation = "جب تم میں سے کوئی اپنے بستر پر آئے تو اپنے تہبند (یا کپڑے) کے کونے سے بستر کو جھاڑ لے۔",
            englishTranslation = "When any one of you goes to bed, let him dust off his bed with the inner edge of his garment, for he does not know what came onto it after he left.",
            wordMeanings = listOf(
                WordMeaning("فِرَاشِهِ", "Firashihi", "His bed", "اس کا بستر", "ف-ر-ش", "Noun"),
                WordMeaning("فَلْيَنْفُضْ", "Falyanfud", "Let him dust/shake", "اسے چاہیے کہ جھاڑے", "ن-ف-ض", "Jussive verb")
            ),
            reference = "Sahih al-Bukhari 6320; Sahih Muslim 2714",
            explanationEn = "A practical and spiritual Sunnah protecting from insects or dust while reminding us to say Bismillah as we prepare to rest.",
            explanationUr = "یہ سنت بستر کی صفائی اور کسی بھی موذی چیز سے حفاظت کا بہترین نبوی طریقہ ہے۔",
            howToPerformEn = "Dust or shake the bedsheet gently three times while saying Bismillah.",
            howToPerformUr = "بسم اللہ کہہ کر چادر یا کپڑے سے بستر کو تین مرتبہ جھاڑ لیں۔",
            recommendedTime = "Before lying down",
            isCoreChecklist = false
        ),
        SunnahActivity(
            id = "sleep_3_right_side",
            timeSlot = SunnahTimeSlot.SLEEPING,
            classification = WorshipClassification.GOOD_DEED_ADAB,
            titleEn = "3. Lie & Sleep on the Right Side",
            titleUr = "۳۔ دائیں کروٹ پر سونا",
            titleRoman = "3. Daayein Karwat Par Sona",
            subCategory = "Bedtime Sunnah",
            arabicText = "ثُمَّ اضْطَجِعْ عَلَىٰ شِقِّكَ الْأَيْمَنِ",
            romanUrdu = "Thummad-taji' 'ala shiqqikal-ayman",
            urduTranslation = "پھر اپنی دائیں کروٹ پر لیٹ جاؤ۔",
            englishTranslation = "Then lie down on your right side.",
            wordMeanings = listOf(
                WordMeaning("اضْطَجِعْ", "Idtaji'", "Lie down", "لیٹ جاؤ", "ض-ج-ع", "Form VIII imperative"),
                WordMeaning("شِقِّكَ الْأَيْمَنِ", "Shiqqikal-ayman", "Your right side", "اپنی دائیں کروٹ", "ش-ق-ق", "Idafa")
            ),
            reference = "Sahih al-Bukhari 247, 6315; Sahih Muslim 2710",
            explanationEn = "The Prophet ﷺ would lie on his right side, placing his right hand under his right cheek while reciting the sleeping dua.",
            explanationUr = "نبی کریم ﷺ دائیں کروٹ لیٹ کر اپنا دایاں ہاتھ دائیں رخسار کے نیچے رکھتے تھے۔",
            howToPerformEn = "Lie on your right side, optionally placing your right palm beneath your right cheek.",
            howToPerformUr = "دائیں کروٹ لیٹ کر دایاں ہاتھ گال کے نیچے رکھیں۔",
            recommendedTime = "Upon lying down",
            isCoreChecklist = false
        ),
        SunnahActivity(
            id = "sleep_4_dua",
            timeSlot = SunnahTimeSlot.SLEEPING,
            classification = WorshipClassification.RECOMMENDED_NAFL,
            titleEn = "4. Recite the Sleeping Dua",
            titleUr = "۴۔ سونے کی مسنون دعا پڑھنا",
            titleRoman = "4. Sone Ki Masnoon Dua Parhna",
            subCategory = "Bedtime Sunnah",
            arabicText = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
            romanUrdu = "Bismikallahumma amootu wa ahya",
            urduTranslation = "اے اللہ! تیرے ہی نام کے ساتھ میں مرتا ہوں (سوتا ہوں) اور زندہ ہوتا ہوں (جاگتا ہوں)۔",
            englishTranslation = "In Your Name, O Allah, I die and I live.",
            wordMeanings = listOf(
                WordMeaning("بِاسْمِكَ", "Bismika", "In Your Name", "تیرے نام سے", "س-م-و", "Preposition + noun"),
                WordMeaning("أَمُوتُ", "Amootu", "I die (sleep)", "میں مرتا/سوتا ہوں", "م-و-ت", "Present verb"),
                WordMeaning("وَأَحْيَا", "Wa ahya", "And I live", "اور میں زندہ ہوتا ہوں", "ح-ي-ي", "Present verb")
            ),
            reference = "Sahih al-Bukhari 6312, 6324; also Sahih al-Bukhari 247 (Allahumma aslamtu nafsi ilayk...)",
            explanationEn = "Entrusting one's soul to Allah before sleep ensures peace of mind and ends the day in remembrance.",
            explanationUr = "سوتے وقت اپنی جان اللہ کے سپرد کر دینا دل کے اطمینان اور ایمان پر خاتمے کا ذریعہ ہے۔",
            howToPerformEn = "Recite 'Bismikallahumma amootu wa ahya' and/or 'Allahumma aslamtu nafsi ilayk...' upon lying down.",
            howToPerformUr = "دائیں کروٹ لیٹ کر سونے کی دعا پڑھیں۔",
            recommendedTime = "Before sleeping",
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "sleep_5_ayatul_kursi",
            timeSlot = SunnahTimeSlot.SLEEPING,
            classification = WorshipClassification.RECOMMENDED_NAFL,
            titleEn = "5. Recite Ayat-ul-Kursi Before Sleeping",
            titleUr = "۵۔ سونے سے پہلے آیۃ الکرسی پڑھنا",
            titleRoman = "5. Sone Se Pehle Ayatul Kursi Parhna",
            subCategory = "Bedtime Sunnah",
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ",
            romanUrdu = "Allahu la ilaha illa huwal-Hayyul-Qayyum...",
            urduTranslation = "اللہ وہ ہے جس کے سوا کوئی معبود نہیں، وہ زندہ اور سب کا تھامنے والا ہے، نہ اسے اونگھ آتی ہے نہ نیند۔",
            englishTranslation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of existence. Neither drowsiness overtakes Him nor sleep.",
            wordMeanings = listOf(
                WordMeaning("الْحَيُّ الْقَيُّومُ", "Al-Hayyul-Qayyum", "Ever-Living, Sustainer", "زندہ و قائم رکھنے والا", "ح-ي-ي / ق-و-م", "Divine Names")
            ),
            reference = "Sahih al-Bukhari 2311, 5010",
            explanationEn = "A guardian angel from Allah stays with the one who recites Ayat-ul-Kursi in bed, and Shaytan cannot come near until morning.",
            explanationUr = "جو شخص بستر پر آیۃ الکرسی پڑھ کر سوتا ہے، اللہ کی طرف سے صبح تک اس پر ایک محافظ مقرر رہتا ہے۔",
            howToPerformEn = "Recite complete Ayat-ul-Kursi (Surah Al-Baqarah 2:255) once in bed.",
            howToPerformUr = "بستر پر لیٹ کر مکمل آیۃ الکرسی ایک بار تلاوت کریں۔",
            recommendedTime = "In bed before sleeping",
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "sleep_6_baqarah_last_two",
            timeSlot = SunnahTimeSlot.SLEEPING,
            classification = WorshipClassification.RECOMMENDED_NAFL,
            titleEn = "6. Recite Last Two Verses of Surah Al-Baqarah (2:285-286)",
            titleUr = "۶۔ سورہ بقرہ کی آخری دو آیات پڑھنا",
            titleRoman = "6. Surah Baqarah Ki Aakhri Do Aayaat",
            subCategory = "Bedtime Sunnah",
            arabicText = "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ",
            romanUrdu = "Amanar-rasoolu bima unzila ilayhi mir-Rabbihi wal-mu'minoon...",
            urduTranslation = "رسول ایمان لائے اس پر جو ان کے رب کی طرف سے نازل کیا گیا اور مومن بھی، سب اللہ، اس کے فرشتوں، اس کی کتابوں اور رسولوں پر ایمان لائے۔",
            englishTranslation = "The Messenger has believed in what was revealed to him from his Lord, and [so have] the believers. All of them have believed in Allah and His angels and His books and His messengers...",
            wordMeanings = listOf(
                WordMeaning("آمَنَ", "Amana", "Believed", "ایمان لائے", "أ-م-ن", "Form IV verb"),
                WordMeaning("وَالْمُؤْمِنُونَ", "Wal-mu'minoon", "And the believers", "اور مومن لوگ", "أ-م-ن", "Active participle plural")
            ),
            reference = "Sahih al-Bukhari 5009; Sahih Muslim 807",
            explanationEn = "The Prophet ﷺ said: 'Whoever recites the last two verses of Surah Al-Baqarah at night, those two verses will be sufficient for him (in protection and reward).'",
            explanationUr = "نبی کریم ﷺ نے فرمایا: جو شخص رات کو سورہ بقرہ کی آخری دو آیات پڑھ لے تو وہ اس کے لیے کافی ہو جاتی ہیں۔",
            howToPerformEn = "Recite Al-Baqarah 2:285 and 2:286 (viewable in full in the Quran tab) before sleeping.",
            howToPerformUr = "سونے سے پہلے سورہ بقرہ کی آخری دو آیات (2:285-286) تلاوت کریں۔",
            recommendedTime = "At night before sleeping",
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "sleep_7_three_quls_method",
            timeSlot = SunnahTimeSlot.SLEEPING,
            classification = WorshipClassification.RECOMMENDED_NAFL,
            titleEn = "7-8. Recite 3 Quls & Wipe Over the Body (Sunnah Method)",
            titleUr = "۷-۸۔ تین قل پڑھ کر ہاتھوں پر پھونکنا اور جسم پر پھیرنا",
            titleRoman = "7-8. Teen Qul Parh Kar Jism Par Pherhne Ki Sunnat",
            subCategory = "Bedtime Sunnah",
            arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ • قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ • قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
            romanUrdu = "Cup hands together, blow gently, recite Surah Al-Ikhlas, Al-Falaq, An-Nas, then wipe over head, face & body (3 times).",
            urduTranslation = "دونوں ہتھیلیاں ملا کر ان پر پھونک ماریں، تینوں قل پڑھیں اور سر، چہرے اور پورے جسم پر جہاں تک ہاتھ پہنچے پھیریں (تین مرتبہ)۔",
            englishTranslation = "Join both palms together, blow gently into them, recite Al-Ikhlas, Al-Falaq, and An-Nas, and wipe your palms over your head, face, and front of the body—repeating this whole cycle 3 times.",
            wordMeanings = listOf(
                WordMeaning("كَفَّيْهِ", "Kaffayh", "His two palms", "دونوں ہتھیلیاں", "ك-ف-ف", "Dual noun"),
                WordMeaning("نَفَثَ", "Nafatha", "He blew lightly", "ہلکی پھونک ماری", "ن-ف-ث", "Past verb")
            ),
            reference = "Sahih al-Bukhari 5017",
            explanationEn = "Reported by Aisha (RA): Every night when the Prophet ﷺ went to bed, he would cup his palms, blow gently into them, recite the three Quls, and wipe over his body three times.",
            explanationUr = "حضرت عائشہؓ فرماتی ہیں کہ نبی کریم ﷺ ہر رات سونے سے پہلے یہی عمل تین مرتبہ دہراتے تھے۔",
            howToPerformEn = "1. Cup both palms together.\n2. Blow lightly into them.\n3. Recite Al-Ikhlas, Al-Falaq, and An-Nas.\n4. Wipe palms starting from head and face down over the body.\n5. Repeat the entire process 3 times.",
            howToPerformUr = "1. دونوں ہتھیلیاں ملائیں اور ہلکی پھونک ماریں۔\n2. تینوں قل پڑھیں۔\n3. سر اور چہرے سے شروع کر کے پورے جسم پر ہاتھ پھیریں۔\n4. یہ عمل تین بار دہرائیں۔",
            recommendedTime = "In bed before sleeping (3x)",
            recommendedCount = 3,
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "sleep_9_tasbih_fatimi",
            timeSlot = SunnahTimeSlot.SLEEPING,
            classification = WorshipClassification.RECOMMENDED_NAFL,
            titleEn = "9. Perform Tasbih Fatimi (33 • 33 • 34)",
            titleUr = "۹۔ تسبیحِ فاطمی (۳۳، ۳۳، ۳۴) پڑھنا",
            titleRoman = "9. Tasbih-e-Fatimi Parhna",
            subCategory = "Bedtime Sunnah",
            arabicText = "سُبْحَانَ اللَّهِ (٣٣) • الْحَمْدُ لِلَّهِ (٣٣) • اللَّهُ أَكْبَرُ (٣٤)",
            romanUrdu = "SubhanAllah (33x) • Alhamdulillah (33x) • Allahu Akbar (34x)",
            urduTranslation = "۳۳ مرتبہ سبحان اللہ، ۳۳ مرتبہ الحمدللہ، ۳۴ مرتبہ اللہ اکبر۔",
            englishTranslation = "Glory be to Allah (33 times), Praise be to Allah (33 times), Allah is the Greatest (34 times).",
            wordMeanings = listOf(
                WordMeaning("سُبْحَانَ اللَّهِ", "SubhanAllah", "Glory be to Allah", "اللہ پاک ہے", "س-ب-ح", "33 times"),
                WordMeaning("الْحَمْدُ لِلَّهِ", "Alhamdulillah", "Praise be to Allah", "سب تعریف اللہ کی", "ح-م-د", "33 times"),
                WordMeaning("اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allah is Greatest", "اللہ سب سے بڑا ہے", "ك-ب-ر", "34 times")
            ),
            reference = "Sahih al-Bukhari 3705, 5361; Sahih Muslim 2727",
            explanationEn = "The Prophet ﷺ taught Fatimah and Ali (RA) that reciting Tasbih Fatimi before sleep is better for them than having a servant, granting strength and Barakah.",
            explanationUr = "نبی کریم ﷺ نے حضرت فاطمہؓ اور حضرت علیؓ کو تعلیم دی کہ سوتے وقت یہ تسبیح پڑھنا خادم سے بہتر ہے اور جسم و روح میں طاقت پیدا کرتا ہے۔",
            howToPerformEn = "Recite SubhanAllah 33 times, Alhamdulillah 33 times, and Allahu Akbar 34 times in bed.",
            howToPerformUr = "بستر پر لیٹ کر ۳۳ بار سبحان اللہ، ۳۳ بار الحمدللہ اور ۳۴ بار اللہ اکبر پڑھیں۔",
            recommendedTime = "Before sleeping",
            recommendedCount = 100,
            isCoreChecklist = true
        ),
        SunnahActivity(
            id = "sleep_10_dhikr_reflection",
            timeSlot = SunnahTimeSlot.SLEEPING,
            classification = WorshipClassification.RECOMMENDED_NAFL,
            titleEn = "10. Remember Allah & Forgive Others Before Sleeping",
            titleUr = "۱۰۔ اللہ کی یاد اور دل صاف کر کے سونا",
            titleRoman = "10. Allah Ki Yaad Aur Dil Saaf Kar Ke Sona",
            subCategory = "Bedtime Sunnah",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا، وَكَفَانَا وَآوَانَا، فَكَمْ مِمَّنْ لَا كَافِيَ لَهُ وَلَا مُؤْوِيَ",
            romanUrdu = "Alhamdu lillahil-ladhi at'amana wa saqana, wa kafana wa awana, fakam mimman la kafiya lahu wa la mu'wi",
            urduTranslation = "تمام تعریف اللہ کے لیے ہے جس نے ہمیں کھلایا اور پلایا، ہماری کفایت کی اور ہمیں ٹھکانا دیا، کتنے ہی ایسے ہیں جن کا کوئی کفالت کرنے والا اور ٹھکانا دینے والا نہیں۔",
            englishTranslation = "All praise is for Allah who fed us and gave us drink, sufficed us and sheltered us, for how many are there who have none to suffice them or shelter them!",
            wordMeanings = listOf(
                WordMeaning("وَكَفَانَا", "Wa kafana", "And sufficed us", "اور ہماری کفایت کی", "ك-ف-ي", "Past verb"),
                WordMeaning("وَآوَانَا", "Wa awana", "And sheltered us", "اور ہمیں ٹھکانا دیا", "أ-و-ي", "Form IV verb")
            ),
            reference = "Sahih Muslim 2715; Musnad Ahmad (clean heart before sleep)",
            explanationEn = "Ending the night with gratitude for food, drink, and shelter, and forgiving anyone who wronged us, brings peaceful sleep and spiritual elevation.",
            explanationUr = "کھانے، پینے اور گھر کی نعمتوں پر شکرگزاری اور دوسروں کو معاف کر کے دل صاف کرنا پرسکون نیند اور جنت کی بشارت کا سبب ہے۔",
            howToPerformEn = "1. Recite this gratitude dua.\n2. Forgive others for the sake of Allah.\n3. Fall asleep remembering Allah (La ilaha illallah).",
            howToPerformUr = "1. شکرگزاری کی یہ دعا پڑھیں۔\n2. دل سے لوگوں کو معاف کر دیں۔\n3. ذکرِ الٰہی کرتے ہوئے سو جائیں۔",
            recommendedTime = "Final moments before sleep",
            isCoreChecklist = true
        )
    )
}

object SunnahRoutineRepository {
    val allActivities: List<SunnahActivity> =
        SunnahRoutineMorningData.activities + SunnahRoutineDailyNightData.activities

    val coreChecklistActivities: List<SunnahActivity>
        get() = allActivities.filter { it.isCoreChecklist }

    fun byId(id: String): SunnahActivity? = allActivities.firstOrNull { it.id == id }
}
