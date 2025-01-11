package com.example.vanishcomposable

import androidx.annotation.DrawableRes
import com.example.vanishcomposable.Animation.AnimationEffect

data class VanishOptions(
    val pixelSize: Float = 4f,
    val pixelSpacing: Float = 2f,
    val pixelVelocity: Int = 1000,
    val animationDuration: Int = 1200,
    val triggerFinishAt: Float = 1f,
)