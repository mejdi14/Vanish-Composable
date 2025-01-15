package com.example.vanishcomposable.Animation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.sqrt


fun DrawScope.mosaic(
    animationProgress: State<Float>,
    col: Int,
    row: Int,
    pixel: Int,
    squareSize: Float,
    baseX: Float,
    baseY: Float,
    cols: Int,
    rows: Int,
    tiltDegrees: List<Float>,
    randomShiftsCache1: Array<Float>,
    randomScales: List<Float>,
) {
    val delayDuration = 5f
    val rawProgress = (animationProgress.value * (1 + delayDuration) - delayDuration)
        .coerceIn(0f, 1f)
    val easedProgress = FastOutSlowInEasing.transform(rawProgress)

    val fadeStart = 0.8f
    val alpha = if (easedProgress < fadeStart) {
        1f
    } else {
        val fractionIntoFade = (easedProgress - fadeStart) / (1f - fadeStart)
        1f - fractionIntoFade
    }

    if (easedProgress <= 0f) {
        drawRect(
            color = Color(pixel),
            topLeft = Offset(baseX, baseY),
            size = Size(squareSize, squareSize)
        )
        return
    }
    val canvasCenterX = cols * squareSize / 2
    val canvasCenterY = rows * squareSize / 2
    val pixelCenterX = baseX + squareSize / 2
    val pixelCenterY = baseY + squareSize / 2

    val vectorX = pixelCenterX - canvasCenterX
    val vectorY = pixelCenterY - canvasCenterY
    val length = sqrt(vectorX * vectorX + vectorY * vectorY)
    val normalizedX = if (length != 0f) vectorX / length else 0f
    val normalizedY = if (length != 0f) vectorY / length else 0f

    val explosionDistance = 580f * easedProgress
    val moveX = normalizedX * explosionDistance
    val moveY = normalizedY * explosionDistance

    val index = row * cols + col
    val rotationDegree = tiltDegrees[index]
    val scaleNow = randomScales[index]

    withTransform({
        rotate(
            degrees = rotationDegree,
            pivot = Offset(
                (baseX + moveX) + (squareSize / 2),
                (baseY + moveY) + (squareSize / 2)
            )
        )
        scale(
            scaleX = scaleNow,
            scaleY = scaleNow
        )
    }) {
        drawRect(
            color = Color(pixel),
            topLeft = Offset(baseX + moveX, baseY + moveY),
            size = Size(squareSize, squareSize),
            alpha = alpha
        )
    }
}
