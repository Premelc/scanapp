package com.domelabs.scanapp.feature.settings.impl

import kotlinx.coroutines.flow.Flow

data class SettingsState(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
)

interface SettingsRepository {
    val settingsFlow: Flow<SettingsState>

    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setVibrationEnabled(enabled: Boolean)
}
