package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistoryItem

data class ScanDetailsViewState(
    val showShareSheet: Boolean = false,
    val historyItem: ScanHistoryItem? = null,
)