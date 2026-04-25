package com.example.quizz.model

import com.google.gson.annotations.SerializedName

// =====================================================================
// 1. LES ENVELOPPES API (Ce que CakePHP renvoie globalement)
// =====================================================================

// Représente le JSON : { "questionnaires": [ {...}, {...} ] }
data class ApiReponseListeQuestionnaires(
    @SerializedName("questionnaires") val listeQuestionnaires: List<QuizzApercu>
)

// Représente le JSON : { "questionnaire": { "id": 3, "questions": [...] } }
data class ApiReponseDetailQuestionnaire(
    @SerializedName("questionnaire") val detailQuestionnaire: QuizzDetail
)

// =====================================================================
// 2. LES DONNÉES MÉTIER (Ce qu'on utilise dans l'application)
// =====================================================================

// Version "légère" d'un Quiz (utilisée pour afficher la liste de choix)
data class QuizzApercu(
    val id: Int,
    val libelle: String,
    val datecreation: String,
    @SerializedName("nb_questions") val nbQuestions: Int
)

// Version "complète" d'un Quiz (utilisée pour jouer)
data class QuizzDetail(
    val id: Int,
    val libelle: String,
    val questions: List<Question>
)

data class Question(
    val id: Int,
    val libelle: String,
    val niveaux: Niveau, // Le barème de points
    val themes: List<Theme>,
    val reponses: List<Reponse>,
    val type: TypeQuestion // Permet de savoir si c'est un QCM ou Vrai/Faux
)

data class Reponse(
    val id: Int,
    val libelle: String,
    @SerializedName("iscorrect") val estCorrecte: Boolean
)

data class Niveau(
    val id: Int,
    @SerializedName("niv") val nom: String,
    val nbpoints: Int
)

data class Theme(val id: Int, val libelle: String)

data class TypeQuestion(
    val id: Int,
    val libelle: String
)