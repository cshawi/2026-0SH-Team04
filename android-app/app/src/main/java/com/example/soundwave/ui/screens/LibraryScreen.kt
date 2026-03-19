package com.example.soundwave.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.soundwave.viewModels.LibraryViewModel

@Composable
fun LibraryScreen(navController: NavController, vm: LibraryViewModel = viewModel()) {
    val items by vm.items

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (items.isEmpty()) {
                Text("Aucun élément dans la bibliothèque")
            } else {
                items.forEach { itName ->
                    Text(text = itName, modifier = Modifier
                        .padding(vertical = 6.dp)
                        .clickable { /* TODO: play or open */ })
                }
            }
        }
    }
}
