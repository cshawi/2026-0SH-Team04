package com.example.soundwave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.soundwave.navigation.NavGraph
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.ui.theme.SoundWaveTheme
import com.example.soundwave.viewModels.MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val mainVM: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        val splash = installSplashScreen()

        super.onCreate(savedInstanceState)

        splash.setKeepOnScreenCondition {
            !mainVM.isReady.value
        }

        splash.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.view.animate()
                .alpha(0f)
                .setDuration(300)
//                .withEndAction {
//                    splashScreenView.remove()
//                }
                .start()
        }

        enableEdgeToEdge()

        setContent {
            SoundWaveTheme {
                //Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

                    val context = LocalContext.current
                    val state = mainVM.screenState.value
                    val isLoading = mainVM.isLoadingUser.value

                    LaunchedEffect(Unit) {
                        mainVM.startInit(context)
                    }

                    AnimatedContent(
                        targetState = state,
                        transitionSpec = {
                            slideInHorizontally(
                                initialOffsetX = { it / 2 },
                                animationSpec = tween(400)
                            ) + fadeIn(animationSpec = tween(400)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { -it / 2 },
                                        animationSpec = tween(400)
                                    ) + fadeOut(animationSpec = tween(400))
                        },
                        label = "screen_transition"
                    ) { targetState ->

                        when (targetState) {

                            "intro" -> {
                                IntroScreen(
                                    isLoading = isLoading,
                                    onStartLoading = {
                                        mainVM.loadUser()
                                    }
                                )
                            }

                            "app" -> {
                                CompositionLocalProvider(LocalActivity provides this@MainActivity) {
                                    NavGraph()
                                }
                            }
                        }
                    }
              //  }
            }
        }
    }
}

@Composable
fun IntroScreen(
    isLoading: Boolean,
    onStartLoading: () -> Unit
) {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(500)
        onStartLoading()
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1800),
        label = "intro_fade"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo_splash1),
            contentDescription = null,
            modifier = Modifier.size(300.dp)
        )

        if (isLoading) {
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}


