package dev.gumil.kaskade.sample.network

import com.squareup.moshi.Moshi
import dev.gumil.kaskade.sample.BuildConfig
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

internal object ApiFactory {

    private const val BASE_URL = "https://dog.ceo"
    private const val TIMEOUT = 10L

    fun create(url: String = BASE_URL) =
        createmApi(url, createOkHttpClient(createLoggingInterceptor(BuildConfig.DEBUG)))

    private fun createmApi(url: String, okHttpClient: OkHttpClient): RandomDogApi =
        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(createConverter())
            .build().create(RandomDogApi::class.java)

    private fun createOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()

    private fun createLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
            if (isDebug) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

    private fun createConverter(): Converter.Factory =
        MoshiConverterFactory.create(Moshi.Builder().build())
}

interface RandomDogApi {
    @GET("/api/breed/shiba/images/random")
    suspend fun getDog(): Dog
}

data class Dog(val message: String)
