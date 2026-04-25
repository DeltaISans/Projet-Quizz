package com.example.quizz

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.quizz.viewmodel.QuizzViewModel
import com.example.quizz.model.Niveau
import com.example.quizz.model.Question
import com.example.quizz.model.QuizzApercu
import com.example.quizz.model.QuizzDetail
import com.example.quizz.model.Reponse
import com.example.quizz.model.Theme
import com.example.quizz.model.TypeQuestion

// =====================================================================
// ÉCRAN 1 : ACCUEIL
// =====================================================================

// COMPOSANT PARENT : Gère la navigation
@Composable
fun EcranAccueil(navController: NavHostController) {
    // On appelle une version "UI seule" pour faciliter la preview
    ContenuAccueil(onCommencerClick = { navController.navigate("choix") })
}

// COMPOSANT ENFANT : Gère uniquement le visuel (Dumb Component)
@Composable
fun ContenuAccueil(onCommencerClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Mon Super Quiz Start", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onCommencerClick) {
            Text("Commencer")
        }
    }
}

// =====================================================================
// ÉCRAN 2 : CHOIX DU QUIZ
// =====================================================================
@Composable
fun EcranChoix(navController: NavHostController, viewModel: QuizzViewModel = viewModel()) {
    // On déclenche le chargement des données au démarrage de l'écran
    LaunchedEffect(Unit) {
        viewModel.chargerListeQuestionnaires()
    }

    // On passe les états du ViewModel à la Vue "passive"
    ContenuChoix(
        listQuizz = viewModel.listeQuizApercu.value,
        isLoading = viewModel.isLoading.value,
        onSelected = { id ->
            navController.navigate("page_jeu/$id")
        }
    )
}

// COMPOSANT ENFANT : Affiche la liste des questionnaires
@Composable
fun ContenuChoix(
    listQuizz: List<QuizzApercu>,
    isLoading: Boolean,
    onSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Choisissez votre Quiz", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally // Centre les colonnes dans la liste
            ) {
                items(listQuizz) { quiz ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally // Centre le bouton et le texte entre eux
                    ) {
                        Button(
                            onClick = { onSelected(quiz.id) },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(top = 8.dp, bottom = 4.dp)
                        ) {
                            Text(quiz.libelle + " : " + quiz.nbQuestions + " questions")
                        }

                        Text(
                            text = "Créé le " + quiz.datecreation,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

// =====================================================================
// ÉCRAN 3 : JEU EN COURS
// =====================================================================

// COMPOSANT PARENT : Logique métier et gestion du score
@Composable
fun EcranJeu(
    quizId: Int,
    navController: NavHostController,
    viewModel: QuizzViewModel = viewModel()
) {
    // 1. On demande au contrôleur de charger le quiz correspondant à l'ID
    LaunchedEffect(quizId) {
        viewModel.chargerQuizDetail(quizId)
    }

    // 2. On récupère les états du contrôleur
    val quiz = viewModel.quizActuel.value
    val isFinished = viewModel.isFinished.value

    // On récupère la question précise selon l'index
    val questionActuelle = if (isFinished) null else quiz?.questions?.getOrNull(viewModel.indexQuestionActuelle.value)

    // 3. ON APPELLE LE CONTENU (La Vue)
    ContenuJeu(
        quizLibelle = quiz?.libelle ?: "",
        question = questionActuelle,
        isLoading = viewModel.isLoading.value,
        score = viewModel.score.value,
        scoreTotal = viewModel.scoreTotal.value,
        onBackClick = { navController.popBackStack() },
        onReponseClick = { reponseId ->
            // Vérification de la réponse et calcul du score
            val reponseChoisie = questionActuelle?.reponses?.find { it.id == reponseId }
            Log.w("id reponse :", reponseId.toString())
            Log.w("icorrect reponse :", reponseChoisie?.estCorrecte.toString())
            Log.w("score reponse :", questionActuelle?.niveaux?.nbpoints.toString())
            if(reponseChoisie?.estCorrecte == true){
                viewModel.score.value += questionActuelle.niveaux.nbpoints
            } else {
                viewModel.score.value += 0
            }
            viewModel.scoreTotal.value += questionActuelle!!.niveaux!!.nbpoints
            viewModel.passerALaQuestionSuivante()
        }
    )
}

// COMPOSANT ENFANT : Affichage pur de la question ou de l'écran de fin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenuJeu(
    quizLibelle: String,
    question: Question?,
    isLoading: Boolean,
    score: Int,
    scoreTotal : Int,
    onBackClick: () -> Unit,
    onReponseClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = quizLibelle.ifEmpty { "Chargement..." }) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center, // Centre tout verticalement
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (question != null) {
                // 0. le theme

                // 1. Le titre de la question
                Text(
                    text = question.libelle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 2. LA LOGIQUE DES BOUTONS (C'est ici qu'on décide 2 ou 4)
                if (question.type.libelle.contains("Vrai", ignoreCase = true)) {
                    // MODE VRAI/FAUX : Une colonne de boutons larges
                    question.reponses.forEach { reponse ->
                        Button(
                            onClick = { onReponseClick(reponse.id) },
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // Pas trop large non plus
                                .padding(vertical = 8.dp)
                                .height(60.dp)
                        ) {
                            Text(text = reponse.libelle, fontSize = 18.sp)
                        }
                    }
                } else {
                    // MODE QCM : Une grille de 2x2
                    question.reponses.chunked(2).forEach { paire ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            paire.forEach { reponse ->
                                Button(
                                    onClick = { onReponseClick(reponse.id) },
                                    modifier = Modifier
                                        .weight(1f) // 50% de la largeur chacun
                                        .padding(vertical = 6.dp)
                                        .height(80.dp)
                                ) {
                                    Text(text = reponse.libelle, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            } else {
                // ÉCRAN DE FIN DE QUIZ
                Text("Félicitations !", fontSize = 50.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(30.dp))
                Text("Vous avez terminé le quiz.", fontSize = 30.sp)
                Spacer(modifier = Modifier.height(30.dp))
                Text("Score : $score / $scoreTotal", fontSize = 45.sp)
                Spacer(modifier = Modifier.height(40.dp))
                Button(onClick = onBackClick) { Text("Choisir un autre quizz") }
            }
        }
    }
}

// =====================================================================
// PREVIEWS (Pour tester le design sans lancer l'app)
// =====================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAccueil() {
    // On peut tester le visuel sans avoir besoin de Navigation !
    ContenuAccueil(onCommencerClick = {})
}

// --- LA PREVIEW CHOIX ---
@Preview(showBackground = true)
@Composable
fun PreviewChoix() {
    val fakeQuizzes = listOf(
        QuizzApercu(id = 1, libelle = "Quiz Histoire", "2025-25-2", 10),
        QuizzApercu(id = 2, libelle = "Quiz Sciences", "2026-02-02", 10),
        QuizzApercu(id = 3, libelle = "Quiz Sciences po", "2026-01-01", 15)
    )

    ContenuChoix(
        listQuizz = fakeQuizzes,
        isLoading = false,
        onSelected = {}
    )
}

// --- 3. LA PREVIEW (Simulée avec de fausses données) ---
@Preview(showBackground = true, showSystemUi = true, name = "Preview QCM")
@Composable
fun PreviewJeu() {
    // On simule l'état où le quiz est chargé et on affiche la question 0
    val quiz = MockData.questionnaireTest
    val premiereQuestion = quiz.questions[0]

    ContenuJeu(
        quizLibelle = quiz.libelle,
        question = premiereQuestion, // 👈 On passe la question au lieu du quiz entier
        isLoading = false,
        score = 0,
        scoreTotal = 100,
        onBackClick = {},
        onReponseClick = {}
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Preview Vrai/Faux")
@Composable
fun PreviewJeuVF() {
    // Utile pour tester si tes 2 boutons s'affichent bien différemment des 4 boutons
    val quiz = MockData.questionnaireTest
    val secondeQuestion = quiz.questions[1]

    ContenuJeu(
        quizLibelle = quiz.libelle,
        question = secondeQuestion,
        isLoading = false,
        score = 0,
        scoreTotal = 100,
        onBackClick = {},
        onReponseClick = {}
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Preview Fin jeu")
@Composable
fun PreviewFinJeu() {
    // Utile pour tester si tes 2 boutons s'affichent bien différemment des 4 boutons
    val quiz = MockData.questionnaireTest


    ContenuJeu(
        quizLibelle = quiz.libelle,
        question = null,
        isLoading = false,
        score = 20,
        scoreTotal = 100,
        onBackClick = {},
        onReponseClick = {}
    )
}

object MockData {
    val questionnaireTest = QuizzDetail(
        id = 1,
        libelle = "Quiz Histoire (JSON Mock)",
        questions = listOf(
            Question(
                id = 101,
                libelle = "Qui était le premier roi des Francs ?",
                type = TypeQuestion(1,"QCM"),
                reponses = listOf(
                    Reponse(1, "Clovis", true),
                    Reponse(2, "Charlemagne", false),
                    Reponse(3, "Louis XIV", false),
                    Reponse(4, "Napoléon", false)
                ),
                niveaux = Niveau(1, "Debutant", 1),
                themes = listOf(Theme(1, "Cybersecurité")),
            ),
            Question(
                id = 102,
                libelle = "Es tu Superman ?",
                type = TypeQuestion(2,"Vrai/Faux"),
                reponses = listOf(
                    Reponse(1, "Vrai", true),
                    Reponse(2, "Faux", false)
                ),
                niveaux = Niveau(1, "Debutant", 1),
                themes = listOf(Theme(1, "Cybersecurité")),
            )
        )
    )
}