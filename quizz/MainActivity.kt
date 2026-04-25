package com.example.quizz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizz.ui.theme.QuizzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizzTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QuizApp()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizApp() {
    val navController = rememberNavController()

    // Gestionnaire de navigation (Le GPS de l'app)
    NavHost(navController = navController, startDestination = "accueil") {
        composable("accueil") { EcranAccueil(navController) }
        composable("choix") { EcranChoix(navController) }

        composable("page_jeu/{id}") { backStackEntry ->
            val quizzId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            // Pour l'instant, on redirige vers l'écran de jeu
            EcranJeu(quizzId, navController)
        }
    }
}

