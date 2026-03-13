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
import com.example.soundwave.ui.components.screens.CreateScreen
import com.example.soundwave.ui.components.screens.HomeScreen
import com.example.soundwave.ui.components.screens.ProfileScreen
import com.example.soundwave.ui.theme.SoundWaveBackground
import com.example.soundwave.ui.components.screens.PlayerScreen
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavGraph() {

    val navController = rememberNavController()
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route
    SoundWaveBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {

                Column {

                    if (!currentRoute.orEmpty().startsWith("Player")) {
                        AudioPlayerBar(navController)
                    }

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
                    HomeScreen(navController)
                }

                composable(Screen.Create.route) {
                    CreateScreen()
                }

                composable(Screen.Profile.route) {
                    ProfileScreen()
                }

                composable("Player/{musicId}") { backStackEntry ->

                    val musicId = backStackEntry.arguments?.getString("musicId") ?: ""

                    PlayerScreen(
                        musicId = musicId,
                        navController = navController
                    )
                }


            }

        }
    }

}