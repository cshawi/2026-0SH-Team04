package com.example.soundwave.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.soundwave.ui.components.AudioPlayerBar
import com.example.soundwave.ui.components.BottomNavBar
import com.example.soundwave.ui.screens.CreateScreen
import com.example.soundwave.ui.screens.HomeScreen
import com.example.soundwave.ui.screens.ProfileScreen
import com.example.soundwave.ui.theme.SoundWaveBackground

@Composable
fun NavGraph() {

    val navController = rememberNavController()
    SoundWaveBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                Column {
                    AudioPlayerBar()
                    BottomNavBar(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ){
                composable(Screen.Home.route) {
                    HomeScreen()
                }

                composable(Screen.Create.route) {
                    CreateScreen()
                }

                composable(Screen.Profile.route) {
                    ProfileScreen()
                }
            }

        }
    }

}