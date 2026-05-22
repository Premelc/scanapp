package com.domelabs.scanapp.core.scan

data class ScanFeedbackCapabilities(
    val supportsSound: Boolean = true,
    val supportsVibration: Boolean = true,
)

data class ScanFeedbackOptions(
    val soundEnabled: Boolean,
    val vibrationEnabled: Boolean,
)

interface ScanFeedbackPlayer {
    val capabilities: ScanFeedbackCapabilities
    fun play(options: ScanFeedbackOptions)
}

expect class PlatformScanFeedbackPlayer(
    platformContext: Any,
) : ScanFeedbackPlayer
