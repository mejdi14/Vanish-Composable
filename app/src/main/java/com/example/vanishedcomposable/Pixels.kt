import android.graphics.Bitmap
import android.os.Looper
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.core.view.doOnLayout
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.postDelayed
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlin.math.roundToInt

@Composable
fun DottedImage(
    image: ImageBitmap,
    dotSize: Float = 2f,
    spacing: Float = 2f,
    modifier: Modifier = Modifier
) {
    // Convert ImageBitmap to Android Bitmap for pixel access
    val androidBitmap = remember(image) { image.asAndroidBitmap() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(image.width.toFloat() / image.height.toFloat())
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val paint = Paint()
            val dotSizePx = dotSize * density
            val spacingPx = spacing * density

            val cols = (size.width / (dotSizePx + spacingPx)).roundToInt()
            val rows = (size.height / (dotSizePx + spacingPx)).roundToInt()

            val scaleX = androidBitmap.width.toFloat() / cols
            val scaleY = androidBitmap.height.toFloat() / rows

            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val x = col * (dotSizePx + spacingPx)
                    val y = row * (dotSizePx + spacingPx)

                    // Sample color from the original image
                    val pixelX = (col * scaleX).roundToInt().coerceIn(0, androidBitmap.width - 1)
                    val pixelY = (row * scaleY).roundToInt().coerceIn(0, androidBitmap.height - 1)
                    val pixel = androidBitmap.getPixel(pixelX, pixelY)

                    drawCircle(
                        color = Color(pixel),
                        radius = dotSizePx / 2,
                        center = androidx.compose.ui.geometry.Offset(
                            x + dotSizePx / 2,
                            y + dotSizePx / 2
                        )
                    )
                }
            }
        }
    }
}
