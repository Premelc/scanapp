@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.domelabs.scanapp.core.persistence.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun getDatabaseBuilder(ctx: Any): RoomDatabase.Builder<ScanAppDatabase> {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )!!
    return Room.databaseBuilder<ScanAppDatabase>(
        name = "${directory.path}/ScanAppDatabase.db",
    )
}
