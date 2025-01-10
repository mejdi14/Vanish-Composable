package com.example.vanishcomposable.composable

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import android.graphics.Bitmap
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.example.vanishcomposable.Animation.AnimationEffect
import com.example.vanishcomposable.Animation.directional
import com.example.vanishcomposable.Animation.dissolve
import com.example.vanishcomposable.Animation.explode
import com.example.vanishcomposable.Animation.pixelate
import com.example.vanishcomposable.Animation.scatter
import com.example.vanishcomposable.Animation.shatter
import com.example.vanishcomposable.Animation.swirl
import com.example.vanishcomposable.Animation.wave
import com.example.vanishcomposable.controller.AnimationController
import com.example.vanishcomposable.controller.AnimationStateHolder
import kotlinx.coroutines.delay
import kotlin.random.Random


@Composable
fun VanishComposable(
    modifier: Modifier = Modifier,
    dotSize: Float = 6f,
    spacing: Float = 2f,
    effect: AnimationEffect = AnimationEffect.LEFT,
    onControllerReady: (AnimationController) -> Unit = {},
    content: @Composable () -> Unit
) {
    val stateHolder = remember { AnimationStateHolder() }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var composeView by remember { mutableStateOf<ComposeView?>(null) }
    var onAnimationFinish by remember { mutableStateOf<() -> Unit>({}) }
    val density = LocalDensity.current.density

    val randomValues = remember {
        List(1000) { Random.nextFloat() }
    }
    val controller = remember {
        object : AnimationController {
            override fun triggerVanish(onFinish: () -> Unit) {
                stateHolder.vanished = true
                onAnimationFinish = onFinish
            }

            override fun reset() {
                stateHolder.vanished = false
                stateHolder.isAnimating = false
                bitmap = null
                onAnimationFinish = {}
            }
        }
    }

    LaunchedEffect(controller) {
        onControllerReady(controller)
    }

    val animationProgress = animateFloatAsState(
        targetValue = if (stateHolder.isAnimating) 1f else 0f,
        animationSpec = tween(
            durationMillis = when (effect) {
                AnimationEffect.SHATTER -> 1500
                AnimationEffect.WAVE -> 1800
                else -> 1200
            },
            easing = FastOutSlowInEasing
        ),
        finishedListener = {
            if (it >= 1f) {
                onAnimationFinish()
            }
        }, label = ""
    )

    LaunchedEffect(composeView) {
        composeView?.let { view ->
            delay(100)
            if (bitmap == null) {
                try {
                    val capturedBitmap = view.drawToBitmap(Bitmap.Config.ARGB_8888)
                    bitmap = capturedBitmap.asImageBitmap()
                } catch (e: Exception) {
                    println("Error capturing bitmap: ${e.message}")
                }
            }
        }
    }

    Box(modifier = modifier) {
        if (bitmap == null || !stateHolder.vanished) {
            AndroidView(
                factory = { context ->
                    ComposeView(context).apply {
                        setContent {
                            Box(modifier = Modifier.fillMaxSize()) {
                                content()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    composeView = view
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { stateHolder.isAnimating = true }
            ) {
                stateHolder.isAnimating = true
                VanishCanvas(
                    dotSize,
                    density,
                    spacing,
                    bitmap,
                    randomValues,
                    effect,
                    animationProgress
                )
            }
        }
    }
}
