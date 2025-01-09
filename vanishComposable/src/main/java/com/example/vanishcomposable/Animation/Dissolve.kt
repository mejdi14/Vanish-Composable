package com.example.vanishcomposable.Animation

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.dissolve(
    animationProgress: State<Float>,
    randomValue: Float,
    pixel: Int,
    dotSizePx: Float,
    baseX: Float,
    baseY: Float
) {
    val dissolveProgress =
        (animationProgress.value + randomValue * 0.3f)
            .coerceIn(0f, 1f)
    val alpha = 1f - dissolveProgress

    if (alpha > 0 && randomValue > dissolveProgress) {
        val shake = (randomValue - 0.5f) * 10 * dissolveProgress
        drawCircle(
            color = Color(pixel).copy(alpha = alpha),
            radius = dotSizePx / 2 * (1f - dissolveProgress * 0.3f),
            center = Offset(
                baseX + dotSizePx / 2 + shake,
                baseY + dotSizePx / 2 + shake
            )
        )
    }
}