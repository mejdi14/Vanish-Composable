package com.example.vanishcomposable.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.example.vanishcomposable.Animation.AnimationEffect
import com.example.vanishcomposable.Animation.directional
import com.example.vanishcomposable.Animation.dissolve
import com.example.vanishcomposable.Animation.explode
import com.example.vanishcomposable.Animation.pixelate
import com.example.vanishcomposable.Animation.scatter
import com.example.vanishcomposable.Animation.shatter
import com.example.vanishcomposable.Animation.swirl
import com.example.vanishcomposable.Animation.wave

@Composable
internal fun VanishCanvas(
    dotSize: Float,
    density: Float,
    spacing: Float,
    bitmap: ImageBitmap?,
    randomValues: List<Float>,
    effect: AnimationEffect,
    animationProgress: State<Float>
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val dotSizePx = dotSize * density
        val spacingPx = spacing * density

        val cols = (size.width / (dotSizePx + spacingPx)).toInt()
        val rows = (size.height / (dotSizePx + spacingPx)).toInt()

        val bmp = bitmap!!
        val scaleX = bmp.width.toFloat() / cols
        val scaleY = bmp.height.toFloat() / rows

        val centerX = size.width / 2
        val centerY = size.height / 2

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val baseX = col * (dotSizePx + spacingPx)
                val baseY = row * (dotSizePx + spacingPx)

                val rIndex = (row * cols + col) % randomValues.size
                val randomValue = randomValues[rIndex]

                val pixelX = (col * scaleX).toInt().coerceIn(0, bmp.width - 1)
                val pixelY = (row * scaleY).toInt().coerceIn(0, bmp.height - 1)
                val pixel = bmp.asAndroidBitmap().getPixel(pixelX, pixelY)

                when (effect) {
                    AnimationEffect.DISSOLVE -> {
                        dissolve(
                            animationProgress,
                            randomValue,
                            pixel,
                            dotSizePx,
                            baseX,
                            baseY
                        )
                    }

                    AnimationEffect.EXPLODE -> {
                        explode(
                            baseX,
                            baseY,
                            animationProgress,
                            randomValue,
                            pixel,
                            dotSizePx
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
                            dotSizePx
                        )
                    }

                    AnimationEffect.SCATTER -> {
                        scatter(
                            animationProgress,
                            randomValue,
                            pixel,
                            dotSizePx,
                            baseX,
                            baseY
                        )
                    }

                    AnimationEffect.WAVE -> {
                        wave(
                            animationProgress,
                            baseY,
                            randomValue,
                            pixel,
                            dotSizePx,
                            baseX
                        )
                    }

                    AnimationEffect.PIXELATE -> {
                        pixelate(
                            animationProgress,
                            col,
                            row,
                            pixel,
                            dotSizePx,
                            baseX,
                            spacingPx,
                            baseY
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
                            dotSizePx
                        )
                    }

                    else -> {
                        directional(
                            animationProgress,
                            randomValue,
                            effect,
                            pixel,
                            dotSizePx,
                            baseX,
                            baseY
                        )
                    }
                }
            }
        }
    }
}