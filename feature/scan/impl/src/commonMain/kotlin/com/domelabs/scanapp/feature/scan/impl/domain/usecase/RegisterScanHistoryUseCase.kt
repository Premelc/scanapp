package com.domelabs.scanapp.feature.scan.impl.domain.usecase

import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistorySource
import com.domelabs.scanapp.feature.scan.impl.domain.repository.ScanHistoryRepository

class RegisterScanHistoryUseCase(
    private val repository: ScanHistoryRepository,
) {
    suspend operator fun invoke(
        rawValue: String,
        codeKind: String,
        codeFormat: String,
        source: ScanHistorySource,
    ) {
        repository.registerScan(
            rawValue = rawValue,
            codeKind = codeKind,
            codeFormat = codeFormat,
            source = source,
        )
    }
}
