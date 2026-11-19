package com.example.data.repository

import com.example.data.corpus.QuranCatalogData
import com.example.data.local.CachedAyahEntity
import com.example.data.local.NoorDao
import com.example.data.remote.QuranApiFactory
import com.example.model.AyahData
import com.example.model.WordMeaning
import org.json.JSONArray
import org.json.JSONObject

class QuranRepository(private val noorDao: NoorDao) {

    suspend fun getSurahAyahs(surahNumber: Int): Result<List<AyahData>> {
        // 1. Check Room offline cache first
        val cached = noorDao.getAyahsBySurah(surahNumber)
        val offlineFallback = QuranCatalogData.getOfflineAyahsForSurah(surahNumber)
        val expectedCount = QuranCatalogData.getSurahInfo(surahNumber).ayahCount

        if (cached.size >= expectedCount && cached.isNotEmpty()) {
            return Result.success(cached.map { entity ->
                val offlineWords = offlineFallback
                    .firstOrNull { it.ayahNumber == entity.ayahNumber }
                    ?.words
                    ?: emptyList()
                val parsedWords = decodeWordsJson(entity.wordsJson)
                AyahData(
                    surahNumber = entity.surahNumber,
                    ayahNumber = entity.ayahNumber,
                    globalAyahNumber = entity.globalAyahNumber,
                    arabicUthmani = entity.arabicUthmani,
                    englishTranslation = entity.englishTranslation,
                    urduTranslation = entity.urduTranslation,
                    romanUrdu = entity.romanUrdu,
                    translatorEn = entity.translatorEn,
                    translatorUr = entity.translatorUr,
                    audioUrl = entity.audioUrl,
                    words = if (parsedWords.isNotEmpty()) parsedWords else offlineWords
                )
            })
        }

        // 2. Fetch full verified Surah from AlQuran.cloud + Word-by-Word from Quran.com v4
        return try {
            val response = QuranApiFactory.alQuranApi.getSurahEditions(surahNumber)
            val editions = response.data
            if (editions.size < 3) {
                if (offlineFallback.isNotEmpty()) {
                    return Result.success(offlineFallback)
                }
                return Result.failure(IllegalStateException("Incomplete Surah editions received."))
            }

            val arabicEd = editions.firstOrNull { it.edition.identifier == "quran-uthmani" } ?: editions[0]
            val englishEd = editions.firstOrNull { it.edition.identifier == "en.sahih" } ?: editions[1]
            val urduEd = editions.firstOrNull { it.edition.identifier == "ur.jalandhry" } ?: editions[2]
            val translitEd = editions.firstOrNull { it.edition.identifier == "en.transliteration" }

            // Fetch word-by-word meanings cleanly (best-effort if network supports Quran.com v4)
            val wordsByAyah: Map<Int, List<WordMeaning>> = try {
                val wbResponse = QuranApiFactory.quranComApi.getChapterWords(surahNumber)
                wbResponse.verses.associate { verse ->
                    val words = verse.words
                        .filter { it.charTypeName != "end" }
                        .map { w ->
                            WordMeaning(
                                arabicWord = w.textUthmani ?: w.text.orEmpty(),
                                transliteration = w.transliteration?.text.orEmpty(),
                                englishMeaning = w.translation?.text.orEmpty(),
                                urduMeaning = offlineFallback
                                    .firstOrNull { it.ayahNumber == verse.verseNumber }
                                    ?.words?.firstOrNull { it.arabicWord == w.textUthmani }?.urduMeaning
                                    ?: "لفظی معنی: ${w.translation?.text.orEmpty()}",
                                rootWord = "Tap word details",
                                grammarInfo = "Position ${w.position}"
                            )
                        }
                    verse.verseNumber to words
                }
            } catch (_: Exception) {
                emptyMap()
            }

            val entities = arabicEd.ayahs.mapIndexed { index, arAyah ->
                val enText = englishEd.ayahs.getOrNull(index)?.text.orEmpty()
                val urText = urduEd.ayahs.getOrNull(index)?.text.orEmpty()
                val romanText = translitEd?.ayahs?.getOrNull(index)?.text.orEmpty()
                val offlineMatch = offlineFallback.firstOrNull { it.ayahNumber == arAyah.numberInSurah }
                val words = if (!offlineMatch?.words.isNullOrEmpty()) {
                    offlineMatch!!.words
                } else {
                    wordsByAyah[arAyah.numberInSurah].orEmpty()
                }
                CachedAyahEntity(
                    surahNumber = surahNumber,
                    ayahNumber = arAyah.numberInSurah,
                    globalAyahNumber = arAyah.number,
                    arabicUthmani = arAyah.text,
                    englishTranslation = enText,
                    urduTranslation = urText,
                    romanUrdu = romanText,
                    translatorEn = "Saheeh International",
                    translatorUr = "Fateh Muhammad Jalandhry",
                    audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/${arAyah.number}.mp3",
                    wordsJson = encodeWordsJson(words)
                )
            }

            noorDao.insertAyahs(entities)

            Result.success(entities.map { entity ->
                AyahData(
                    surahNumber = entity.surahNumber,
                    ayahNumber = entity.ayahNumber,
                    globalAyahNumber = entity.globalAyahNumber,
                    arabicUthmani = entity.arabicUthmani,
                    englishTranslation = entity.englishTranslation,
                    urduTranslation = entity.urduTranslation,
                    romanUrdu = entity.romanUrdu,
                    translatorEn = entity.translatorEn,
                    translatorUr = entity.translatorUr,
                    audioUrl = entity.audioUrl,
                    words = decodeWordsJson(entity.wordsJson)
                )
            })
        } catch (e: Exception) {
            if (offlineFallback.isNotEmpty()) {
                Result.success(offlineFallback)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun searchVerses(query: String): List<AyahData> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return emptyList()
        val offlineMatches = QuranCatalogData.offlineFeaturedAyahs.filter {
            it.arabicUthmani.contains(trimmed, ignoreCase = true) ||
                it.englishTranslation.contains(trimmed, ignoreCase = true) ||
                it.urduTranslation.contains(trimmed, ignoreCase = true) ||
                it.romanUrdu.contains(trimmed, ignoreCase = true)
        }
        val dbMatches = noorDao.searchCachedAyahs(trimmed).map { entity ->
            AyahData(
                surahNumber = entity.surahNumber,
                ayahNumber = entity.ayahNumber,
                globalAyahNumber = entity.globalAyahNumber,
                arabicUthmani = entity.arabicUthmani,
                englishTranslation = entity.englishTranslation,
                urduTranslation = entity.urduTranslation,
                romanUrdu = entity.romanUrdu,
                translatorEn = entity.translatorEn,
                translatorUr = entity.translatorUr,
                audioUrl = entity.audioUrl,
                words = decodeWordsJson(entity.wordsJson)
            )
        }
        return (offlineMatches + dbMatches).distinctBy { "${it.surahNumber}:${it.ayahNumber}" }
    }

    private fun encodeWordsJson(words: List<WordMeaning>): String {
        val arr = JSONArray()
        words.forEach { w ->
            arr.put(JSONObject().apply {
                put("ar", w.arabicWord)
                put("tr", w.transliteration)
                put("en", w.englishMeaning)
                put("ur", w.urduMeaning)
                put("root", w.rootWord)
                put("gram", w.grammarInfo)
            })
        }
        return arr.toString()
    }

    private fun decodeWordsJson(json: String): List<WordMeaning> {
        if (json.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { idx ->
                val obj = arr.getJSONObject(idx)
                WordMeaning(
                    arabicWord = obj.optString("ar"),
                    transliteration = obj.optString("tr"),
                    englishMeaning = obj.optString("en"),
                    urduMeaning = obj.optString("ur"),
                    rootWord = obj.optString("root"),
                    grammarInfo = obj.optString("gram")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
