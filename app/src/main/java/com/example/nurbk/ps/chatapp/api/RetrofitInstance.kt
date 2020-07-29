package com.example.nurbk.ps.chatapp.api

import com.example.nurbk.ps.chatapp.unit.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {

        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_MESSAGE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        val apiMessage by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}