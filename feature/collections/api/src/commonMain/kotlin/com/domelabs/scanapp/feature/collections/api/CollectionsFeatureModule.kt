package com.domelabs.scanapp.feature.collections.api

import com.domelabs.scanapp.feature.collections.impl.collectionsFeatureImplModule
import org.koin.dsl.module

val collectionsFeatureModule = module {
    includes(collectionsFeatureImplModule)
}
