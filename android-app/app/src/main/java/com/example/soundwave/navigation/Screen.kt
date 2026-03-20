package com.example.soundwave.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    var route: String,
    val icon: ImageVector
) {

    object Home: Screen("Home", icon = Icons.Default.Home)
    object Create: Screen("Create", icon = Icons.Default.Add)
    object Search: Screen("Search", icon = Icons.Default.Search)
    object Library: Screen("Library", icon = Icons.Default.LibraryMusic)
    object Profile: Screen("Profile", icon = Icons.Default.Person)

    object Player: Screen("Player/{title}/{duration}", icon = Icons.Default.Home)
}