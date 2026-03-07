package com.example.soundwave.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.soundwave.navigation.Screen


@Composable
fun BottomNavBar(navController: NavController) {

    val screens = listOf(
        Screen.Home,
        Screen.Create,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {

        screens.forEach { screen ->

            NavigationBarItem(
                selected = currentRoute == screen.route,

                onClick = {
                    navController.navigate(screen.route){
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },

                icon = {
                    Icon(
                        screen.icon,
                        contentDescription = screen.route
                    )
                },

                label = {
                    Text(screen.route)
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Blue,
                    selectedTextColor = Color.Blue,
                    indicatorColor = MaterialTheme.colorScheme.primary,

                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray

                )

            )
        }

    }

}