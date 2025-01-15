package com.example.vanishcomposable.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AnimationStateHolder {
    var vanished by mutableStateOf(false)
    var isAnimating by mutableStateOf(false)
}