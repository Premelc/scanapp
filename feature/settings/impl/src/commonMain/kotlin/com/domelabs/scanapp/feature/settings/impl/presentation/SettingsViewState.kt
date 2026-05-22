package com.domelabs.scanapp.feature.settings.impl.presentation

import com.domelabs.scanapp.core.scan.ScanFeedbackCapabilities

data class SettingsViewState(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val capabilities: ScanFeedbackCapabilities = ScanFeedbackCapabilities(),
)
