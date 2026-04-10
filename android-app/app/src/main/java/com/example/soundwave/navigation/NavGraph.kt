package com.example.soundwave.navigation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soundwave.ui.LocalActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.soundwave.data.repository.UserSession
import com.example.soundwave.ui.components.AudioPlayerBar
import com.example.soundwave.ui.components.BottomNavBar
import com.example.soundwave.ui.screens.CreateScreen
import com.example.soundwave.ui.screens.HomeScreen
import com.example.soundwave.ui.screens.SearchScreen
import com.example.soundwave.ui.screens.LibraryScreen
import com.example.soundwave.ui.screens.ProfileScreen
import com.example.soundwave.ui.theme.SoundWaveBackground
import com.example.soundwave.ui.screens.PlayerScreen
import com.example.soundwave.ui.screens.*
import com.example.soundwave.viewModels.PlayerViewModel
import com.example.soundwave.viewModels.ProfileViewModel

@Composable
fun NavGraph() {

    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val profileViewModel: ProfileViewModel = viewModel(LocalActivity.current)
    val playerViewModel: PlayerViewModel = viewModel(LocalActivity.current)

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
                        CreateScreen(navController)
                    }

                    composable(Screen.Search.route) {
                        SearchScreen(navController)
                    }

                    composable(Screen.Library.route) {
                        LibraryScreen(navController)
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen(navController, profileViewModel)
                    }

                    composable("login") {
                        LoginScreen(navController, profileViewModel)
                    }

                    composable("register") {
                        RegisterScreen(navController, profileViewModel)
                    }

                    composable("auth") {
                        AuthChoiceScreen(navController)
                    }

                    composable("Player") {
                        //backStackEntry ->

                        //val musicId = backStackEntry.arguments?.getString("musicId") ?: ""

                        PlayerScreen(
                            navController = navController,
                            playerViewModel
                        )
                    }
                }
            }
    }
}
