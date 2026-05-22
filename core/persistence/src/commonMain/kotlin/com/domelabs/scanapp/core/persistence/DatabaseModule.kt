package com.domelabs.scanapp.core.persistence

import com.domelabs.scanapp.core.persistence.database.ScanAppDatabase
import com.domelabs.scanapp.core.persistence.database.dao.ScanHistoryDao
import com.domelabs.scanapp.core.persistence.database.dao.ScannedItemDao
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single<ScannedItemDao> { get<ScanAppDatabase>().scannedItemDao() } bind Cached::class
    single<ScanHistoryDao> { get<ScanAppDatabase>().scanHistoryDao() }
}
