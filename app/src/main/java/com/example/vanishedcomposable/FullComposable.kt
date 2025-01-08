import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap
import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ComposableToDots(
    modifier: Modifier = Modifier,
    dotSize: Float = 8f,
    spacing: Float = 2f,
    vanished: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isAnimating by remember { mutableStateOf(false) }
    var composeView by remember { mutableStateOf<ComposeView?>(null) }
    val density = LocalDensity.current.density

    val randomSpeeds = remember {
        List(1000) { Random.nextFloat() * 0.7f + 0.3f }
    }

    val animationProgress = animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        finishedListener = {
           // isAnimating = false
        }
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
        if (bitmap == null || vanished.value == false) {
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
                    .clickable { isAnimating = true }
                    .alpha(1f - animationProgress.value)
            ) {
                isAnimating = true
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val dotSizePx = dotSize * density
                    val spacingPx = spacing * density

                    val cols = (size.width / (dotSizePx + spacingPx)).toInt()
                    val rows = (size.height / (dotSizePx + spacingPx)).toInt()

                    val bmp = bitmap!!
                    val scaleX = bmp.width.toFloat() / cols
                    val scaleY = bmp.height.toFloat() / rows

                    for (row in 0 until rows) {
                        for (col in 0 until cols) {
                            val baseX = col * (dotSizePx + spacingPx)
                            val baseY = row * (dotSizePx + spacingPx)

                            val speedIndex = (row * cols + col) % randomSpeeds.size
                            val speed = randomSpeeds[speedIndex]

                            val dotProgress = (animationProgress.value * speed).coerceIn(0f, 1f)

                            val offsetX = size.width * dotProgress
                            val alpha = 1f - dotProgress

                            if (alpha > 0) {
                                val pixelX = (col * scaleX).toInt().coerceIn(0, bmp.width - 1)
                                val pixelY = (row * scaleY).toInt().coerceIn(0, bmp.height - 1)
                                val pixel = bmp.asAndroidBitmap().getPixel(pixelX, pixelY)

                                drawCircle(
                                    color = Color(pixel).copy(alpha = alpha),
                                    radius = dotSizePx / 2,
                                    center = androidx.compose.ui.geometry.Offset(
                                        baseX + dotSizePx / 2 + offsetX,
                                        baseY + dotSizePx / 2
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