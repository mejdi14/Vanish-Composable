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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.example.vanishcomposable.controller.AnimationController
import com.example.vanishcomposable.controller.AnimationStateHolder
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

enum class AnimationEffect {
    PIXELATE,
    SWIRL,
    SCATTER,
    SHATTER,
    WAVE,
    LEFT,
    RIGHT,
    UP,
    DOWN,
    DISSOLVE,
    EXPLODE
}

@Composable
fun ComposableAnimation(
    modifier: Modifier = Modifier,
    dotSize: Float = 6f,
    spacing: Float = 2f,
    effect: AnimationEffect = AnimationEffect.SCATTER,
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

                                AnimationEffect.EXPLODE -> {
                                    val centerX = size.width / 2
                                    val centerY = size.height / 2
                                    val dx = baseX - centerX
                                    val dy = baseY - centerY
                                    val angle = atan2(dy, dx)
                                    val distance = sqrt(dx * dx + dy * dy)

                                    val progress = animationProgress.value
                                    val speed = 1f + randomValue * 0.5f
                                    val newDistance = distance + (size.width * progress * speed)
                                    val alpha = (1f - progress) * (1f - distance / size.width)

                                    if (alpha > 0) {
                                        drawCircle(
                                            color = Color(pixel).copy(alpha = alpha),
                                            radius = dotSizePx / 2 * (1f - progress * 0.5f),
                                            center = Offset(
                                                centerX + cos(angle) * newDistance,
                                                centerY + sin(angle) * newDistance
                                            )
                                        )
                                    }
                                }


                                AnimationEffect.SHATTER -> {
                                    val dx = baseX - centerX
                                    val dy = baseY - centerY
                                    val distance = sqrt(dx * dx + dy * dy)
                                    val maxDistance =
                                        sqrt(size.width * size.width + size.height * size.height)
                                    val angle = atan2(dy, dx)

                                    val progress =
                                        (animationProgress.value + distance / maxDistance * 0.3f)
                                            .coerceIn(0f, 1f)
                                    val scale = 1 - progress * 0.5f
                                    val alpha = (1 - progress).coerceIn(0f, 1f)

                                    if (alpha > 0) {
                                        val speed = 1 + randomValue * 0.5f
                                        val newDistance =
                                            distance + (maxDistance * progress * speed)
                                        drawCircle(
                                            color = Color(pixel).copy(alpha = alpha),
                                            radius = dotSizePx / 2 * scale,
                                            center = Offset(
                                                centerX + cos(angle) * newDistance,
                                                centerY + sin(angle) * newDistance
                                            )
                                        )
                                    }
                                }

                                AnimationEffect.SCATTER -> {
                                    val progress = animationProgress.value
                                    val angle = randomValue * 2 * PI.toFloat()
                                    val distance = progress * size.width * randomValue
                                    val scale = 1 - progress * 0.5f
                                    val alpha = (1 - progress).coerceIn(0f, 1f)

                                    if (alpha > 0) {
                                        drawCircle(
                                            color = Color(pixel).copy(alpha = alpha),
                                            radius = dotSizePx / 2 * scale,
                                            center = Offset(
                                                baseX + cos(angle) * distance,
                                                baseY + sin(angle) * distance
                                            )
                                        )
                                    }
                                }

                                AnimationEffect.WAVE -> {
                                    val progress = animationProgress.value
                                    val waveHeight = size.height * 0.1f
                                    val frequency = 0.05f
                                    val speed = 10f

                                    val offsetX = sin(
                                        (baseY * frequency + progress * speed) +
                                                randomValue * 0.2f
                                    ) * waveHeight * progress

                                    val alpha =
                                        if (progress < 0.7f) 1f else (1 - (progress - 0.7f) / 0.3f)

                                    if (alpha > 0) {
                                        drawCircle(
                                            color = Color(pixel).copy(alpha = alpha),
                                            radius = dotSizePx / 2,
                                            center = Offset(
                                                baseX + offsetX,
                                                baseY
                                            )
                                        )
                                    }
                                }

                                AnimationEffect.PIXELATE -> {
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

                                AnimationEffect.SWIRL -> {
                                    val progress = animationProgress.value
                                    val dx = baseX - centerX
                                    val dy = baseY - centerY
                                    val distance = sqrt(dx * dx + dy * dy)
                                    val angle = atan2(dy, dx) +
                                            (1 - distance / size.width) * progress * 4 * PI.toFloat()

                                    val alpha = (1 - progress).coerceIn(0f, 1f)
                                    if (alpha > 0) {
                                        drawCircle(
                                            color = Color(pixel).copy(alpha = alpha),
                                            radius = dotSizePx / 2,
                                            center = Offset(
                                                centerX + cos(angle) * distance,
                                                centerY + sin(angle) * distance
                                            )
                                        )
                                    }
                                }

                                else -> {
                                    val dotProgress = (animationProgress.value * randomValue)
                                        .coerceIn(0f, 1f)
                                    val alpha = 1f - dotProgress

                                    val offset = when (effect) {
                                        AnimationEffect.LEFT -> Offset(size.width * dotProgress, 0f)
                                        AnimationEffect.RIGHT -> Offset(
                                            -size.width * dotProgress,
                                            0f
                                        )

                                        AnimationEffect.UP -> Offset(0f, size.height * dotProgress)
                                        AnimationEffect.DOWN -> Offset(
                                            0f,
                                            -size.height * dotProgress
                                        )

                                        else -> Offset.Zero
                                    }

                                    if (alpha > 0) {
                                        drawCircle(
                                            color = Color(pixel).copy(alpha = alpha),
                                            radius = dotSizePx / 2,
                                            center = Offset(
                                                baseX + dotSizePx / 2 + offset.x,
                                                baseY + dotSizePx / 2 + offset.y
                                            )
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
}