package com.domelabs.scanapp.di

import com.domelabs.scanapp.feature.scan.api.scanFeatureModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(scanFeatureModule)
    }
}
