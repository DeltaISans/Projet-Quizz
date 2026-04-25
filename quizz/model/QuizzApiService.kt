package com.example.quizz.model

import retrofit2.http.GET
import retrofit2.http.Path

// Le "contrat" de notre API. Définit les routes (URL) et ce qu'elles renvoient.
interface QuizzApiService {

    // Route : /questionnaires/index-json.json
    @GET("questionnaires/index-json.json")
    suspend fun getListeQuiz(): ApiReponseListeQuestionnaires

    // Route : /questionnaires/view-json/{id}.json
    @GET("questionnaires/view-json/{id}.json")
    suspend fun getQuizDetail(@Path("id") id: Int): ApiReponseDetailQuestionnaire
}