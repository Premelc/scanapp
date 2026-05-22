package com.domelabs.scanapp.feature.scan.impl

import com.domelabs.scanapp.core.persistence.database.dao.ScanHistoryDao
import com.domelabs.scanapp.feature.scan.impl.data.repository.ScanHistoryRepositoryImpl
import com.domelabs.scanapp.feature.scan.impl.data.source.ScanHistoryLocalSource
import com.domelabs.scanapp.feature.scan.impl.data.source.ScanHistoryLocalSourceImpl
import com.domelabs.scanapp.feature.scan.impl.domain.repository.ScanHistoryRepository
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.ClearScanHistoryUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.DeleteScanHistoryItemUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.ObserveScanHistoryUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.PlayScanFeedbackIfEnabledUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.RegisterScanHistoryUseCase
import com.domelabs.scanapp.feature.scan.impl.presentation.model.ScanViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val scanFeatureImplModule = module {
    single<ScanHistoryLocalSource> { ScanHistoryLocalSourceImpl(get<ScanHistoryDao>()) }
    single<ScanHistoryRepository> { ScanHistoryRepositoryImpl(get()) }
    factoryOf(::ObserveScanHistoryUseCase)
    factoryOf(::RegisterScanHistoryUseCase)
    factoryOf(::DeleteScanHistoryItemUseCase)
    factoryOf(::ClearScanHistoryUseCase)
    factoryOf(::PlayScanFeedbackIfEnabledUseCase)
    viewModelOf(::ScanViewModel)
}
