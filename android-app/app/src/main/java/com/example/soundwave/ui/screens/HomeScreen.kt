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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import com.example.soundwave.models.MusicTrack
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale



//val musicList = listOf(
//    Music("Night Drive","2:47"),
//    Music("Chill Vibes","3:12"),
//    Music("Electro Dream","2:29"),
//    Music("Sunset Lo-Fi","2:51")
//)

val musicList = listOf(
    MusicTrack(
        id = "5",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        imageUrl = "https://images.unsplash.com/photo-1508704019882-f9cf40e475b4",
        title = "Chill Waves",
        duration = 210.20,
        createdAt = "34"
    ),

    MusicTrack(
        id = "6",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4",
        title = "Ocean Beats",
        duration = 184.15,
        createdAt = "34"
    ),

    MusicTrack(
        id = "7",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f",
        title = "Night Lights",
        duration = 220.50,
        createdAt = "34"
    ),

    MusicTrack(
        id = "8",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
        imageUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d",
        title = "Lo-Fi Dreams",
        duration = 176.80,
        createdAt = "34"
    ),

    MusicTrack(
        id = "9",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
        imageUrl = "https://images.unsplash.com/photo-1507874457470-272b3c8d8ee2",
        title = "Midnight Groove",
        duration = 205.10,
        createdAt = "34"
    ),
    MusicTrack(id = "1",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        imageUrl = "https://www.causeur.fr/wp-content/uploads/2020/12/gims-music-awards.jpg",
        title = "Night Drive",
        duration = 198.44,
        createdAt = "34"),

    MusicTrack(id = "2",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        imageUrl = "https://www.causeur.fr/wp-content/uploads/2020/12/gims-music-awards.jpg",
        title = "Next play",
        duration = 198.44,
        createdAt = "34"),

    MusicTrack(id = "3",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        imageUrl = "https://www.causeur.fr/wp-content/uploads/2020/12/gims-music-awards.jpg",
        title = "Night Drive",
        duration = 198.44,
        createdAt = "34"),

    MusicTrack(id = "4",
        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        imageUrl = "https://www.causeur.fr/wp-content/uploads/2020/12/gims-music-awards.jpg",
        title = "Night Drive",
        duration = 198.44,
        createdAt = "34")
)



@Composable
fun TopBar(navController: NavController){

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
                )
                .clickable {
                    navController.navigate("profile")
                },
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
    val context = LocalContext.current
    val filteredMusic = musicList.filter {
        it.title.contains(searchText, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
//            .background(
//                Brush.verticalGradient(
//                    listOf(
//                        Color(0xFF1A0933),
//                        Color(0xFF2E0F5A),
//                        Color(0xFF120421)
//                    )
//                )
//            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            TopBar(navController)
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
fun MusicRow(musics: List<MusicTrack>, navController: NavController){

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ){

        items(musics){
            MusicCard(it, navController)
        }

    }

}
@Composable
fun MusicCard(music: MusicTrack, navController: NavController){

    Column(
        modifier = Modifier
            .width(170.dp)
            .clickable {
                navController.navigate("player/${music.id}")
            }
    ){

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)   // image carrée
        ){

            AsyncImage(
                model = music.imageUrl,
                contentDescription = music.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = music.title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            maxLines = 1
        )

        Text(
            text = "${music.duration.toInt()} sec",
            style = MaterialTheme.typography.bodySmall,
            color = Color.LightGray
        )

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