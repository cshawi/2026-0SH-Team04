package com.example.soundwave.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soundwave.viewModels.ProfileViewModel

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {

    val isLoggedIn = viewModel.isLoggedIn.value

    if (!isLoggedIn) {
        LoginScreen(viewModel)
    } else {
        ProfileContent(viewModel)
    }
}

@Composable
fun LoginScreen(viewModel: ProfileViewModel) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Connexion",
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nom utilisateur") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        )
                    )
                )
                .clickable { viewModel.loginTestUser(name, email) },
            contentAlignment = Alignment.Center
        ){
            Text("Se connecter")
        }
    }
}

@Composable
fun ProfileContent(viewModel: ProfileViewModel) {

    val user = viewModel.user.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        if (user?.avatarUrl != null) {

            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "Photo profil",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        } else {

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar par défaut",
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = user?.name ?: "",
            fontSize = 24.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user?.email ?: "",
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(27.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        )
                    )
                )
                .clickable { viewModel.logout() },
            contentAlignment = Alignment.Center
        ){
            Text("Se déconnecter")
        }
    }
}