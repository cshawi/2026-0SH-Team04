package com.example.soundwave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.soundwave.ui.viewmodel.ProfileViewModel
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun AuthChoiceScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    // Vérifier si l'utilisateur est déjà connecté
    val isLoggedIn = viewModel.currentUser.value != null

    // Animation pour la brillance
    val shimmerProgress = remember { mutableFloatStateOf(0f) }

    // Animation infinie pour la brillance
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(16) // ~60 FPS
            shimmerProgress.floatValue = (shimmerProgress.floatValue + 0.02f) % 1f
        }
    }

    // Redirection automatique si déjà connecté
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("profile") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    // Pas de background ici - on utilise celui par défaut du thème
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Logo SoundWave",
            modifier = Modifier.size(80.dp),
            tint = Color(0xFF6C5CE7)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Titre
        Text(
            text = "SoundOfSoul",
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Sous-titre
        Text(
            text = "Votre musique, votre univers",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(60.dp))

        // BOUTON CONNEXION AVEC DÉGRADÉ ET BRILLANCE
        ShimmerButton(
            text = "Se connecter",
            colors = listOf(
                Color(0xFF6C5CE7),
                Color(0xFFA463F5)
            ),
            shimmerProgress = shimmerProgress.floatValue,
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // BOUTON INSCRIPTION AVEC DÉGRADÉ ET BRILLANCE
        ShimmerButton(
            text = "S'inscrire",
            colors = listOf(
                Color(0xFF00B894),
                Color(0xFF00CEC9)
            ),
            shimmerProgress = shimmerProgress.floatValue,
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ShimmerButton(
    text: String,
    colors: List<Color>,
    shimmerProgress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(27.dp))
            .background(
                Brush.horizontalGradient(
                    colors = colors
                )
            )
            .graphicsLayer(alpha = 0.99f) // Évite les problèmes de clipping
            .drawWithContent {
                drawContent()

                // Calcul de la position de la brillance
                val shimmerWidth = size.width * 0.5f
                val shimmerX = size.width * (shimmerProgress - 0.2f)

                // Dégradé de brillance (blanc semi-transparent)
                val shimmerBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0f),
                        Color.White.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.8f),
                        Color.White.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0f)
                    ),
                    start = Offset(shimmerX, 0f),
                    end = Offset(shimmerX + shimmerWidth, 0f)
                )

                // Dessine la brillance
                drawRect(
                    brush = shimmerBrush,
                    size = size
                )
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}