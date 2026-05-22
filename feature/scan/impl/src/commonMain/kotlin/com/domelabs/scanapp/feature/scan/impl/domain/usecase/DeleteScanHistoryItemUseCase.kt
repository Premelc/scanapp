package com.domelabs.scanapp.feature.scan.impl.domain.usecase

import com.domelabs.scanapp.feature.scan.impl.domain.repository.ScanHistoryRepository

class DeleteScanHistoryItemUseCase(
    private val repository: ScanHistoryRepository,
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteHistoryItem(id)
    }
}
