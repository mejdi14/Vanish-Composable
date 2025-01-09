package com.example.vanishcomposable.Animation

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal fun DrawScope.explode(
    baseX: Float,
    baseY: Float,
    animationProgress: State<Float>,
    randomValue: Float,
    pixel: Int,
    dotSizePx: Float
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val dx = baseX - centerX
    val dy = baseY - centerY
    val angle = atan2(dy, dx)
    val distance = sqrt(dx * dx + dy * dy)

    val progress = animationProgress.value
    val speed = 1f + randomValue * 0.5f
    val newDistance = distance + (size.width * progress * speed)
    val alpha = (1f - progress) * (1f - distance / size.width)

    if (alpha > 0) {
        drawCircle(
            color = Color(pixel).copy(alpha = alpha),
            radius = dotSizePx / 2 * (1f - progress * 0.5f),
            center = Offset(
                centerX + cos(angle) * newDistance,
                centerY + sin(angle) * newDistance
            )
        )
    }
}