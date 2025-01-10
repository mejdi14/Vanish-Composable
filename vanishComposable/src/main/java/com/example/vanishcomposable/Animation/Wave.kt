package com.example.vanishcomposable.Animation

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin

internal fun DrawScope.wave(
    animationProgress: State<Float>,
    baseY: Float,
    randomValue: Float,
    pixel: Int,
    dotSizePx: Float,
    baseX: Float
) {
    val progress = animationProgress.value
    val waveHeight = size.height * 0.1f
    val frequency = 0.05f
    val speed = 10f

    val offsetX = sin(
        (baseY * frequency + progress * speed) +
                randomValue * 0.2f
    ) * waveHeight * progress

    val alpha =
        if (progress < 0.7f) 1f else (1 - (progress - 0.7f) / 0.3f)

    if (alpha > 0) {
        drawCircle(
            color = Color(pixel).copy(alpha = alpha),
            radius = dotSizePx / 2,
            center = Offset(
                baseX + offsetX,
                baseY
            )
        )
    }
}