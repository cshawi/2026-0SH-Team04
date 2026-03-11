package com.example.soundwave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.clickable

data class Music(val title:String, val duration:String)

val musicList = listOf(
    Music("Night Drive","2:47"),
    Music("Chill Vibes","3:12"),
    Music("Electro Dream","2:29"),
    Music("Sunset Lo-Fi","2:51")
)

@Composable
fun TopBar(){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        Text(
            text = "Accueil",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Box(
            modifier = Modifier
                .size(45.dp)
                .background(
                    Color(0xFF9C4DFF),
                    shape = RoundedCornerShape(50)
                ),
            contentAlignment = Alignment.Center
        ){

            Text(
                "AN",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

        }

    }

}

@Composable
fun HomeScreen(navController: NavController) {

    var searchText by remember { mutableStateOf("") }

    val filteredMusic = musicList.filter {
        it.title.contains(searchText, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A0933),
                        Color(0xFF2E0F5A),
                        Color(0xFF120421)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            TopBar()
            Header()

            Spacer(modifier = Modifier.height(15.dp))

            SearchBar(
                searchText = searchText,
                onValueChange = { searchText = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Section("Recommandations")
            MusicRow(filteredMusic , navController)

            Spacer(modifier = Modifier.height(25.dp))

            Section("Genres")
            MusicRow(filteredMusic, navController)

            Spacer(modifier = Modifier.height(25.dp))

            Section("Découvrir")
            GenreList()

            Spacer(modifier = Modifier.height(25.dp))

            Section("🔥 Tendances")
            TrendButtons()

        }

    }

}

@Composable
fun Header(){

    Column {

        Text(
            "Bienvenue Pharel",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Text(
            "Prêt à explorer de la nouvelle musique ? ✨",
            color = Color(0xFFBFBFBF)
        )

    }

}

@Composable
fun SearchBar(searchText:String,onValueChange:(String)->Unit){

    TextField(
        value = searchText,
        onValueChange = onValueChange,
        placeholder = { Text("Rechercher une musique") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF2A1660),
            unfocusedContainerColor = Color(0xFF2A1660),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )

}

@Composable
fun Section(title:String){

    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        modifier = Modifier.padding(bottom = 10.dp)
    )

}

@Composable
fun MusicRow(musics: List<Music>, navController: NavController){

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ){

        items(musics){
            MusicCard(it, navController)
        }

    }

}

@Composable
fun MusicCard(music: Music, navController: NavController){

    Card(
        modifier = Modifier
            .width(200.dp)
            .height(140.dp)
            .clickable {
                navController.navigate("Player/${music.title}/${music.duration}")},
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF32176D)

        )
    ){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ){

            Text(
                music.title,
                color = Color.White
            )

            Text(
                music.duration,
                color = Color.LightGray
            )

        }

    }

}

@Composable
fun GenreList(){

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){

        GenreItem("Neon Groove","3:35")
        GenreItem("Soulful Chill","2:58")
        GenreItem("Cosmic Voyage","3:20")

    }

}

@Composable
fun GenreItem(title:String, duration:String){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF2E1A63),
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        Column {

            Text(title, color = Color.White)

            Text(duration, color = Color.LightGray)

        }

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9C4DFF)
            )
        ) {
            Text("Play")
        }

    }

}

@Composable
fun TrendButtons(){

    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF9C4DFF)
        )
    ){

        Text(
            "🎤 Générer une chanson",
            color = Color.White
        )

    }

}