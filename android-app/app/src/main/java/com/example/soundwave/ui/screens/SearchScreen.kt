package com.example.soundwave.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.soundwave.viewModels.LibraryViewModel
import com.example.soundwave.viewModels.SearchViewModel

@Composable
fun SearchScreen(navController: NavController, searchViewModel: SearchViewModel = viewModel()) {
    val query by searchViewModel.query
    val results by searchViewModel.results
    val loading by searchViewModel.isLoading

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), contentAlignment = Alignment.TopCenter) {
        
        androidx.compose.foundation.layout.Column {
            BasicTextField(value = query, onValueChange = { searchViewModel.onQueryChange(it) }, modifier = Modifier.padding(8.dp))
            Button(onClick = { searchViewModel.search() }, modifier = Modifier.padding(8.dp)) {
                Text("Search")
            }
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            }
            results.forEach { r ->
                Text(text = r, modifier = Modifier.padding(6.dp))
            }
        }
    }
}
