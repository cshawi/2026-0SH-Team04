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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.example.soundwave.models.MusicTrack
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soundwave.navigation.Screen
import com.example.soundwave.ui.components.AudioPlayerController
import com.example.soundwave.viewModels.HomeViewModel






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
                    navController.navigate(Screen.Profile.route)
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
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {

    val searchText = homeViewModel.searchText.value
    val filteredMusic = homeViewModel.getFilteredMusic()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            TopBar(navController)
            Header()

            Spacer(modifier = Modifier.height(15.dp))


            Section("Recommandations")
            MusicRow(filteredMusic , navController)

            Spacer(modifier = Modifier.height(25.dp))

            Section("Genres")
            MusicRow(filteredMusic, navController)

            Spacer(modifier = Modifier.height(25.dp))

            Section("Découvrir")
            DiscoverList(filteredMusic, navController)

            Spacer(modifier = Modifier.height(25.dp))

            Section("🔥 Tendances")
            TrendButtons(navController)

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
fun Section(title:String){

    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        modifier = Modifier.padding(bottom = 15.dp)
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

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .width(170.dp)
            .clickable {
                AudioPlayerController.play(
                    context,
                    music.audioUrl,
                    music.title,
                    music.imageUrl,
                    music.id
                )
            }
    ){

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
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
fun DiscoverList(
    musics: List<MusicTrack>,
    navController: NavController
){

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){

        musics.take(4).forEach { music ->
            DiscoverItem(music, navController)
        }

    }

}

@Composable
fun DiscoverItem(music: MusicTrack, navController: NavController){

    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF1E1E2E),
                RoundedCornerShape(18.dp)
            )
            .clickable {
                navController.navigate("player/${music.id}")
            }
            .padding(16.dp),

        verticalAlignment = Alignment.CenterVertically
    ){

        AsyncImage(
            model = music.imageUrl,
            contentDescription = music.title,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ){

            Text(
                text = music.title,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "${music.duration.toInt()} sec",
                color = Color(0xFFA0A0B5),
                style = MaterialTheme.typography.bodySmall
            )

        }

        Button(
            onClick = {
                AudioPlayerController.play(
                    context,
                    music.audioUrl,
                    music.title,
                    music.imageUrl,
                    music.id
                )
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues()
        ){

            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF4FACFE),
                                Color(0xFF7B61FF),
                                Color(0xFF9F5DE2)
                            )
                        ),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ){

                Text(
                    "Play",
                    color = Color.White
                )

            }

        }

    }

}

@Composable
fun TrendButtons(navController: NavController){

    Button(
        onClick = {
            navController.navigate(Screen.Create.route)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ){

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF36D1DC),
                            Color(0xFF5B86E5)
                        )
                    ),
                    RoundedCornerShape(30.dp)
                ),
            contentAlignment = Alignment.Center
        ){

            Text(
                "🎤 Générer une chanson",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )

        }

    }

}