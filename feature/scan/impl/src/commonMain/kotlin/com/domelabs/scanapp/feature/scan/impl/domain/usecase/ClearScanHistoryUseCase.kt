package com.domelabs.scanapp.feature.scan.impl.domain.usecase

import com.domelabs.scanapp.feature.scan.impl.domain.repository.ScanHistoryRepository

class ClearScanHistoryUseCase(
    private val repository: ScanHistoryRepository,
) {
    suspend operator fun invoke() {
        repository.clearHistory()
    }
}
