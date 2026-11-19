package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class AlAdhanTimingsResponse(
    val code: Int = 0,
    val status: String = "",
    val data: AlAdhanDayData? = null
)

data class AlAdhanCalendarResponse(
    val code: Int = 0,
    val status: String = "",
    val data: List<AlAdhanDayData> = emptyList()
)

data class AlAdhanDayData(
    val timings: AlAdhanTimings? = null,
    val date: AlAdhanDateWrapper? = null,
    val meta: AlAdhanMeta? = null
)

data class AlAdhanTimings(
    val Fajr: String = "",
    val Sunrise: String = "",
    val Dhuhr: String = "",
    val Asr: String = "",
    val Maghrib: String = "",
    val Isha: String = "",
    val Lastthird: String = ""
)

data class AlAdhanDateWrapper(
    val readable: String = "",
    val gregorian: AlAdhanGregorian? = null,
    val hijri: AlAdhanHijri? = null
)

data class AlAdhanGregorian(
    val date: String = "",
    val day: String = "",
    val month: AlAdhanMonth? = null,
    val year: String = ""
)

data class AlAdhanHijri(
    val date: String = "",
    val day: String = "",
    val month: AlAdhanHijriMonth? = null,
    val year: String = ""
)

data class AlAdhanMonth(
    val number: Int = 1,
    val en: String = ""
)

data class AlAdhanHijriMonth(
    val number: Int = 1,
    val en: String = "",
    val ar: String = ""
)

data class AlAdhanMeta(
    val method: AlAdhanMethodInfo? = null
)

data class AlAdhanMethodInfo(
    val id: Int = 1,
    val name: String = ""
)

interface AlAdhanApi {
    @GET("timings/{dateStr}")
    suspend fun getTimings(
        @Path("dateStr") dateStr: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
        @Query("tune") tune: String
    ): AlAdhanTimingsResponse

    @GET("gToHCalendar/{month}/{year}")
    suspend fun getGregorianToHijriCalendar(
        @Path("month") month: Int,
        @Path("year") year: Int
    ): AlAdhanCalendarResponse
}

object AlAdhanApiFactory {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val api: AlAdhanApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AlAdhanApi::class.java)
    }
}
