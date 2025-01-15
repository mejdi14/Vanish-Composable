package com.example.vanishcomposable.helper

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap

internal fun getDominantColor(bitmap: ImageBitmap, startX: Int, startY: Int, endX: Int, endY: Int): Int {
    val colorMap = mutableMapOf<Int, Int>()
    val androidBitmap = bitmap.asAndroidBitmap()

    for (x in startX..endX) {
        for (y in startY..endY) {
            val pixel = androidBitmap.getPixel(x, y)
            colorMap[pixel] = (colorMap[pixel] ?: 0) + 1
        }
    }

    return colorMap.maxByOrNull { it.value }?.key ?: androidBitmap.getPixel(startX, startY)
}