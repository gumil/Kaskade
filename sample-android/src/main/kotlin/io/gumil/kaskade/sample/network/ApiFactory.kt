package io.gumil.kaskade.sample.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import io.gumil.kaskade.sample.BuildConfig
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

internal object ApiFactory {

    private const val BASE_URL = "https://dog.ceo"

    fun create(url: String = BASE_URL) =
        createmApi(url, createOkHttpClient(createLoggingInterceptor(BuildConfig.DEBUG)))

    private fun createmApi(url: String, okHttpClient: OkHttpClient): RandomDogApi =
        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(createConverter())
            .build().create(RandomDogApi::class.java)

    private fun createOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

    private fun createLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    private fun createConverter(): Converter.Factory {
        return MoshiConverterFactory.create(Moshi.Builder().build())
    }
}

interface RandomDogApi {
    @GET("/api/breed/shiba/images/random")
    fun getDog(): Deferred<Dog>
}

data class Dog(val message: String)