package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// AlQuran.cloud API responses (Free, open-source Quran text & translations)
data class AlQuranMultiEditionResponse(
    val code: Int = 0,
    val status: String = "",
    val data: List<AlQuranSurahEdition> = emptyList()
)

data class AlQuranSurahEdition(
    val number: Int = 0,
    val name: String = "",
    val englishName: String = "",
    val ayahs: List<AlQuranAyahDto> = emptyList(),
    val edition: AlQuranEditionInfo = AlQuranEditionInfo()
)

data class AlQuranEditionInfo(
    val identifier: String = "",
    val language: String = "",
    val name: String = "",
    val englishName: String = ""
)

data class AlQuranAyahDto(
    val number: Int = 0,
    val text: String = "",
    val numberInSurah: Int = 0
)

// Quran.com API v4 responses (Word-by-Word authentic meanings & transliteration)
data class QuranComVersesResponse(
    val verses: List<QuranComVerseDto> = emptyList()
)

data class QuranComVerseDto(
    val id: Int = 0,
    @Json(name = "verse_key") val verseKey: String = "",
    @Json(name = "verse_number") val verseNumber: Int = 0,
    val words: List<QuranComWordDto> = emptyList()
)

data class QuranComWordDto(
    val position: Int = 0,
    @Json(name = "char_type_name") val charTypeName: String = "",
    @Json(name = "text_uthmani") val textUthmani: String? = null,
    val text: String? = null,
    val translation: QuranComTextDto? = null,
    val transliteration: QuranComTextDto? = null
)

data class QuranComTextDto(
    val text: String? = null
)

interface AlQuranCloudApi {
    @GET("surah/{surahNumber}/editions/quran-uthmani,en.sahih,ur.jalandhry,en.transliteration")
    suspend fun getSurahEditions(
        @Path("surahNumber") surahNumber: Int
    ): AlQuranMultiEditionResponse
}

interface QuranComV4Api {
    @GET("verses/by_chapter/{chapter}")
    suspend fun getChapterWords(
        @Path("chapter") chapter: Int,
        @Query("words") words: Boolean = true,
        @Query("word_fields") wordFields: String = "text_uthmani",
        @Query("per_page") perPage: Int = 300
    ): QuranComVersesResponse
}

object QuranApiFactory {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val alQuranApi: AlQuranCloudApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.alquran.cloud/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AlQuranCloudApi::class.java)
    }

    val quranComApi: QuranComV4Api by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.quran.com/api/v4/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(QuranComV4Api::class.java)
    }
}
