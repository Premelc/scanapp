package com.domelabs.scanapp.feature.scan.impl

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val scanFeatureImplModule = module {
    viewModelOf(::ScanViewModel)
}
