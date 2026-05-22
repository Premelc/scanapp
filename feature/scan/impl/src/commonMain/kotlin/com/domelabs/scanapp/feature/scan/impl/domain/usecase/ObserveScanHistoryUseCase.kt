package com.domelabs.scanapp.feature.scan.impl.domain.usecase

import com.domelabs.scanapp.feature.scan.impl.domain.repository.ScanHistoryRepository

class ObserveScanHistoryUseCase(
    private val repository: ScanHistoryRepository,
) {
    operator fun invoke() = repository.observeHistory()
}
