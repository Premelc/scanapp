package com.domelabs.scanapp.feature.scan.api

import com.domelabs.scanapp.feature.scan.impl.scanFeatureImplModule
import org.koin.dsl.module

val scanFeatureModule = module {
    includes(scanFeatureImplModule)
}
