package com.domelabs.scanapp.core.scan

import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate

actual class PlatformScanFeedbackPlayer actual constructor(
    platformContext: Any,
) : ScanFeedbackPlayer {
    override val capabilities: ScanFeedbackCapabilities =
        ScanFeedbackCapabilities(
            supportsSound = true,
            supportsVibration = true,
        )

    override fun play(options: ScanFeedbackOptions) {
        if (options.soundEnabled) {
            // Placeholder iOS system click.
            AudioServicesPlaySystemSound(1104u)
        }
        if (options.vibrationEnabled) {
            AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
        }
    }
}
