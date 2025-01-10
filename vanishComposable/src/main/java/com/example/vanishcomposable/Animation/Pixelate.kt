package com.example.vanishcomposable.Animation

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.pixelate(
    animationProgress: State<Float>,
    col: Int,
    row: Int,
    pixel: Int,
    dotSizePx: Float,
    baseX: Float,
    spacingPx: Float,
    baseY: Float
) {
    val progress = animationProgress.value
    val gridSize = (progress * 20).toInt() + 1
    val cellX = (col / gridSize) * gridSize
    val cellY = (row / gridSize) * gridSize

    if (col % gridSize == 0 && row % gridSize == 0) {
        val alpha = (1 - progress).coerceIn(0f, 1f)
        val scale = gridSize.toFloat()

        drawCircle(
            color = Color(pixel).copy(alpha = alpha),
            radius = dotSizePx / 2 * scale,
            center = Offset(
                baseX + (dotSizePx + spacingPx) * gridSize / 2,
                baseY + (dotSizePx + spacingPx) * gridSize / 2
            )
        )
    }
}