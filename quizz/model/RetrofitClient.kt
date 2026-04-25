package com.example.quizz.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "http://10.0.12.78/quizz/"

    // lazy = l'instance ne sera créée que la première fois qu'on l'appelle
    val instance: QuizzApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Transforme le JSON en Objets Kotlin
            .build()
            .create(QuizzApiService::class.java)
    }
}