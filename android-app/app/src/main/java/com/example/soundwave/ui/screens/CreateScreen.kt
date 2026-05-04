package com.example.soundwave.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.soundwave.navigation.Screen
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.ui.components.AudioPlayerController
import com.example.soundwave.viewModels.CreateViewModel
import com.example.soundwave.viewModels.LibraryViewModel
import com.example.soundwave.viewModels.PlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateScreen(navController: NavController, createViewModel: CreateViewModel = viewModel(), libraryViewModel: LibraryViewModel = viewModel()) {
    var showAllStyles by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val playerViewModel: PlayerViewModel = viewModel(LocalActivity.current)
    val loadingTransition = rememberInfiniteTransition(label = "loading")
    val loadingDotsProgress by loadingTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(animation = tween(900, easing = LinearEasing)),
        label = "loadingDots"
    )
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            createViewModel.onImageSelected(uri)
        }
    )

    val user = createViewModel.getUser()

    // ensure playlists are loaded from server for the picker
    LaunchedEffect(Unit) {
        libraryViewModel.loadPlaylists()
    }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current


    LaunchedEffect(Unit) {
        AudioPlayerController.ensureInitialized(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scrollState = rememberScrollState()
        val resultsBringIntoViewRequester = remember { BringIntoViewRequester() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var isPressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.92f else 1f,
                label = "backScale"
            )
            val backBgColor by animateColorAsState(
                targetValue = if (isPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                else MaterialTheme.colorScheme.surfaceVariant,
                label = "backBgColor"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clip(CircleShape)
                        .background(backBgColor)
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                isPressed = true
                                try {
                                    awaitRelease()
                                    navController.navigateUp()
                                } finally {
                                    isPressed = false
                                }
                            })
                        }
                        .border(
                            width = 1.dp,
                            color = if (isPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.36f) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = if (isPressed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    buildAnnotatedString {
                        append("Créer une ")
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("Musique")
                        }
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Donne vie à ta créativité ✨",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.7f
                    )
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            imagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudUpload,
                            contentDescription = "Importer",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .height(35.dp)
                                .width(40.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            buildAnnotatedString {
                                append("Importer une image ")
                                withStyle(
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = 0.7f
                                        )
                                    )
                                ) {
                                    append("(optionnel)")
                                }
                            },
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Clique pour ajouter",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (createViewModel.imageUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(createViewModel.imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Image sélectionnée",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Image,
                                contentDescription = "Ajouter une image",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mode personnalisé",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Switch(
                            checked = createViewModel.isCustomMode,
                            onCheckedChange = { createViewModel.isCustomMode = it }
                        )
                    }

                    if (createViewModel.isCustomMode) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Title,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Titre",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        TextField(
                            value = createViewModel.title,
                            onValueChange = { createViewModel.title = it },
                            placeholder = { Text("Ex: Rêves Électriques") },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(35.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .clickable {
                                            focusManager.clearFocus()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Terminer",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Style musical",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Text(
                                text = if (showAllStyles) "Réduire" else "Voir tout",
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 15.dp, vertical = 5.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 2.dp)
                                    .clickable { showAllStyles = !showAllStyles }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val visibleStyles =
                            if (showAllStyles) createViewModel.styles else createViewModel.styles.take(
                                4
                            )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(visibleStyles, key = { it.name }) { style ->
                                val isSelected = createViewModel.selectedStyle == style
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) style.color.copy(alpha = 0.18f)
                                        else MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) style.color else style.color.copy(
                                                alpha = 0.5f
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .padding(2.dp)
                                        .clickable { createViewModel.selectedStyle = style }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = style.icon,
                                            contentDescription = null,
                                            tint = style.color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = style.name,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Instrumental",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Switch(
                            checked = createViewModel.isInstrumental,
                            onCheckedChange = { createViewModel.isInstrumental = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (createViewModel.isCustomMode && !createViewModel.isInstrumental) "Paroles" else "Description",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    val descriptionBringRequester = remember { BringIntoViewRequester() }
                    val coroutineScope = rememberCoroutineScope()

                    TextField(
                        value = createViewModel.description,
                        onValueChange = { new ->
                            val old = createViewModel.description
                            createViewModel.description = new
                            // if user added a newline at the end while keyboard is visible, bring field into view
                            if (new.length > old.length && new.lastOrNull() == '\n') {
                                coroutineScope.launch {
                                    try {
                                        // wait for IME to settle then bring into view
                                        kotlinx.coroutines.delay(120)
                                        descriptionBringRequester.bringIntoView()
                                        // perform a small extra scroll to ensure caret isn't cut
                                        try {
                                            scrollState.animateScrollBy(120f)
                                        } catch (_: Exception) {
                                        }
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        },
                        placeholder = { Text(if (createViewModel.isCustomMode && !createViewModel.isInstrumental) "Écrivez vos propres paroles, deux couplets pour un meilleur résultat." else "Décris ton idée, ton ambiance, ton style...") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(descriptionBringRequester),
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(35.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable {
                                        focusManager.clearFocus()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Terminer",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .clickable { createViewModel.onGenerateClicked() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    if (createViewModel.isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Filled.GraphicEq,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = if (createViewModel.isGenerating) {
                            val dots = "".padEnd(((loadingDotsProgress.toInt() % 3) + 1), '.')
                            "Génération en cours$dots"
                        } else {
                            "Générer la musique"
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                }
            }

            if (createViewModel.generationError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = createViewModel.generationError ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            val generationResult = createViewModel.generationResult

            LaunchedEffect(generationResult) {
                if (generationResult != null) {
                    resultsBringIntoViewRequester.bringIntoView()
                }
            }

            if (generationResult != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.bringIntoViewRequester(resultsBringIntoViewRequester)) {
                    Text(
                        text = "Résultats générés",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    generationResult.tracks.forEach { track ->
                        val isTrackPlaying = AudioPlayerController.isPlaying &&
                                AudioPlayerController.currentUrl == track.audioUrl
                        val eqPulse by rememberInfiniteTransition(label = "eqPulse").animateFloat(
                            initialValue = 0.9f,
                            targetValue = 1.1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    600,
                                    easing = LinearEasing
                                )
                            ),
                            label = "eqPulseAnim"
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    AudioPlayerController.play(
                                        context,
                                        track,
                                        generationResult.tracks,
                                        playerViewModel
                                    )
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(track.coverUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Couverture générée",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.matchParentSize()
                                    )
                                    if (isTrackPlaying) {
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .background(Color.Black.copy(alpha = 0.35f))
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.GraphicEq,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .graphicsLayer(
                                                    scaleX = eqPulse,
                                                    scaleY = eqPulse,
                                                    alpha = 0.9f
                                                )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.AccessTimeFilled,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${track.createdAt} ",
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Box(
                                    modifier = Modifier
                                        .offset { IntOffset(0, -30) }
                                        .padding(5.dp, 0.dp)
                                ) {
                                    MusicOptionsMenu(track, libraryViewModel, context, coroutineScope)
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



