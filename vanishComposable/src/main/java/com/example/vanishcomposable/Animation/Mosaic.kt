package com.example.vanishcomposable.Animation

import android.R.attr.scaleY
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.sqrt
import kotlin.random.Random

private val randomShiftsCache = mutableMapOf<Pair<Int, Int>, Float>()

private fun randomShiftFor(row: Int, col: Int): Float {
    return randomShiftsCache.getOrPut(row to col) {
        // Generate a random value between -20..+20
        Random.nextFloat() * 100f - 20f
    }
}

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

    val alpha = (1 - easedProgress).coerceIn(0f, 1f)

    if (easedProgress <= 0f) {
        drawRect(
            color = Color(pixel),
            topLeft = Offset(baseX, baseY),
            size = Size(squareSize, squareSize)
        )
        return
    }
    val randomAngles =
        Array(cols * rows) { Random.nextFloat() * 360f }


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
    val moveX = normalizedX * explosionDistance + Random.nextFloat()
    val moveY = normalizedY * explosionDistance

    val randomAngleForThisPixel = randomAngles[row * cols + col] // for example

    val index = row * cols + col
    val rotationDegree = tiltDegrees[index]




    withTransform({
        rotate(
            degrees = rotationDegree ,
            pivot = Offset(
                (baseX + moveX) + (squareSize / 2),
                (baseY + moveY) + (squareSize / 2)
            )
        )
        scale(
            scaleX = randomScales[index],
            scaleY = randomScales[index],
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
