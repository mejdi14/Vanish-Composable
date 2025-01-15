package com.example.vanishedcomposable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.vanishcomposable.Animation.AnimationEffect
import com.example.vanishcomposable.VanishOptions
import com.example.vanishcomposable.composable.VanishComposable
import com.example.vanishcomposable.controller.AnimationController

@Composable
fun OneImageDemo(
) {
    var controller: AnimationController? by remember { mutableStateOf(null) }
    VanishComposable(
        vanishOptions = VanishOptions(
            pixelSize = 50f,  // Larger size for more visible squares
            pixelSpacing = 0f,  // No spacing between squares
            animationDuration = 5000,  // Customize as needed
            triggerFinishAt = 1f,
            pixelVelocity = 100
        ),
        effect = AnimationEffect.MOSAIC,
        onControllerReady = {
            controller = it
        }) {
        ImageContent(controller)
    }
}

@Composable
fun ImageContent(controller: AnimationController?) {
    Image(
        painterResource(R.drawable.demarco),
        contentScale = ContentScale.Crop,
        contentDescription = "",
        modifier = Modifier.fillMaxSize()
            .clickable{
                controller?.triggerVanish()
            }
    )
}