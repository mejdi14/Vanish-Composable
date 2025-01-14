package com.example.vanishcomposable.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.vanishcomposable.Animation.AnimationEffect
import com.example.vanishcomposable.Animation.directional
import com.example.vanishcomposable.Animation.dissolve
import com.example.vanishcomposable.Animation.explode
import com.example.vanishcomposable.Animation.mosaic
import com.example.vanishcomposable.Animation.pixelate
import com.example.vanishcomposable.Animation.scatter
import com.example.vanishcomposable.Animation.shatter
import com.example.vanishcomposable.Animation.swirl
import com.example.vanishcomposable.Animation.wave
import com.example.vanishcomposable.helper.getDominantColor

@Composable
internal fun VanishCanvas(
    pixelSize: Float,
    density: Float,
    spacing: Float,
    bitmap: ImageBitmap?,
    randomValues: List<Float>,
    effect: AnimationEffect,
    animationProgress: State<Float>,
    isSquarePixel: Boolean = false // New parameter to control pixel shape
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val pixelSizePx = pixelSize * density
        val spacingPx = spacing * density

        val cols = (size.width / (pixelSizePx + spacingPx)).toInt()
        val rows = (size.height / (pixelSizePx + spacingPx)).toInt()

        val bmp = bitmap!!
        val scaleX = bmp.width.toFloat() / cols
        val scaleY = bmp.height.toFloat() / rows

        val centerX = size.width / 2
        val centerY = size.height / 2

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val baseX = col * (pixelSizePx + spacingPx)
                val baseY = row * (pixelSizePx + spacingPx)

                val rIndex = (row * cols + col) % randomValues.size
                val randomValue = randomValues[rIndex]

                val startX = (col * scaleX).toInt().coerceIn(0, bmp.width - 1)
                val startY = (row * scaleY).toInt().coerceIn(0, bmp.height - 1)

                val pixel = if (isSquarePixel && effect == AnimationEffect.MOSAIC) {
                    val endX = ((col + 1) * scaleX).toInt().coerceIn(0, bmp.width - 1)
                    val endY = ((row + 1) * scaleY).toInt().coerceIn(0, bmp.height - 1)
                    getDominantColor(bmp, startX, startY, endX, endY)
                } else {
                    bmp.asAndroidBitmap().getPixel(startX, startY)
                }

                when (effect) {
                    AnimationEffect.DISSOLVE -> {
                        dissolve(
                            animationProgress,
                            randomValue,
                            pixel,
                            pixelSizePx,
                            baseX,
                            baseY,
                        )
                    }

                    AnimationEffect.EXPLODE -> {
                        explode(
                            baseX,
                            baseY,
                            animationProgress,
                            randomValue,
                            pixel,
                            pixelSizePx,
                        )
                    }

                    AnimationEffect.SHATTER -> {
                        shatter(
                            baseX,
                            centerX,
                            baseY,
                            centerY,
                            animationProgress,
                            randomValue,
                            pixel,
                            pixelSizePx,
                        )
                    }

                    AnimationEffect.SCATTER -> {
                        scatter(
                            animationProgress,
                            randomValue,
                            pixel,
                            pixelSizePx,
                            baseX,
                            baseY,
                        )
                    }

                    AnimationEffect.WAVE -> {
                        wave(
                            animationProgress,
                            baseY,
                            randomValue,
                            pixel,
                            pixelSizePx,
                            baseX,
                        )
                    }

                    AnimationEffect.PIXELATE -> {
                        pixelate(
                            animationProgress,
                            col,
                            row,
                            pixel,
                            pixelSizePx,
                            baseX,
                            spacingPx,
                            baseY,
                        )
                    }

                    AnimationEffect.SWIRL -> {
                        swirl(
                            animationProgress,
                            baseX,
                            centerX,
                            baseY,
                            centerY,
                            pixel,
                            pixelSizePx,
                        )
                    }

                    AnimationEffect.MOSAIC -> {
                        mosaic(
                            animationProgress,
                            col,
                            row,
                            pixel,
                            pixelSizePx,
                            baseX,
                            baseY,
                            cols,
                            rows
                        )
                    }

                    else -> {
                        directional(
                            animationProgress,
                            randomValue,
                            effect,
                            pixel,
                            pixelSizePx,
                            baseX,
                            baseY,
                        )
                    }
                }
            }
        }
    }
}

// Extension function to draw either a circle or square
fun DrawScope.drawPixel(
    center: Offset,
    color: Color,
    size: Float,
    isSquare: Boolean = false
) {
    if (isSquare) {
        val topLeft = Offset(
            center.x - size / 2,
            center.y - size / 2
        )
        drawRect(
            color = color,
            topLeft = topLeft,
            size = Size(size, size)
        )
    } else {
        drawCircle(
            color = color,
            radius = size / 2,
            center = center
        )
    }
}