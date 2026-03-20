package com.example.soundwave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.soundwave.navigation.Screen
import com.example.soundwave.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf("") }

    val isLoading = viewModel.isLoading.value
    val viewModelError = viewModel.errorMessage.value

    val shimmerProgress = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // ~60 FPS
            shimmerProgress.floatValue = (shimmerProgress.floatValue + 0.02f) % 1f
        }
    }

    val errorMessage = localError.ifEmpty { viewModelError ?: "" }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Retour",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { navController.navigateUp() },
                tint = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Connexion",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Connectez-vous pour continuer",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    localError = ""
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = Color(0xFF6C5CE7),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    localError = ""
                },
                label = { Text("Mot de passe") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = Color(0xFF6C5CE7),
                    unfocusedBorderColor = Color.Gray
                )
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            ShimmerButton(
                text = if (isLoading) "Connexion en cours..." else "Se connecter",
                colors = listOf(
                    Color(0xFF6C5CE7),
                    Color(0xFFA463F5)
                ),
                shimmerProgress = shimmerProgress.floatValue,
                isLoading = isLoading,
                onClick = {
                    when {
                        email.isBlank() || password.isBlank() ->
                            localError = "Veuillez remplir tous les champs"
                        else -> {
                            val success = viewModel.login(email, password)
                            if (success) {
                                navController.navigate(Screen.Profile.route) {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Pas encore de compte ? ",
                    color = Color.Gray
                )
                Text(
                    text = "S'inscrire",
                    color = Color(0xFF6C5CE7),
                    modifier = Modifier
                        .clickable { navController.navigate("register") }
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun ShimmerButton(
    text: String,
    colors: List<Color>,
    shimmerProgress: Float,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(
                Brush.horizontalGradient(
                    colors = colors
                )
            )
            .graphicsLayer(alpha = 0.99f)
            .drawWithContent {
                drawContent()

                if (!isLoading) {
                    val shimmerWidth = size.width * 0.5f
                    val shimmerX = size.width * (shimmerProgress - 0.2f)

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

                    drawRect(
                        brush = shimmerBrush,
                        size = size
                    )
                }
            }
            .clickable(enabled = !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    color = Color.White
                )
            }
        } else {
            Text(
                text = text,
                color = Color.White
            )
        }
    }
}