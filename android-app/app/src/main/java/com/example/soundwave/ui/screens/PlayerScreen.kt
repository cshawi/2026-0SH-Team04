package com.example.soundwave.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlayerScreen(title:String, duration:String){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF120421)),
        contentAlignment = Alignment.Center
    ){

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Durée : $duration",
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = { }) {
                Text("▶ Play")
            }

        }

    }

}