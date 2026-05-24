package com.domelabs.scanapp.core.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Stable
class CameraZoomState internal constructor() {
    var minZoomRatio by mutableFloatStateOf(1f)
        internal set
    var maxZoomRatio by mutableFloatStateOf(1f)
        internal set
    var zoomRatio by mutableFloatStateOf(1f)
        internal set
    var isAvailable by mutableStateOf(false)
        internal set

    internal var isUserAdjusting: Boolean = false
    internal var applyZoomRatio: (Float) -> Unit = {}

    fun setZoomRatio(ratio: Float) {
        if (!isAvailable) return
        val clamped = ratio.coerceIn(minZoomRatio, maxZoomRatio)
        isUserAdjusting = true
        zoomRatio = clamped
        applyZoomRatio(clamped)
    }

    fun onZoomAdjustFinished() {
        isUserAdjusting = false
    }

    internal fun updateFromCamera(min: Float, max: Float, ratio: Float) {
        minZoomRatio = min
        maxZoomRatio = max
        isAvailable = max > min + 0.01f
        if (!isUserAdjusting) {
            zoomRatio = ratio.coerceIn(min, max)
        }
    }

    internal fun reset() {
        minZoomRatio = 1f
        maxZoomRatio = 1f
        zoomRatio = 1f
        isAvailable = false
        isUserAdjusting = false
        applyZoomRatio = {}
    }
}

@Composable
fun rememberCameraZoomState(): CameraZoomState = remember { CameraZoomState() }
