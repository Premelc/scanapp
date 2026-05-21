package com.domelabs.scanapp.core.persistence.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(ctx: Any): RoomDatabase.Builder<ScanAppDatabase> {
    val appContext = (ctx as Context).applicationContext
    val dbFile = appContext.getDatabasePath("SwiSSHDatabase.db")
    return Room.databaseBuilder<ScanAppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}