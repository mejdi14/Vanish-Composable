package com.example.vanishcomposable.composable

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.example.vanishcomposable.Animation.AnimationEffect
import com.example.vanishcomposable.VanishOptions
import com.example.vanishcomposable.controller.AnimationController
import com.example.vanishcomposable.controller.AnimationStateHolder
import kotlinx.coroutines.delay
import kotlin.random.Random


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VanishComposable(
    modifier: Modifier = Modifier,
    vanishOptions: VanishOptions = VanishOptions(),
    effect: AnimationEffect = AnimationEffect.LEFT_TO_RIGHT,
    onControllerReady: (AnimationController) -> Unit = {},
    content: @Composable () -> Unit
) {
    val stateHolder = remember { AnimationStateHolder() }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var composeView by remember { mutableStateOf<ComposeView?>(null) }
    var onAnimationFinish by remember { mutableStateOf<() -> Unit>({}) }
    val density = LocalDensity.current.density

    val randomValues = remember {
        List(vanishOptions.pixelVelocity) { Random.nextFloat() }
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
            durationMillis = vanishOptions.animationDuration,
            easing = FastOutSlowInEasing
        ),
        finishedListener = {
            if (it >= vanishOptions.triggerFinishAt) {
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
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                stateHolder.isAnimating = true
                VanishCanvas(
                    vanishOptions.pixelSize,
                    density,
                    vanishOptions.pixelSpacing,
                    bitmap,
                    randomValues,
                    effect,
                    animationProgress,
                    boxWidth = maxWidth,
                    boxHeight = maxHeight
                )
            }
        }
    }
}
