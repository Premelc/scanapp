package com.domelabs.scanapp.feature.settings.impl.presentation

sealed interface SettingsInteraction {
    data object NavigateBack : SettingsInteraction
    data class ToggleSound(val enabled: Boolean) : SettingsInteraction
    data class ToggleVibration(val enabled: Boolean) : SettingsInteraction
}
