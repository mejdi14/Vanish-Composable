package com.example.vanishcomposable.Animation

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal fun DrawScope.shatter(
    baseX: Float,
    centerX: Float,
    baseY: Float,
    centerY: Float,
    animationProgress: State<Float>,
    randomValue: Float,
    pixel: Int,
    dotSizePx: Float
) {
    val dx = baseX - centerX
    val dy = baseY - centerY
    val distance = sqrt(dx * dx + dy * dy)
    val maxDistance =
        sqrt(size.width * size.width + size.height * size.height)
    val angle = atan2(dy, dx)

    val progress =
        (animationProgress.value + distance / maxDistance * 0.3f)
            .coerceIn(0f, 1f)
    val scale = 1 - progress * 0.5f
    val alpha = (1 - progress).coerceIn(0f, 1f)

    if (alpha > 0) {
        val speed = 1 + randomValue * 0.5f
        val newDistance =
            distance + (maxDistance * progress * speed)
        drawCircle(
            color = Color(pixel).copy(alpha = alpha),
            radius = dotSizePx / 2 * scale,
            center = Offset(
                centerX + cos(angle) * newDistance,
                centerY + sin(angle) * newDistance
            )
        )
    }
}