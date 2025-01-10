package com.example.vanishcomposable.Animation

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal fun DrawScope.scatter(
    animationProgress: State<Float>,
    randomValue: Float,
    pixel: Int,
    dotSizePx: Float,
    baseX: Float,
    baseY: Float
) {
    val progress = animationProgress.value
    val angle = randomValue * 2 * PI.toFloat()
    val distance = progress * size.width * randomValue
    val scale = 1 - progress * 0.5f
    val alpha = (1 - progress).coerceIn(0f, 1f)

    if (alpha > 0) {
        drawCircle(
            color = Color(pixel).copy(alpha = alpha),
            radius = dotSizePx / 2 * scale,
            center = Offset(
                baseX + cos(angle) * distance,
                baseY + sin(angle) * distance
            )
        )
    }
}