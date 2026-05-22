package com.domelabs.scanapp.feature.settings.impl.data

import com.domelabs.scanapp.core.persistence.datastore.DataStoreSource
import com.domelabs.scanapp.feature.settings.impl.SettingsRepository
import com.domelabs.scanapp.feature.settings.impl.SettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class SettingsRepositoryImpl(
    private val dataStoreSource: DataStoreSource,
) : SettingsRepository {

    override val settingsFlow: Flow<SettingsState> = combine(
        dataStoreSource.getBooleanValueFlow(ScanFeedbackPreferenceKeys.soundEnabled, true),
        dataStoreSource.getBooleanValueFlow(ScanFeedbackPreferenceKeys.vibrationEnabled, true),
    ) { soundEnabled, vibrationEnabled ->
        SettingsState(
            soundEnabled = soundEnabled ?: true,
            vibrationEnabled = vibrationEnabled ?: true,
        )
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        dataStoreSource.setBooleanValueFlow(
            key = ScanFeedbackPreferenceKeys.soundEnabled,
            value = enabled,
        )
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStoreSource.setBooleanValueFlow(
            key = ScanFeedbackPreferenceKeys.vibrationEnabled,
            value = enabled,
        )
    }
}
