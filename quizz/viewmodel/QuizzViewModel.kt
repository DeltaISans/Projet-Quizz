package com.example.quizz.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizz.model.QuizzApercu
import com.example.quizz.model.QuizzDetail
import com.example.quizz.model.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

class QuizzViewModel(application: Application) : AndroidViewModel(application) {

    // ================= ÉTATS (Observables par l'interface) =================

    // La liste des quiz pour l'écran de choix
    var listeQuizApercu = mutableStateOf<List<QuizzApercu>>(emptyList())
    // Le quiz actuellement joué
    var quizActuel = mutableStateOf<QuizzDetail?>(null)

    // Variables de progression du jeu
    var indexQuestionActuelle = mutableStateOf(0)
    var score = mutableStateOf(0)
    var scoreTotal = mutableStateOf(0)
    var isFinished = mutableStateOf(false)
    var isLoading = mutableStateOf(false)

    // ================= ACTIONS (Appelées par l'interface) =================

    // Charge la liste de tous les quiz disponibles
    fun chargerListeQuestionnaires() {
        isLoading.value = true
        viewModelScope.launch { // Coroutine : s'exécute en arrière-plan
            try {
                val reponse = RetrofitClient.instance.getListeQuiz()
                listeQuizApercu.value = reponse.listeQuestionnaires
            } catch (e: HttpException) {
                // Si c'est une erreur HTTP (comme 404, 500)
                val urlAppelee = e.response()?.raw()?.request?.url
                val messageErreur = e.response()?.errorBody()?.string()

                Log.e("API_ERREUR", "🛑 L'URL qui a planté : $urlAppelee")
                Log.e("API_ERREUR", "🛑 Code erreur : ${e.code()}")
                //Log.e("API_ERREUR", "🛑 Réponse du serveur : $messageErreur")
            }catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    // Charge un quiz spécifique via son ID
    fun chargerQuizDetail(id: Int) {
        // 1. Réinitialisation des stats de la partie
        indexQuestionActuelle.value = 0
        score.value = 0
        scoreTotal.value = 0
        isFinished.value = false
        isLoading.value = true

        // 2. Appel API
        viewModelScope.launch {
            try {
                val reponse = RetrofitClient.instance.getQuizDetail(id)
                quizActuel.value = reponse.detailQuestionnaire
                Log.i("QuizViewModel", "Quiz chargé : ${reponse.detailQuestionnaire.libelle}")
            } catch (e: HttpException) {
                // Si c'est une erreur HTTP (comme 404, 500)
                val urlAppelee = e.response()?.raw()?.request?.url
                val messageErreur = e.response()?.errorBody()?.string()

                Log.e("API_ERREUR", "🛑 L'URL qui a planté : $urlAppelee")
                Log.e("API_ERREUR", "🛑 Code erreur : ${e.code()}")
                //Log.e("API_ERREUR", "🛑 Réponse du serveur : $messageErreur")
            }catch (e: Exception) {
                e.printStackTrace()

            } finally {
                isLoading.value = false
            }
        }
    }

    // Passe à la question suivante ou termine le quiz
    fun passerALaQuestionSuivante() {
        val quiz = quizActuel.value
        if (quiz != null && indexQuestionActuelle.value < quiz.questions.size - 1) {
            indexQuestionActuelle.value++
        } else {
            isFinished.value = true // Déclenche l'écran de fin
        }
    }
}