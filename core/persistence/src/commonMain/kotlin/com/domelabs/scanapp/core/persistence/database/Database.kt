package com.domelabs.scanapp.core.persistence.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.domelabs.scanapp.core.persistence.database.dao.ScannedItemDao
import com.domelabs.scanapp.core.persistence.database.entity.ScannedItemEntity
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [
        ScannedItemEntity::class,
    ],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(Converters::class)
abstract class ScanAppDatabase : RoomDatabase() {
    abstract fun scannedItemDao(): ScannedItemDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "-Xexpect-actual-classes")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<ScanAppDatabase>

fun getRoomDatabase(
    builder: RoomDatabase.Builder<ScanAppDatabase>,
): ScanAppDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
}
