package com.infiren.crispyapp.service

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
   // private const val BASE_URL = "http://212.220.204.239:25565"
    private const val BASE_URL = "http://192.168.0.10:25565"
   // private const val BASE_URL = "http://79.137.248.25:25565"

    private lateinit var retrofit: Retrofit

    fun getClient(context: Context): ApiService {
        if (!::retrofit.isInitialized) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val authInterceptor = Interceptor { chain ->
                val token = getToken(context)
                val requestBuilder = chain.request().newBuilder()
                token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(StringConverterFactory.create())
                .client(httpClient)
                .build()
        }

        return retrofit.create(ApiService::class.java)
    }

    private fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }
}
