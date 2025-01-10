package com.example.vanishcomposable.Animation

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal fun DrawScope.swirl(
    animationProgress: State<Float>,
    baseX: Float,
    centerX: Float,
    baseY: Float,
    centerY: Float,
    pixel: Int,
    dotSizePx: Float
) {
    val progress = animationProgress.value
    val dx = baseX - centerX
    val dy = baseY - centerY
    val distance = sqrt(dx * dx + dy * dy)
    val angle = atan2(dy, dx) +
            (1 - distance / size.width) * progress * 4 * PI.toFloat()

    val alpha = (1 - progress).coerceIn(0f, 1f)
    if (alpha > 0) {
        drawCircle(
            color = Color(pixel).copy(alpha = alpha),
            radius = dotSizePx / 2,
            center = Offset(
                centerX + cos(angle) * distance,
                centerY + sin(angle) * distance
            )
        )
    }
}