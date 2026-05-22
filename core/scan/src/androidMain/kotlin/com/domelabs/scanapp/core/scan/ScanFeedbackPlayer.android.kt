package com.domelabs.scanapp.core.scan

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

actual class PlatformScanFeedbackPlayer actual constructor(
    platformContext: Any,
) : ScanFeedbackPlayer {
    private val context = platformContext as Context
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 40)

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override val capabilities: ScanFeedbackCapabilities
        get() = ScanFeedbackCapabilities(
            supportsSound = true,
            supportsVibration = vibrator?.hasVibrator() == true,
        )

    override fun play(options: ScanFeedbackOptions) {
        if (options.soundEnabled && capabilities.supportsSound) {
            // Placeholder beep for v1.
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 90)
        }
        if (options.vibrationEnabled && capabilities.supportsVibration) {
            val deviceVibrator = vibrator ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceVibrator.vibrate(VibrationEffect.createOneShot(35L, 80))
            } else {
                @Suppress("DEPRECATION")
                deviceVibrator.vibrate(35L)
            }
        }
    }
}
