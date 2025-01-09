package com.example.vanishcomposable.Animation

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.directional(
    animationProgress: State<Float>,
    randomValue: Float,
    effect: AnimationEffect,
    pixel: Int,
    dotSizePx: Float,
    baseX: Float,
    baseY: Float
) {
    val dotProgress = (animationProgress.value * randomValue)
        .coerceIn(0f, 1f)
    val alpha = 1f - dotProgress

    val offset = when (effect) {
        AnimationEffect.LEFT -> Offset(size.width * dotProgress, 0f)
        AnimationEffect.RIGHT -> Offset(
            -size.width * dotProgress,
            0f
        )

        AnimationEffect.UP -> Offset(0f, size.height * dotProgress)
        AnimationEffect.DOWN -> Offset(
            0f,
            -size.height * dotProgress
        )

        else -> Offset.Zero
    }

    if (alpha > 0) {
        drawCircle(
            color = Color(pixel).copy(alpha = alpha),
            radius = dotSizePx / 2,
            center = Offset(
                baseX + dotSizePx / 2 + offset.x,
                baseY + dotSizePx / 2 + offset.y
            )
        )
    }
}