package com.example.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.data.corpus.AzkarDuaData
import com.example.data.corpus.HadithLibraryData
import com.example.data.corpus.QuranCatalogData
import com.example.data.corpus.SunnahRoutineRepository
import com.example.data.local.NoorDao
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

data class OcrVerificationResult(
    val extractedText: String,
    val verificationStatus: String,
    val matchedReference: String,
    val summary: String
)

class IslamicAiRepository(private val noorDao: NoorDao) {

    // Free-tier Gemini 3.x Flash via Firebase AI Logic (requires no API key; protected by App Check)
    private val model by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    fun retrieveVerifiedSourcesForQuery(query: String): List<String> {
        val tokens = query.lowercase()
            .split(Regex("[\\s,?!.]+"))
            .filter { it.length > 2 }

        val sunnahMatches = SunnahRoutineRepository.allActivities.filter { act ->
            tokens.any { t ->
                act.titleEn.contains(t, true) ||
                    act.englishTranslation.contains(t, true) ||
                    act.explanationEn.contains(t, true) ||
                    act.subCategory.contains(t, true)
            }
        }.take(3).map {
            "[Sunnah: ${it.titleEn}] Arabic: ${it.arabicText} | Roman: ${it.romanUrdu} | Meaning: ${it.englishTranslation} | Urdu: ${it.urduTranslation} | Ref: ${it.reference}"
        }

        val duaMatches = AzkarDuaData.allDuas.filter { dua ->
            tokens.any { t ->
                dua.titleEn.contains(t, true) ||
                    dua.englishTranslation.contains(t, true) ||
                    dua.whenToRead.contains(t, true) ||
                    dua.category.titleEn.contains(t, true)
            }
        }.take(3).map {
            "[Dua: ${it.titleEn}] Arabic: ${it.arabicText} | Roman: ${it.romanUrdu} | Meaning: ${it.englishTranslation} | Count: ${it.recommendedCount}x | Ref: ${it.reference}"
        }

        val hadithMatches = HadithLibraryData.allHadiths.filter { h ->
            tokens.any { t ->
                h.englishTranslation.contains(t, true) ||
                    h.chapter.contains(t, true) ||
                    h.bookName.contains(t, true) ||
                    h.explanation.contains(t, true)
            }
        }.take(2).map {
            "[Hadith: ${it.bookName} #${it.hadithNumber} (${it.authenticity.label})] ${it.englishTranslation} | Source: ${it.gradingSource}"
        }

        val quranMatches = QuranCatalogData.offlineFeaturedAyahs.filter { a ->
            tokens.any { t ->
                a.englishTranslation.contains(t, true) ||
                    a.romanUrdu.contains(t, true)
            }
        }.take(2).map {
            "[Quran ${it.surahNumber}:${it.ayahNumber}] ${it.arabicUthmani} | ${it.englishTranslation} (${it.translatorEn})"
        }

        return (sunnahMatches + duaMatches + hadithMatches + quranMatches).distinct()
    }

    fun askAssistantStream(userQuestion: String): Flow<Result<String>> = flow {
        val groundedSources = retrieveVerifiedSourcesForQuery(userQuestion)
        val contextBlock = if (groundedSources.isNotEmpty()) {
            groundedSources.joinToString("\n")
        } else {
            "No direct keyword match found in the local offline database for this exact query."
        }

        val prompt = """
            You are the Islamic Knowledge Assistant inside Noor-e-Zindagi.
            STRICT AUTHENTICITY & SAFETY RULES:
            1. Answer ONLY using verified Quranic verses, authentic Sunnah, and reliable Hadith references.
            2. Prioritize the verified database context provided below.
            3. NEVER fabricate a Quranic verse, Surah:Ayah number, or Hadith number/grading.
            4. NEVER present personal opinions as Islamic rulings (Fatwa).
            5. Clearly mention when scholars have valid differences of opinion (e.g., Hanafi/Shafi'i/Maliki/Hanbali prayer details).
            6. Refer complex or personal Fiqh rulings to qualified Islamic scholars.
            7. Distinguish authentic Sunnah from cultural customs.
            8. If verified information is unavailable in your reliable knowledge or database context, state so honestly.
            9. Include Arabic text (if verified), brief translation, and clear citation when relevant.

            VERIFIED APP DATABASE CONTEXT:
            $contextBlock

            USER QUESTION:
            $userQuestion
        """.trimIndent()

        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                model.generateContentStream(prompt).collect { chunk ->
                    val text = chunk.text.orEmpty()
                    if (text.isNotEmpty()) {
                        accumulated.append(text)
                        emit(Result.success(accumulated.toString()))
                    }
                }
            }
            if (accumulated.isBlank()) {
                emit(Result.failure(IllegalStateException("Assistant returned an empty response.")))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI request timed out", e)
            emit(Result.failure(Exception("Assistant timed out after 60 seconds. Please try again.")))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI generation failed", e)
            emit(Result.failure(e))
        }
    }

    suspend fun extractAndVerifyOcr(bitmap: Bitmap): Result<OcrVerificationResult> {
        return try {
            val response = withTimeout(60_000L) {
                model.generateContent(
                    content {
                        image(bitmap)
                        text(
                            "Extract any Arabic, Urdu, and English text visible on this Islamic poster or scanned page verbatim. " +
                                "Do NOT invent any missing words or Hadith references that are not clearly visible in the image. " +
                                "Preserve exact Arabic diacritics if present, or note where text is blurry."
                        )
                    }
                )
            }
            val extracted = response.text?.trim().orEmpty()
            if (extracted.isBlank()) {
                return Result.failure(IllegalStateException("No readable text extracted from the image."))
            }
            val verification = verifyExtractedReligiousText(extracted)
            Result.success(verification)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "OCR timed out", e)
            Result.failure(Exception("OCR extraction timed out. Please try a clearer or smaller image."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "OCR extraction failed", e)
            Result.failure(e)
        }
    }

    suspend fun verifyExtractedReligiousText(extractedText: String): OcrVerificationResult {
        val normalized = normalizeArabic(extractedText)
        // Check local verified Quran verses + Room cached Quran verses
        val matchedQuran = QuranCatalogData.offlineFeaturedAyahs.firstOrNull { ayah ->
            val ayahNorm = normalizeArabic(ayah.arabicUthmani)
            ayahNorm.isNotBlank() && (normalized.contains(ayahNorm.take(24)) || ayahNorm.contains(normalized.take(24)))
        }

        if (matchedQuran != null) {
            return OcrVerificationResult(
                extractedText = extractedText,
                verificationStatus = "pending",
                matchedReference = "Quran ${matchedQuran.surahNumber}:${matchedQuran.ayahNumber} (${matchedQuran.translatorEn})",
                summary = "Potential Quran match detected (${matchedQuran.surahNumber}:${matchedQuran.ayahNumber}). Human admin verification of spelling & diacritics is still required before publishing."
            )
        }

        val dbQuranMatches = if (normalized.length >= 6) {
            noorDao.searchCachedAyahs(extractedText.take(18))
        } else emptyList()

        if (dbQuranMatches.isNotEmpty()) {
            val first = dbQuranMatches.first()
            return OcrVerificationResult(
                extractedText = extractedText,
                verificationStatus = "pending",
                matchedReference = "Quran ${first.surahNumber}:${first.ayahNumber} (Cached Uthmani)",
                summary = "Matched cached Quran ${first.surahNumber}:${first.ayahNumber}. Awaiting human admin approval."
            )
        }

        val matchedHadith = HadithLibraryData.allHadiths.firstOrNull { h ->
            val hNorm = normalizeArabic(h.arabicText)
            hNorm.isNotBlank() && normalized.contains(hNorm.take(20))
        }

        if (matchedHadith != null) {
            return OcrVerificationResult(
                extractedText = extractedText,
                verificationStatus = "pending",
                matchedReference = "${matchedHadith.bookName} #${matchedHadith.hadithNumber} (${matchedHadith.authenticity.label})",
                summary = "Matched ${matchedHadith.bookName} #${matchedHadith.hadithNumber}. Awaiting human admin approval."
            )
        }

        return OcrVerificationResult(
            extractedText = extractedText,
            verificationStatus = "pending",
            matchedReference = "Unverified — Requires Manual Source Check",
            summary = "No exact automatic match found in local Quran/Hadith corpus. Never assume OCR is correct; requires human scholar/admin review against original collections before publishing."
        )
    }

    private fun normalizeArabic(input: String): String {
        return input
            .replace(Regex("[\\u064B-\\u065F\\u0670]"), "")
            .replace('أ', 'ا')
            .replace('إ', 'ا')
            .replace('آ', 'ا')
            .replace('ى', 'ي')
            .replace('ة', 'ه')
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private companion object {
        const val TAG = "IslamicAiRepo"
    }
}
