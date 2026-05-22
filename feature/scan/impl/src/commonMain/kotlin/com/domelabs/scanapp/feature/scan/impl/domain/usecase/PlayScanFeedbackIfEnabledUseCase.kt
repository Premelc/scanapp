package com.domelabs.scanapp.feature.scan.impl.domain.usecase

import com.domelabs.scanapp.core.scan.ScanFeedbackOptions
import com.domelabs.scanapp.core.scan.ScanFeedbackPlayer
import com.domelabs.scanapp.feature.settings.impl.SettingsRepository
import kotlinx.coroutines.flow.first

class PlayScanFeedbackIfEnabledUseCase(
    private val settingsRepository: SettingsRepository,
    private val feedbackPlayer: ScanFeedbackPlayer,
) {
    suspend operator fun invoke() {
        val settings = settingsRepository.settingsFlow.first()
        if (settings.soundEnabled || settings.vibrationEnabled) {
            feedbackPlayer.play(
                ScanFeedbackOptions(
                    soundEnabled = settings.soundEnabled,
                    vibrationEnabled = settings.vibrationEnabled,
                )
            )
        }
    }
}
