package com.domelabs.scanapp.di

import com.domelabs.scanapp.core.persistence.database.ScanAppDatabase
import com.domelabs.scanapp.core.persistence.database.getDatabaseBuilder
import com.domelabs.scanapp.core.persistence.database.getRoomDatabase
import com.domelabs.scanapp.core.persistence.databaseModule
import com.domelabs.scanapp.feature.scan.api.scanFeatureModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin(platformContext: Any) {
    startKoin {
        modules(
            module {
                single<ScanAppDatabase> {
                    getRoomDatabase(
                        builder = getDatabaseBuilder(platformContext)
                    )
                }
            },
            databaseModule,
            scanFeatureModule,
        )
    }
}
