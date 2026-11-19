package com.example.data.corpus

import com.example.model.AyahData
import com.example.model.SurahInfo
import com.example.model.WordMeaning

object QuranCatalogData {
    const val EDITION_ATTRIBUTION =
        "Arabic: Tanzil Uthmani (Mishary Alafasy Audio) • English: Saheeh International • Urdu: Fateh Muhammad Jalandhry (Shamshad Ali Khan Urdu Audio) • Word-by-Word: Quran.com API v4"

    val allSurahs: List<SurahInfo> = listOf(
        SurahInfo(1, "الفاتحة", "Al-Fatihah", "Al-Fatihah", "The Opener", "کھولنے والی", "Makki", 7),
        SurahInfo(2, "البقرة", "Al-Baqarah", "Al-Baqarah", "The Cow", "گائے", "Madani", 286),
        SurahInfo(3, "آل عمران", "Ali 'Imran", "Aal-e-Imran", "Family of Imran", "آلِ عمران", "Madani", 200),
        SurahInfo(4, "النساء", "An-Nisa", "An-Nisa", "The Women", "عورتیں", "Madani", 176),
        SurahInfo(5, "المائدة", "Al-Ma'idah", "Al-Ma'idah", "The Table Spread", "دسترخوان", "Madani", 120),
        SurahInfo(6, "الأنعام", "Al-An'am", "Al-An'am", "The Cattle", "مویشی", "Makki", 165),
        SurahInfo(7, "الأعراف", "Al-A'raf", "Al-A'raf", "The Heights", "بلندیاں", "Makki", 206),
        SurahInfo(8, "الأنفال", "Al-Anfal", "Al-Anfal", "The Spoils of War", "مالِ غنیمت", "Madani", 75),
        SurahInfo(9, "التوبة", "At-Tawbah", "At-Taubah", "The Repentance", "توبہ", "Madani", 129),
        SurahInfo(10, "يونس", "Yunus", "Yunus", "Jonah", "حضرت یونسؑ", "Makki", 109),
        SurahInfo(11, "هود", "Hud", "Hud", "Hud", "حضرت ہودؑ", "Makki", 123),
        SurahInfo(12, "يوسف", "Yusuf", "Yusuf", "Joseph", "حضرت یوسفؑ", "Makki", 111),
        SurahInfo(13, "الرعد", "Ar-Ra'd", "Ar-Ra'd", "The Thunder", "گرج", "Madani", 43),
        SurahInfo(14, "ابراهيم", "Ibrahim", "Ibrahim", "Abraham", "حضرت ابراہیمؑ", "Makki", 52),
        SurahInfo(15, "الحجر", "Al-Hijr", "Al-Hijr", "The Rocky Tract", "حجر", "Makki", 99),
        SurahInfo(16, "النحل", "An-Nahl", "An-Nahl", "The Bee", "شہد کی مکھی", "Makki", 128),
        SurahInfo(17, "الإسراء", "Al-Isra", "Al-Isra", "The Night Journey", "معراج", "Makki", 111),
        SurahInfo(18, "الكهف", "Al-Kahf", "Al-Kahf", "The Cave", "غار", "Makki", 110),
        SurahInfo(19, "مريم", "Maryam", "Maryam", "Mary", "حضرت مریمؑ", "Makki", 98),
        SurahInfo(20, "طه", "Taha", "Ta-Ha", "Ta-Ha", "طہٰ", "Makki", 135),
        SurahInfo(21, "الأنبياء", "Al-Anbya", "Al-Anbiya", "The Prophets", "انبیاء", "Makki", 112),
        SurahInfo(22, "الحج", "Al-Hajj", "Al-Hajj", "The Pilgrimage", "حج", "Madani", 78),
        SurahInfo(23, "المؤمنون", "Al-Mu'minun", "Al-Mu'minun", "The Believers", "مومنین", "Makki", 118),
        SurahInfo(24, "النور", "An-Nur", "An-Noor", "The Light", "نور", "Madani", 64),
        SurahInfo(25, "الفرقان", "Al-Furqan", "Al-Furqan", "The Criterion", "حق و باطل میں فرق", "Makki", 77),
        SurahInfo(26, "الشعراء", "Ash-Shu'ara", "Ash-Shu'ara", "The Poets", "شعراء", "Makki", 227),
        SurahInfo(27, "النمل", "An-Naml", "An-Naml", "The Ant", "چیونٹی", "Makki", 93),
        SurahInfo(28, "القصص", "Al-Qasas", "Al-Qasas", "The Stories", "قصے", "Makki", 88),
        SurahInfo(29, "العنكبوت", "Al-'Ankabut", "Al-Ankabut", "The Spider", "مکڑی", "Makki", 69),
        SurahInfo(30, "الروم", "Ar-Rum", "Ar-Room", "The Romans", "رومی", "Makki", 60),
        SurahInfo(31, "لقمان", "Luqman", "Luqman", "Luqman", "لقمان", "Makki", 34),
        SurahInfo(32, "السجدة", "As-Sajdah", "As-Sajdah", "The Prostration", "سجدہ", "Makki", 30),
        SurahInfo(33, "الأحزاب", "Al-Ahzab", "Al-Ahzab", "The Combined Forces", "لشکر", "Madani", 73),
        SurahInfo(34, "سبإ", "Saba", "Saba", "Sheba", "قومِ سبا", "Makki", 54),
        SurahInfo(35, "فاطر", "Fatir", "Fatir", "Originator", "پیدا کرنے والا", "Makki", 45),
        SurahInfo(36, "يس", "Ya-Sin", "Ya-Seen", "Ya Sin", "یٰسین", "Makki", 83),
        SurahInfo(37, "الصافات", "As-Saffat", "As-Saffat", "Those Who Set the Ranks", "صف باندھنے والے", "Makki", 182),
        SurahInfo(38, "ص", "Sad", "Saad", "The Letter Sad", "ص", "Makki", 88),
        SurahInfo(39, "الزمر", "Az-Zumar", "Az-Zumar", "The Troops", "گروہ", "Makki", 75),
        SurahInfo(40, "غافر", "Ghafir", "Ghafir", "The Forgiver", "بخشنے والا", "Makki", 85),
        SurahInfo(41, "فصلت", "Fussilat", "Fussilat", "Explained in Detail", "واضح بیان", "Makki", 54),
        SurahInfo(42, "الشورى", "Ash-Shuraa", "Ash-Shura", "The Consultation", "مشورہ", "Makki", 53),
        SurahInfo(43, "الزخرف", "Az-Zukhruf", "Az-Zukhruf", "The Ornaments of Gold", "سونے کی زینت", "Makki", 89),
        SurahInfo(44, "الدخان", "Ad-Dukhan", "Ad-Dukhan", "The Smoke", "دھواں", "Makki", 59),
        SurahInfo(45, "الجاثية", "Al-Jathiyah", "Al-Jathiyah", "The Crouching", "گھٹنوں کے بل", "Makki", 37),
        SurahInfo(46, "الأحقاف", "Al-Ahqaf", "Al-Ahqaf", "The Wind-Curved Sandhills", "ریت کے ٹیلے", "Makki", 35),
        SurahInfo(47, "محمد", "Muhammad", "Muhammad", "Muhammad", "حضرت محمد ﷺ", "Madani", 38),
        SurahInfo(48, "الفتح", "Al-Fath", "Al-Fath", "The Victory", "فتح", "Madani", 29),
        SurahInfo(49, "الحجرات", "Al-Hujurat", "Al-Hujurat", "The Rooms", "حجرے", "Madani", 18),
        SurahInfo(50, "ق", "Qaf", "Qaf", "The Letter Qaf", "ق", "Makki", 45),
        SurahInfo(51, "الذاريات", "Adh-Dhariyat", "Adh-Dhariyat", "The Winnowing Winds", "اڑانے والی ہوائیں", "Makki", 60),
        SurahInfo(52, "الطور", "At-Tur", "At-Tur", "The Mount", "پہاڑ", "Makki", 49),
        SurahInfo(53, "النجم", "An-Najm", "An-Najm", "The Star", "ستارہ", "Makki", 62),
        SurahInfo(54, "القمر", "Al-Qamar", "Al-Qamar", "The Moon", "چاند", "Makki", 55),
        SurahInfo(55, "الرحمن", "Ar-Rahman", "Ar-Rahman", "The Beneficent", "نہایت مہربان", "Madani", 78),
        SurahInfo(56, "الواقعة", "Al-Waqi'ah", "Al-Waqi'ah", "The Inevitable", "واقع ہونے والی", "Makki", 96),
        SurahInfo(57, "الحديد", "Al-Hadid", "Al-Hadid", "The Iron", "لوہا", "Madani", 29),
        SurahInfo(58, "المجادلة", "Al-Mujadila", "Al-Mujadilah", "The Pleading Woman", "جھگڑنے والی", "Madani", 22),
        SurahInfo(59, "الحشر", "Al-Hashr", "Al-Hashr", "The Exile", "حشر", "Madani", 24),
        SurahInfo(60, "الممتحنة", "Al-Mumtahanah", "Al-Mumtahanah", "She That Is Examined", "امتحان لی جانے والی", "Madani", 13),
        SurahInfo(61, "الصف", "As-Saf", "As-Saff", "The Ranks", "صف", "Madani", 14),
        SurahInfo(62, "الجمعة", "Al-Jumu'ah", "Al-Jumu'ah", "The Congregation, Friday", "جمعہ", "Madani", 11),
        SurahInfo(63, "المنافقون", "Al-Munafiqun", "Al-Munafiqun", "The Hypocrites", "منافقین", "Madani", 11),
        SurahInfo(64, "التغابن", "At-Taghabun", "At-Taghabun", "The Mutual Disillusion", "ہار جیت", "Madani", 18),
        SurahInfo(65, "الطلاق", "At-Talaq", "At-Talaq", "The Divorce", "طلاق", "Madani", 12),
        SurahInfo(66, "التحريم", "At-Tahrim", "At-Tahrim", "The Prohibition", "حرام قرار دینا", "Madani", 12),
        SurahInfo(67, "الملك", "Al-Mulk", "Al-Mulk", "The Sovereignty", "بادشاہت", "Makki", 30),
        SurahInfo(68, "القلم", "Al-Qalam", "Al-Qalam", "The Pen", "قلم", "Makki", 52),
        SurahInfo(69, "الحاقة", "Al-Haqqah", "Al-Haqqah", "The Reality", "برحق واقعہ", "Makki", 52),
        SurahInfo(70, "المعارج", "Al-Ma'arij", "Al-Ma'arij", "The Ascending Stairways", "عروج کے زینے", "Makki", 44),
        SurahInfo(71, "نوح", "Nuh", "Nuh", "Noah", "حضرت نوحؑ", "Makki", 28),
        SurahInfo(72, "الجن", "Al-Jinn", "Al-Jinn", "The Jinn", "جن", "Makki", 28),
        SurahInfo(73, "المزمل", "Al-Muzzammil", "Al-Muzzammil", "The Enshrouded One", "چادر اوڑھنے والا", "Makki", 20),
        SurahInfo(74, "المدثر", "Al-Muddaththir", "Al-Muddaththir", "The Cloaked One", "کپڑا لپیٹنے والا", "Makki", 56),
        SurahInfo(75, "القيامة", "Al-Qiyamah", "Al-Qiyamah", "The Resurrection", "قیامت", "Makki", 40),
        SurahInfo(76, "الانسان", "Al-Insan", "Al-Insan", "The Man", "انسان", "Madani", 31),
        SurahInfo(77, "المرسلات", "Al-Mursalat", "Al-Mursalat", "The Emissaries", "بھیجی جانے والی", "Makki", 50),
        SurahInfo(78, "النبإ", "An-Naba", "An-Naba", "The Tidings", "بڑی خبر", "Makki", 40),
        SurahInfo(79, "النازعات", "An-Nazi'at", "An-Nazi'at", "Those Who Drag Forth", "کھینچ کر نکالنے والے", "Makki", 46),
        SurahInfo(80, "عبس", "'Abasa", "Abasa", "He Frowned", "تیوری چڑھائی", "Makki", 42),
        SurahInfo(81, "التكوير", "At-Takwir", "At-Takwir", "The Overthrowing", "لپیٹنا", "Makki", 29),
        SurahInfo(82, "الإنفطار", "Al-Infitar", "Al-Infitar", "The Cleaving", "پھٹ جانا", "Makki", 19),
        SurahInfo(83, "المطففين", "Al-Mutaffifin", "Al-Mutaffifin", "The Defrauding", "ناپ تول میں کمی", "Makki", 36),
        SurahInfo(84, "الإنشقاق", "Al-Inshiqaq", "Al-Inshiqaq", "The Sundering", "شق ہو جانا", "Makki", 25),
        SurahInfo(85, "البروج", "Al-Buruj", "Al-Buruj", "The Mansions of the Stars", "برج", "Makki", 22),
        SurahInfo(86, "الطارق", "At-Tariq", "At-Tariq", "The Nightcomer", "رات کو آنے والا", "Makki", 17),
        SurahInfo(87, "الأعلى", "Al-A'la", "Al-A'la", "The Most High", "سب سے بلند", "Makki", 19),
        SurahInfo(88, "الغاشية", "Al-Ghashiyah", "Al-Ghashiyah", "The Overwhelming", "چھا جانے والی", "Makki", 26),
        SurahInfo(89, "الفجر", "Al-Fajr", "Al-Fajr", "The Dawn", "فجر", "Makki", 30),
        SurahInfo(90, "البلد", "Al-Balad", "Al-Balad", "The City", "شہر", "Makki", 20),
        SurahInfo(91, "الشمس", "Ash-Shams", "Ash-Shams", "The Sun", "سورج", "Makki", 15),
        SurahInfo(92, "الليل", "Al-Layl", "Al-Layl", "The Night", "رات", "Makki", 21),
        SurahInfo(93, "الضحى", "Ad-Duhaa", "Ad-Duha", "The Morning Hours", "چاشت", "Makki", 11),
        SurahInfo(94, "الشرح", "Ash-Sharh", "Ash-Sharh", "The Relief", "شرح صدر", "Makki", 8),
        SurahInfo(95, "التين", "At-Tin", "At-Teen", "The Fig", "انجیر", "Makki", 8),
        SurahInfo(96, "العلق", "Al-'Alaq", "Al-Alaq", "The Clot", "جما ہوا خون", "Makki", 19),
        SurahInfo(97, "القدر", "Al-Qadr", "Al-Qadr", "The Power", "شبِ قدر", "Makki", 5),
        SurahInfo(98, "البينة", "Al-Bayyinah", "Al-Bayyinah", "The Clear Proof", "کھلی دلیل", "Madani", 8),
        SurahInfo(99, "الزلزلة", "Az-Zalzalah", "Az-Zalzalah", "The Earthquake", "زلزلہ", "Madani", 8),
        SurahInfo(100, "العاديات", "Al-'Adiyat", "Al-Adiyat", "The Courser", "دوڑنے والے گھوڑے", "Makki", 11),
        SurahInfo(101, "القارعة", "Al-Qari'ah", "Al-Qari'ah", "The Calamity", "کھڑکھڑانے والی", "Makki", 11),
        SurahInfo(102, "التكاثر", "At-Takathur", "At-Takathur", "The Rivalry in World Increase", "کثرت کی ہوس", "Makki", 8),
        SurahInfo(103, "العصر", "Al-'Asr", "Al-Asr", "The Declining Day", "زمانہ", "Makki", 3),
        SurahInfo(104, "الهمزة", "Al-Humazah", "Al-Humazah", "The Traducer", "طعنہ زن", "Makki", 9),
        SurahInfo(105, "الفيل", "Al-Fil", "Al-Feel", "The Elephant", "ہاتھی", "Makki", 5),
        SurahInfo(106, "قريش", "Quraysh", "Quraysh", "Quraysh", "قریش", "Makki", 4),
        SurahInfo(107, "الماعون", "Al-Ma'un", "Al-Ma'un", "The Small Kindnesses", "عام ضرورت کی چیزیں", "Makki", 7),
        SurahInfo(108, "الكوثر", "Al-Kawthar", "Al-Kawthar", "The Abundance", "حوضِ کوثر", "Makki", 3),
        SurahInfo(109, "الكافرون", "Al-Kafirun", "Al-Kafirun", "The Disbelievers", "کافر لوگ", "Makki", 6),
        SurahInfo(110, "النصر", "An-Nasr", "An-Nasr", "The Divine Support", "مدد", "Madani", 3),
        SurahInfo(111, "المسد", "Al-Masad", "Al-Lahab", "The Palm Fiber", "بٹی ہوئی رسی", "Makki", 5),
        SurahInfo(112, "الإخلاص", "Al-Ikhlas", "Al-Ikhlas", "The Sincerity", "خالص توحید", "Makki", 4),
        SurahInfo(113, "الفلق", "Al-Falaq", "Al-Falaq", "The Daybreak", "صبح", "Makki", 5),
        SurahInfo(114, "الناس", "An-Nas", "An-Nas", "Mankind", "انسان", "Makki", 6)
    )

    val offlineFeaturedAyahs: List<AyahData> = listOf(
        // Surah Al-Fatihah (1:1-7)
        AyahData(
            1, 1, 1,
            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
            "شروع اللہ کے نام سے جو بڑا مہربان نہایت رحم والا ہے۔",
            "Bismillahir-Rahmanir-Raheem",
            "Saheeh International", "Fateh Muhammad Jalandhry",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3",
            listOf(
                WordMeaning("بِسْمِ", "Bismi", "In the name of", "نام سے", "س-م-و", "Preposition + Noun (Genitive)"),
                WordMeaning("اللَّهِ", "Allahi", "Allah", "اللہ", "أ-ل-ه", "Proper Noun (Lafz al-Jalalah)"),
                WordMeaning("الرَّحْمَٰنِ", "Ar-Rahmani", "The Entirely Merciful", "بڑا مہربان", "ر-ح-م", "Intensive Adjective (Fa'lan)"),
                WordMeaning("الرَّحِيمِ", "Ar-Raheemi", "The Especially Merciful", "نہایت رحم والا", "ر-ح-م", "Permanent Adjective (Fa'eel)")
            )
        ),
        AyahData(
            1, 2, 2,
            "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
            "[All] praise is [due] to Allah, Lord of the worlds -",
            "سب طرح کی تعریف اللہ ہی کے لیے ہے جو تمام مخلوقات کا پروردگار ہے۔",
            "Alhamdu lillahi Rabbil-'aalameen",
            "Saheeh International", "Fateh Muhammad Jalandhry",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/2.mp3",
            listOf(
                WordMeaning("الْحَمْدُ", "Al-Hamdu", "All praise and thanks", "تمام تعریف", "ح-م-د", "Definite Noun (Nominative)"),
                WordMeaning("لِلَّهِ", "Lillahi", "Is for Allah", "اللہ کے لیے", "أ-ل-ه", "Preposition + Noun"),
                WordMeaning("رَبِّ", "Rabbi", "Lord / Sustainer", "رب / پالنے والا", "ر-ب-ب", "Construct Noun"),
                WordMeaning("الْعَالَمِينَ", "Al-'aalameen", "Of the worlds", "تمام جہانوں کا", "ع-ل-م", "Sound Masculine Plural")
            )
        ),
        AyahData(
            1, 3, 3,
            "الرَّحْمَٰنِ الرَّحِيمِ",
            "The Entirely Merciful, the Especially Merciful,",
            "بڑا مہربان نہایت رحم والا۔",
            "Ar-Rahmanir-Raheem",
            "Saheeh International", "Fateh Muhammad Jalandhry",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/3.mp3",
            listOf(
                WordMeaning("الرَّحْمَٰنِ", "Ar-Rahmani", "The Entirely Merciful", "بڑا مہربان", "ر-ح-م", "Adjective"),
                WordMeaning("الرَّحِيمِ", "Ar-Raheemi", "The Especially Merciful", "نہایت رحم والا", "ر-ح-م", "Adjective")
            )
        ),
        AyahData(
            1, 4, 4,
            "مَالِكِ يَوْمِ الدِّينِ",
            "Sovereign of the Day of Recompense.",
            "انصاف کے دن کا مالک ہے۔",
            "Maliki yawmid-deen",
            "Saheeh International", "Fateh Muhammad Jalandhry",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/4.mp3",
            listOf(
                WordMeaning("مَالِكِ", "Maliki", "Master / Sovereign", "مالک", "م-ل-ك", "Active Participle"),
                WordMeaning("يَوْمِ", "Yawmi", "Of the Day", "دن کا", "ي-و-م", "Noun (Idafa)"),
                WordMeaning("الدِّينِ", "Ad-Deen", "Of Judgment / Recompense", "جزا و سزا / انصاف", "د-ي-ن", "Definite Noun")
            )
        ),
        AyahData(
            1, 5, 5,
            "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
            "It is You we worship and You we ask for help.",
            "اے پروردگار! ہم تیری ہی عبادت کرتے ہیں اور تجھ ہی سے مدد مانگتے ہیں۔",
            "Iyyaka na'budu wa iyyaka nasta'een",
            "Saheeh International", "Fateh Muhammad Jalandhry",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/5.mp3",
            listOf(
                WordMeaning("إِيَّاكَ", "Iyyaka", "You alone", "صرف تیری ہی", "إ-ي-ا", "Detached Object Pronoun"),
                WordMeaning("نَعْبُدُ", "Na'budu", "We worship", "ہم عبادت کرتے ہیں", "ع-ب-د", "Imperfect Verb (1st Plural)"),
                WordMeaning("نَسْتَعِينُ", "Nasta'een", "We seek help", "ہم مدد مانگتے ہیں", "ع-و-ن", "Form X Verb (1st Plural)")
            )
        ),
        AyahData(
            1, 6, 6,
            "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
            "Guide us to the straight path -",
            "ہم کو سیدھے رستے چلا۔",
            "Ihdinas-siratal-mustaqeem",
            "Saheeh International", "Fateh Muhammad Jalandhry",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6.mp3",
            listOf(
                WordMeaning("اهْدِنَا", "Ihdina", "Guide us", "ہمیں ہدایت دے", "ه-د-ي", "Imperative + Object Pronoun"),
                WordMeaning("الصِّرَاطَ", "As-Sirata", "The path", "راستہ", "ص-ر-ط", "Definite Noun"),
                WordMeaning("الْمُسْتَقِيمَ", "Al-Mustaqeem", "The straight", "سیدھا", "ق-و-م", "Form X Participle")
            )
        ),
        AyahData(
            1, 7, 7,
            "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
            "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.",
            "ان لوگوں کے رستے جن پر تو اپنا فضل و کرم کرتا رہا، نہ ان کے جن پر غصے ہوتا رہا اور نہ گمراہوں کے۔",
            "Siratal-ladhina an'amta 'alayhim ghayril-maghdoobi 'alayhim wa lad-daalleen",
            "Saheeh International", "Fateh Muhammad Jalandhry",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/7.mp3",
            listOf(
                WordMeaning("أَنْعَمْتَ", "An'amta", "You bestowed favor", "تو نے انعام فرمایا", "ن-ع-م", "Form IV Verb"),
                WordMeaning("غَيْرِ", "Ghayri", "Not of", "نہ کہ", "غ-ي-ر", "Exception Noun"),
                WordMeaning("الضَّالِّينَ", "Ad-Daalleen", "Those who went astray", "گمراہ ہونے والے", "ض-ل-ل", "Active Participle Plural")
            )
        ),

        // Key Verses of Al-Baqarah (Ayat-ul-Kursi 2:255 & 2:285-286)
        AyahData(
            2, 255, 262,
            "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is [presently] before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
            "اللہ وہ معبودِ برحق ہے جس کے سوا کوئی عبادت کے لائق نہیں، زندہ اور سب کا تھامنے والا، نہ اسے اونگھ آتی ہے نہ نیند، جو کچھ آسمانوں میں اور جو کچھ زمین میں ہے سب اسی کا ہے۔ کون ہے جو اس کی اجازت کے بغیر اس کے ہاں سفارش کر سکے؟ جو کچھ ان کے سامنے ہے اور جو ان کے پیچھے ہے سب جانتا ہے، اور لوگ اس کے علم میں سے کسی چیز پر احاطہ نہیں کر سکتے مگر جتنا وہ چاہے۔ اس کی کرسی آسمانوں اور زمین کو گھیرے ہوئے ہے اور ان دونوں کی حفاظت اسے ذرا بھی نہیں تھکاتی، اور وہی سب سے بلند و عظیم ہے۔",
            "Allahu la ilaha illa huwal-Hayyul-Qayyum, la ta'khudhuhu sinatun wa la nawm, lahu ma fis-samawati wa ma fil-ard...",
            "Saheeh International", "Fateh Muhammad Jalandhry",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3",
            listOf(
                WordMeaning("الْحَيُّ", "Al-Hayyu", "The Ever-Living", "ہمیشہ زندہ", "ح-ي-ي", "Divine Name"),
                WordMeaning("الْقَيُّومُ", "Al-Qayyumu", "Sustainer of all", "سب کا نگہبان", "ق-و-م", "Divine Name"),
                WordMeaning("كُرْسِيُّهُ", "Kursiyyuhu", "His Kursi (Footstool/Dominion)", "اس کی کرسی", "ك-ر-س", "Noun + Pronoun"),
                WordMeaning("الْعَلِيُّ الْعَظِيمُ", "Al-'Aliyyul-'Azeem", "Most High, Most Great", "بلند و عظیم", "ع-ل-و / ع-ظ-م", "Divine Names")
            )
        ),
        AyahData(
            2, 285, 292,
            "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِّن رُّسُلِهِ ۚ وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ",
            "The Messenger has believed in what was revealed to him from his Lord, and [so have] the believers. All of them have believed in Allah and His angels and His books and His messengers, [saying], \"We make no distinction between any of His messengers.\" And they say, \"We hear and we obey. [We seek] Your forgiveness, our Lord, and to You is the [final] destination.\"",
            "رسول (محمد ﷺ) اس کتاب پر جو ان کے پروردگار کی طرف سے ان پر نازل ہوئی ایمان رکھتے ہیں اور مومن بھی۔ سب اللہ پر، اس کے فرشتوں پر، اس کی کتابوں پر اور اس کے پیغمبروں پر ایمان لائے ہیں۔ (اور کہتے ہیں کہ) ہم اس کے پیغمبروں میں سے کسی میں فرق نہیں کرتے، اور کہتے ہیں کہ ہم نے (حکم) سنا اور قبول کیا، اے ہمارے پروردگار! ہمیں تیری بخشش درکار ہے اور تیری ہی طرف لوٹ کر جانا ہے۔",
            "Amanar-rasoolu bima unzila ilayhi mir-Rabbihi wal-mu'minoon...",
            "Saheeh International", "Fateh Muhammad Jalandhry",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/292.mp3",
            listOf(
                WordMeaning("سَمِعْنَا", "Sami'na", "We heard", "ہم نے سنا", "س-م-ع", "Past verb"),
                WordMeaning("وَأَطَعْنَا", "Wa ata'na", "And we obeyed", "اور ہم نے اطاعت کی", "ط-و-ع", "Form IV verb"),
                WordMeaning("غُفْرَانَكَ", "Ghufranaka", "Your forgiveness", "تیری بخشش", "غ-ف-ر", "Verbal noun")
            )
        ),
        AyahData(
            2, 286, 293,
            "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
            "Allah does not charge a soul except [with that within] its capacity. It will have [the consequence of] what [good] it has gained, and it will bear [the consequence of] what [evil] it has earned. \"Our Lord, do not impose blame upon us if we have forgotten or erred. Our Lord, and lay not upon us a burden like that which You laid upon those before us. Our Lord, and burden us not with that which we have no ability to bear. And pardon us; and forgive us; and have mercy upon us. You are our protector, so give us victory over the disbelieving people.\"",
            "اللہ کسی شخص کو اس کی طاقت سے زیادہ کا ذمہ دار نہیں بناتا۔ جو نیکی اس نے کمائی اس کے لیے ہے اور جو برائی کی اس کا وبال اسی پر ہے۔ اے ہمارے رب! اگر ہم بھول جائیں یا خطا کریں تو ہماری گرفت نہ فرما۔ اے ہمارے رب! ہم پر وہ بوجھ نہ ڈال جیسا ہم سے پہلوں پر ڈالا تھا۔ اور ہم سے وہ بوجھ نہ اٹھوا جس کی ہم میں طاقت نہ ہو۔ اور ہمیں معاف فرما، ہمیں بخش دے اور ہم پر رحم فرما۔ تو ہی ہمارا کارساز ہے۔",
            "La yukallifullahu nafsan illa wus'aha...",
            "Saheeh International", "Fateh Muhammad Jalandhry",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/293.mp3",
            listOf(
                WordMeaning("وُسْعَهَا", "Wus'aha", "Its capacity", "اس کی وسعت/طاقت", "و-س-ع", "Noun + pronoun"),
                WordMeaning("وَاعْفُ عَنَّا", "Wa'fu 'anna", "And pardon us", "اور ہمیں معاف فرما", "ع-ف-و", "Imperative"),
                WordMeaning("مَوْلَانَا", "Mawlana", "Our Protector/Master", "ہمارا کارساز", "و-ل-ي", "Noun + pronoun")
            )
        ),

        // Surah Al-Asr (103)
        AyahData(103, 1, 6177, "وَالْعَصْرِ", "By time,", "زمانے کی قسم،", "Wal-'Asr", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6177.mp3",
            listOf(WordMeaning("وَالْعَصْرِ", "Wal-'Asr", "By time / afternoon", "زمانے کی قسم", "ع-ص-ر", "Oath particle + Noun"))),
        AyahData(103, 2, 6178, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "Indeed, mankind is in loss,", "بے شک انسان نقصان میں ہے،", "Innal-insana lafi khusr", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6178.mp3",
            listOf(WordMeaning("خُسْرٍ", "Khusr", "Loss", "خسارہ / نقصان", "خ-س-ر", "Verbal Noun"))),
        AyahData(103, 3, 6179, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.", "مگر وہ لوگ جو ایمان لائے اور نیک عمل کرتے رہے اور آپس میں حق کی نصیحت اور صبر کی تاکید کرتے رہے۔", "Illal-ladhina amanoo wa 'amilus-salihati wa tawasaw bil-haqqi wa tawasaw bis-sabr", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6179.mp3",
            listOf(
                WordMeaning("الصَّالِحَاتِ", "As-Salihat", "Righteous deeds", "نیک اعمال", "ص-ل-ح", "Plural participle"),
                WordMeaning("تَوَاصَوْا", "Tawasaw", "Advised one another", "باہم نصیحت کی", "و-ص-ي", "Form VI verb")
            )),

        // Surah Al-Ikhlas (112)
        AyahData(112, 1, 6222, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, \"He is Allah, [who is] One,", "کہو کہ وہ (ذات پاک جس کا نام) اللہ (ہے) ایک ہے۔", "Qul Huwallahu Ahad", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6222.mp3",
            listOf(WordMeaning("أَحَدٌ", "Ahad", "One and Only", "یکتا و بے مثل", "أ-ح-د", "Noun"))),
        AyahData(112, 2, 6223, "اللَّهُ الصَّمَدُ", "Allah, the Eternal Refuge.", "معبودِ برحق جو بے نیاز ہے۔", "Allahus-Samad", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6223.mp3",
            listOf(WordMeaning("الصَّمَدُ", "As-Samad", "Eternal Refuge / Self-Sufficient", "بے نیاز، سب کا سہارا", "ص-م-د", "Divine Name"))),
        AyahData(112, 3, 6224, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born,", "نہ کسی کا باپ ہے اور نہ کسی کا بیٹا۔", "Lam yalid wa lam yoolad", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6224.mp3",
            listOf(WordMeaning("يَلِدْ", "Yalid", "Begets", "جنا ہو", "و-ل-د", "Jussive verb"))),
        AyahData(112, 4, 6225, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Nor is there to Him any equivalent.\"", "اور کوئی اس کا ہمسر نہیں۔", "Wa lam yakul-lahu kufuwan ahad", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6225.mp3",
            listOf(WordMeaning("كُفُوًا", "Kufuwan", "Equivalent / Comparable", "ہمسر / برابر", "ك-ف-أ", "Noun"))),

        // Surah Al-Falaq (113)
        AyahData(113, 1, 6226, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, \"I seek refuge in the Lord of daybreak", "کہو کہ میں صبح کے پیدا کرنے والے کی پناہ مانگتا ہوں،", "Qul a'oodhu bi-Rabbil-falaq", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6226.mp3",
            listOf(WordMeaning("الْفَلَقِ", "Al-Falaq", "Daybreak", "صبح کی روشنی", "ف-ل-ق", "Noun"))),
        AyahData(113, 2, 6227, "مِن شَرِّ مَا خَلَقَ", "From the evil of that which He created", "ہر چیز کی برائی سے جو اس نے پیدا کی،", "Min sharri ma khalaq", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6227.mp3",
            listOf(WordMeaning("شَرِّ", "Sharri", "Evil / Harm", "شر / برائی", "ش-ر-ر", "Noun"))),
        AyahData(113, 3, 6228, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles", "اور شبِ تاریک کی برائی سے جب اس کا اندھیرا چھا جائے،", "Wa min sharri ghasiqin idha waqab", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6228.mp3",
            listOf(WordMeaning("غَاسِقٍ", "Ghasiq", "Darkness of night", "اندھیری رات", "غ-س-ق", "Active participle"))),
        AyahData(113, 4, 6229, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots", "اور گرہوں میں پھونکنے والیوں کے شر سے،", "Wa min sharrin-naffathati fil-'uqad", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6229.mp3",
            listOf(WordMeaning("النَّفَّاثَاتِ", "An-Naffathat", "Those who blow (sorcery)", "پھونک مارنے والیاں", "ن-ف-ث", "Plural noun"))),
        AyahData(113, 5, 6230, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies.\"", "اور حسد کرنے والے کی برائی سے جب وہ حسد کرنے لگے۔", "Wa min sharri hasidin idha hasad", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6230.mp3",
            listOf(WordMeaning("حَاسِدٍ", "Hasid", "An envier", "حسد کرنے والا", "ح-س-د", "Active participle"))),

        // Surah An-Nas (114)
        AyahData(114, 1, 6231, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, \"I seek refuge in the Lord of mankind,", "کہو کہ میں لوگوں کے پروردگار کی پناہ مانگتا ہوں،", "Qul a'oodhu bi-Rabbin-Nas", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6231.mp3",
            listOf(WordMeaning("النَّاسِ", "An-Nas", "Mankind", "تمام انسان", "ن-و-س", "Collective noun"))),
        AyahData(114, 2, 6232, "مَلِكِ النَّاسِ", "The Sovereign of mankind.", "لوگوں کے حقیقی بادشاہ کی،", "Malikin-Nas", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6232.mp3",
            listOf(WordMeaning("مَلِكِ", "Maliki", "King / Sovereign", "بادشاہ", "م-ل-ك", "Noun"))),
        AyahData(114, 3, 6233, "إِلَٰهِ النَّاسِ", "The God of mankind,", "لوگوں کے معبودِ برحق کی،", "Ilahin-Nas", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6233.mp3",
            listOf(WordMeaning("إِلَٰهِ", "Ilahi", "God / Deity", "معبود", "أ-ل-ه", "Noun"))),
        AyahData(114, 4, 6234, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer -", "اس پیچھے ہٹ جانے والے وسوسہ ڈالنے والے کے شر سے،", "Min sharril-waswasil-khannas", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6234.mp3",
            listOf(WordMeaning("الْخَنَّاسِ", "Al-Khannas", "The one who retreats (when Allah is remembered)", "پیچھے ہٹ جانے والا", "خ-ن-س", "Intensive adjective"))),
        AyahData(114, 5, 6235, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers [evil] into the breasts of mankind -", "جو لوگوں کے دلوں میں وسوسے ڈالتا ہے،", "Alladhi yuwaswisu fi sudoorin-Nas", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6235.mp3",
            listOf(WordMeaning("صُدُورِ", "Sudoori", "Chests / Hearts", "سینوں / دلوں میں", "ص-د-ر", "Broken plural"))),
        AyahData(114, 6, 6236, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind.\"", "خواہ وہ جنوں میں سے ہو یا انسانوں میں سے۔", "Minal-jinnati wan-Nas", "Saheeh International", "Fateh Muhammad Jalandhry", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6236.mp3",
            listOf(WordMeaning("الْجِنَّةِ", "Al-Jinnati", "The Jinn", "جنات", "ج-ن-ن", "Collective noun")))
    )

    fun getSurahInfo(surahNumber: Int): SurahInfo =
        allSurahs.firstOrNull { it.number == surahNumber } ?: allSurahs.first()

    fun getOfflineAyahsForSurah(surahNumber: Int): List<AyahData> =
        offlineFeaturedAyahs.filter { it.surahNumber == surahNumber }

    fun getDailyAyah(dayOfYear: Int): AyahData {
        val pool = offlineFeaturedAyahs
        return pool[dayOfYear % pool.size]
    }
}
