package com.domelabs.scanapp.feature.settings.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.core.scan.ScanFeedbackPlayer
import com.domelabs.scanapp.feature.settings.impl.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val feedbackPlayer: ScanFeedbackPlayer,
) : ViewModel() {
    val viewState: StateFlow<SettingsViewState> = settingsRepository.settingsFlow
        .map { settings ->
            SettingsViewState(
                soundEnabled = settings.soundEnabled,
                vibrationEnabled = settings.vibrationEnabled,
                capabilities = feedbackPlayer.capabilities,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
            initialValue = SettingsViewState(capabilities = feedbackPlayer.capabilities),
        )

    fun onInteraction(interaction: SettingsInteraction) {
        when (interaction) {
            SettingsInteraction.NavigateBack -> {
                viewModelScope.launch {
                    NavigationDispatcher.back()
                }
            }

            is SettingsInteraction.ToggleSound -> {
                viewModelScope.launch {
                    settingsRepository.setSoundEnabled(interaction.enabled)
                }
            }

            is SettingsInteraction.ToggleVibration -> {
                viewModelScope.launch {
                    settingsRepository.setVibrationEnabled(interaction.enabled)
                }
            }
        }
    }
}
