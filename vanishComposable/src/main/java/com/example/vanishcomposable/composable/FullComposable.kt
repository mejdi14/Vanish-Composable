import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import android.graphics.Bitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import kotlin.math.*
import kotlin.random.Random


@Composable
fun ComposableAnimation(
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
        }
    }
}
