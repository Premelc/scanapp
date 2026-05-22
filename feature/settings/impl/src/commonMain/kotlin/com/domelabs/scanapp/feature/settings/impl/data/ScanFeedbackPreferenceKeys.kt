package com.domelabs.scanapp.feature.settings.impl.data

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object ScanFeedbackPreferenceKeys {
    val soundEnabled = booleanPreferencesKey("scan_feedback_sound_enabled")
    val vibrationEnabled = booleanPreferencesKey("scan_feedback_vibration_enabled")
}
