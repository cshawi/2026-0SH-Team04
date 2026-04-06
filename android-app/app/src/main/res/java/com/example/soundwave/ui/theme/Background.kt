package com.example.soundwave.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun SoundWaveBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val baseGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF050812),
            Color(0xFF0C0A1F),
            Color(0xFF120A24),
            Color(0xFF06060F)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseGradient)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val topWave = Path().apply {
                moveTo(0f, height * 0.18f)
                cubicTo(
                    width * 0.22f,
                    height * 0.08f,
                    width * 0.55f,
                    height * 0.28f,
                    width,
                    height * 0.16f
                )
                lineTo(width, 0f)
                lineTo(0f, 0f)
                close()
            }
            drawPath(topWave, color = Color(0xFF1F3470).copy(alpha = 0.18f))

            val middleWave = Path().apply {
                moveTo(0f, height * 0.48f)
                cubicTo(
                    width * 0.2f,
                    height * 0.38f,
                    width * 0.6f,
                    height * 0.62f,
                    width,
                    height * 0.46f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(middleWave, color = Color(0xFF3F2378).copy(alpha = 0.14f))

            val bottomWave = Path().apply {
                moveTo(0f, height * 0.7f)
                cubicTo(
                    width * 0.3f,
                    height * 0.6f,
                    width * 0.55f,
                    height * 0.88f,
                    width,
                    height * 0.76f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(bottomWave, color = Color(0xFF8E2E63).copy(alpha = 0.12f))
        }

        content()
    }
}
