package com.example.soundwave.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soundwave.viewModels.CreateViewModel

@Composable
fun CreateScreen(createViewModel: CreateViewModel = viewModel()) {

    Text(
        text = "Create Screen"
    )

}
