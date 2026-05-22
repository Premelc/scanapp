package com.domelabs.scanapp.feature.settings.impl

import com.domelabs.scanapp.core.persistence.datastore.DataStoreSource
import com.domelabs.scanapp.core.scan.PlatformScanFeedbackPlayer
import com.domelabs.scanapp.core.scan.ScanFeedbackPlayer
import com.domelabs.scanapp.feature.settings.impl.data.SettingsRepositoryImpl
import com.domelabs.scanapp.feature.settings.impl.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsFeatureImplModule = module {
    single<ScanFeedbackPlayer> { PlatformScanFeedbackPlayer(get(named("platformContext"))) }
    single<SettingsRepository> { SettingsRepositoryImpl(get<DataStoreSource>()) }
    viewModelOf(::SettingsViewModel)
}
