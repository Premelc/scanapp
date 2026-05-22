package com.domelabs.scanapp.di

import com.domelabs.scanapp.core.persistence.database.ScanAppDatabase
import com.domelabs.scanapp.core.persistence.database.getDatabaseBuilder
import com.domelabs.scanapp.core.persistence.database.getRoomDatabase
import com.domelabs.scanapp.core.persistence.datastore.DataStoreSource
import com.domelabs.scanapp.core.persistence.datastore.createDataStore
import com.domelabs.scanapp.core.persistence.databaseModule
import com.domelabs.scanapp.feature.scan.api.scanFeatureModule
import com.domelabs.scanapp.feature.settings.api.settingsFeatureModule
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initKoin(platformContext: Any) {
    startKoin {
        modules(
            module {
                single(named("platformContext")) { platformContext }
                single<ScanAppDatabase> {
                    getRoomDatabase(
                        builder = getDatabaseBuilder(platformContext)
                    )
                }
                single<DataStoreSource> {
                    DataStoreSource(createDataStore(platformContext))
                }
            },
            databaseModule,
            scanFeatureModule,
            settingsFeatureModule,
        )
    }
}
