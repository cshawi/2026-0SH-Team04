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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.soundwave.ui.viewmodel.ProfileViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    var username by remember { mutableStateOf("") }  // ← Renommé
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf("") }

    // États du ViewModel
    val isLoading = viewModel.isLoading.value
    val viewModelError = viewModel.errorMessage.value

    // Combine les erreurs
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
            // Bouton retour
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Retour",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { navController.navigateUp() },
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Créer un compte",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rejoignez SoundWave",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Nom d'utilisateur
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    localError = ""
                },
                label = { Text("Nom d'utilisateur") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6C5CE7),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email
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
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6C5CE7),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mot de passe
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
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6C5CE7),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirmation
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    localError = ""
                },
                label = { Text("Confirmer le mot de passe") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
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

            // Bouton d'inscription
            Button(
                onClick = {
                    when {
                        username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                            localError = "Tous les champs sont requis"

                        password.length < 6 ->
                            localError = "Le mot de passe doit contenir au moins 6 caractères"

                        password != confirmPassword ->
                            localError = "Les mots de passe ne correspondent pas"

                        else -> {
                            val success = viewModel.register(username, email, password)
                            if (success) {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                            // Si erreur, elle sera dans viewModel.errorMessage
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00B894)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("S'inscrire")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lien vers connexion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Déjà un compte ? ",
                    color = Color.Gray
                )
                Text(
                    text = "Se connecter",
                    color = Color(0xFF6C5CE7),
                    modifier = Modifier
                        .clickable { navController.navigate("login") }
                        .padding(4.dp)
                )
            }
        }
    }
}