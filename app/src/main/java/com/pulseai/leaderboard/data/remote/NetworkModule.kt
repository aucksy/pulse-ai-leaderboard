package com.pulseai.leaderboard.data.remote

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class KotlinxJsonConverterFactory(private val json: Json) : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val serializer = json.serializersModule.serializer(type)
        return Converter<ResponseBody, Any> { body ->
            body.use {
                json.decodeFromString(serializer, it.string())
            }
        }
    }
}

object NetworkModule {
    private const val BASE_URL = "https://openrouter.ai/api/v1/"

    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "PulseAI-Android/1.0")
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val apiService: OpenRouterApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(KotlinxJsonConverterFactory(json))
            .build()
            .create(OpenRouterApiService::class.java)
    }
}
