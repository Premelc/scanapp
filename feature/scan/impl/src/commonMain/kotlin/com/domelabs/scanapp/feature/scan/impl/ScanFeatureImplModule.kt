package com.domelabs.scanapp.feature.scan.impl

import com.domelabs.scanapp.feature.scan.impl.domain.usecase.PlayScanFeedbackIfEnabledUseCase
import com.domelabs.scanapp.feature.scan.impl.presentation.model.details.ItemDetailsViewModel
import com.domelabs.scanapp.feature.scan.impl.presentation.model.history.ScanHistoryScreenViewModel
import com.domelabs.scanapp.feature.scan.impl.presentation.model.scan.ScanViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val scanFeatureImplModule = module {
    factoryOf(::PlayScanFeedbackIfEnabledUseCase)
    viewModelOf(::ScanViewModel)
    viewModelOf(::ItemDetailsViewModel)
    viewModelOf(::ScanHistoryScreenViewModel)
}
