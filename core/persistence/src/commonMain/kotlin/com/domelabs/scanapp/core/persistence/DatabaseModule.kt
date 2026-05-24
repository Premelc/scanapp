package com.domelabs.scanapp.core.persistence

import com.domelabs.scanapp.core.persistence.database.ScanAppDatabase
import com.domelabs.scanapp.core.persistence.database.dao.CollectionDao
import com.domelabs.scanapp.core.persistence.database.dao.ScannedItemDao
import org.koin.dsl.module

val databaseModule = module {
    single<CollectionDao> { get<ScanAppDatabase>().collectionDao() }
    single<ScannedItemDao> { get<ScanAppDatabase>().scannedItemDao() }
}
