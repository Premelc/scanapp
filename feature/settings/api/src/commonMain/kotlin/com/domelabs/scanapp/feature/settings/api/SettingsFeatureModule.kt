package com.domelabs.scanapp.feature.settings.api

import com.domelabs.scanapp.feature.settings.impl.settingsFeatureImplModule
import org.koin.dsl.module

val settingsFeatureModule = module {
    includes(settingsFeatureImplModule)
}
