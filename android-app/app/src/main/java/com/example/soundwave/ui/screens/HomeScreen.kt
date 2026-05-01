package com.example.soundwave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.navigation.Screen
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.ui.components.AudioPlayerController
import com.example.soundwave.util.TimeUtils
import com.example.soundwave.viewModels.HomeViewModel
import com.example.soundwave.viewModels.PlayerViewModel
import kotlin.random.Random

val genresFallbackNames = listOf("Pop", "Rap", "Afro", "Jazz", "Rock")

@Composable
fun TopBar(navController: NavController, homeViewModel: HomeViewModel){

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

        val user = homeViewModel.getUser()

        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF9C4DFF))
                .clickable { navController.navigate(Screen.Profile.route) },
            contentAlignment = Alignment.Center
        ) {
            if (user?.avatarUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(user.avatarUrl).crossfade(true).build(),
                    contentDescription = user.name,
                    modifier = Modifier.size(45.dp).clip(RoundedCornerShape(50)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = user?.name?.take(2)?.uppercase() ?: "AN",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

    }

}

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {

    val searchText = homeViewModel.searchText.value
    val searchResults = homeViewModel.searchResults
    // fetch recommendations and discover list once when the HomeScreen composes
    LaunchedEffect(Unit) {
        homeViewModel.launchRecommendation()
        homeViewModel.fetchDiscover()
        // initial search (populates searchResults) if there's a value
        if (searchText.isNotBlank()) homeViewModel.searchTracks()
    }


    LaunchedEffect(searchText) {
        homeViewModel.searchTracks()
    }

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

   
            TopBar(navController, homeViewModel)
            Header(homeViewModel)

            Spacer(modifier = Modifier.height(15.dp))

            Section("Recommandation")
            val displayedRecommendations: List<MusicTrack> = homeViewModel.recommendationList.ifEmpty { searchResults }
            MusicRow(displayedRecommendations, navController)

            Spacer(modifier = Modifier.height(25.dp))


            Section("Genres")
            // load styles and render
            LaunchedEffect(Unit) { homeViewModel.loadStyles() }
            val styles = homeViewModel.styles.ifEmpty { genresFallbackNames }
            GenreRow(styles, navController)

            Spacer(modifier = Modifier.height(25.dp))

            Section("Découvrir")
            DiscoverList(homeViewModel.discoverList.toList(), navController)

            Spacer(modifier = Modifier.height(25.dp))

            Section("🔥 Tendances")
            TrendButtons(navController)

        }

    }

}

@Composable
fun GenreRow(styleNames: List<String>, navController: NavController) {

    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        items(styleNames) { name ->
            GenreCard(name, navController)
        }
    }
}
@Composable
fun GenreCard(genreName: String, navController: NavController) {

    val fallbackColors = listOf(
        Color.Gray,
        Color.Magenta,
        Color(0xFF7F0000), // rouge très sombre
        Color(0xFF4A148C), // violet très profond
        Color(0xFF004D40), // teal très sombre
        Color(0xFFE65100), // orange brûlé sombre
        Color(0xFFF57F17), // jaune ambré foncé
        Color(0xFF1B5E20), // vert très sombre
        Color(0xFF880E4F), // rose/bordeaux profond
        Color(0xFF1A237E)  // bleu nuit
    )

    val color = fallbackColors[Random.nextInt(fallbackColors.size)]

    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(color)
            .clickable {
                navController.navigate("genre/${genreName}")
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = genreName,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

    }
}

@Composable
fun Header(homeViewModel: HomeViewModel){

    val user = homeViewModel.getUser()

    Column {

        Text(
            text = "Bienvenue ${user?.name ?: "Utilisateur"}",
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

    val activity = LocalActivity.current
    val playerViewModel: PlayerViewModel = viewModel(activity)
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ){
        items(musics){
            MusicCard(it, musics, navController, playerViewModel)
        }
    }
}

@Composable
fun MusicCard(music: MusicTrack, parentList: List<MusicTrack>, navController: NavController, playerViewModel: PlayerViewModel){

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .width(170.dp)
            .clickable {
                AudioPlayerController.play(context, music, parentList, playerViewModel)
            }
    ){

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ){

            AsyncImage(
                model = music.coverUrl,
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

        Row(verticalAlignment = Alignment.CenterVertically) {

            val fallbackColors = listOf(
                Color.Gray,
                Color.Magenta,
                Color.Red,
                Color(0xFF9F5DE2),
                Color(0xFF36D1DC),
                Color(0xFFFFA726), 
                Color(0xFFFFD54F), 
                Color(0xFF66BB6A), 
                Color(0xFFEC407A),
                Color(0xFF5C6BC0)  
            )

            val styleColor = fallbackColors[Random.nextInt(fallbackColors.size)]
            val displayName = music.username ?: (music.username ?: "AI")

            Box(modifier = Modifier
                .background(color = styleColor, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(displayName, color = Color.White, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = TimeUtils.formatSecondsToMMSS(music.duration),
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
        }

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
            DiscoverItem(music, musics, navController)
        }

    }

}

@Composable
fun DiscoverItem(music: MusicTrack, parentList: List<MusicTrack>, navController: NavController){

    val context = LocalContext.current
    val playerViewModel: PlayerViewModel = viewModel(LocalActivity.current)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF1E1E2E),
                RoundedCornerShape(18.dp)
            )
            .clickable {
                AudioPlayerController.play(context, music, parentList, playerViewModel)
                //navController.navigate("player")
            }
            .padding(16.dp),

        verticalAlignment = Alignment.CenterVertically
    ){

        AsyncImage(
            model = music.coverUrl,
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
                text = TimeUtils.formatSecondsToMMSS(music.duration),
                color = Color(0xFFA0A0B5),
                style = MaterialTheme.typography.bodySmall
            )

        }

        Button(
            onClick = {
                AudioPlayerController.play(context, music, parentList, playerViewModel)
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