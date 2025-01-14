package com.example.vanishcomposable.Animation

import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate

fun DrawScope.mosaic(
    animationProgress: State<Float>,
    col: Int,
    row: Int,
    pixel: Int,
    squareSize: Float,
    baseX: Float,
    baseY: Float,
    cols: Int,
    rows: Int
) {
    // Calculate the dominant color for the square area
    val alpha = (1f - animationProgress.value).coerceIn(0f, 1f)
    val color = Color(pixel)

    // Create path for the square
    val path = Path().apply {
        moveTo(baseX, baseY)
        lineTo(baseX + squareSize, baseY)
        lineTo(baseX + squareSize, baseY + squareSize)
        lineTo(baseX, baseY + squareSize)
        close()
    }

    // Apply animation effect
    val scale = (1f - animationProgress.value).coerceIn(0f, 1f)
    val centerX = baseX + squareSize / 2
    val centerY = baseY + squareSize / 2

    // Scale from center
    translate(centerX, centerY) {
        scale(scale, scale) {
            translate(-centerX, -centerY) {
                drawPath(
                    path = path,
                    color = color.copy(alpha = alpha)
                )
            }
        }
    }
}