import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.animation.core.*
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun DottedImage(
    image: ImageBitmap,
    dotSize: Float = 8f,
    spacing: Float = 2f,
    modifier: Modifier = Modifier
) {
    var isAnimating by remember { mutableStateOf(false) }
    val androidBitmap = remember(image) { image.asAndroidBitmap() }

    // Store random speeds for each dot
    val randomSpeeds = remember {
        List(1000) { Random.nextFloat() * 0.7f + 0.3f } // speeds between 0.3 and 1.0
    }

    val animationProgress = animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(
            durationMillis = 2000,
            easing = LinearEasing
        ),
        finishedListener = { isAnimating = false }
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(image.width.toFloat() / image.height.toFloat())
            .clickable { isAnimating = true }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val dotSizePx = dotSize * density
            val spacingPx = spacing * density

            val cols = (size.width / (dotSizePx + spacingPx)).roundToInt()
            val rows = (size.height / (dotSizePx + spacingPx)).roundToInt()

            val scaleX = androidBitmap.width.toFloat() / cols
            val scaleY = androidBitmap.height.toFloat() / rows

            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val baseX = col * (dotSizePx + spacingPx)
                    val baseY = row * (dotSizePx + spacingPx)

                    // Get random speed for this dot
                    val speedIndex = (row * cols + col) % randomSpeeds.size
                    val speed = randomSpeeds[speedIndex]

                    // Calculate individual dot progress
                    val dotProgress = (animationProgress.value * speed).coerceIn(0f, 1f)

                    // Move dots to the right and fade out with individual speeds
                    val offsetX = size.width * dotProgress
                    val alpha = 1f - dotProgress

                    if (alpha > 0) {
                        val pixelX = (col * scaleX).roundToInt().coerceIn(0, androidBitmap.width - 1)
                        val pixelY = (row * scaleY).roundToInt().coerceIn(0, androidBitmap.height - 1)
                        val pixel = androidBitmap.getPixel(pixelX, pixelY)
                        val dotColor = Color(pixel)

                        drawCircle(
                            color = dotColor.copy(alpha = alpha),
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