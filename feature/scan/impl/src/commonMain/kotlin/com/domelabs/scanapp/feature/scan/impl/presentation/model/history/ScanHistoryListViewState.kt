package com.domelabs.scanapp.feature.scan.impl.presentation.model.history

data class ScanHistoryListViewState(
    val historyItems: List<ScanHistoryItemUi> = emptyList(),
)