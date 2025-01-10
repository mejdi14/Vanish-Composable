package com.example.vanishcomposable.controller

interface AnimationController {
    fun triggerVanish(onFinish: () -> Unit = {})
    fun reset()
}