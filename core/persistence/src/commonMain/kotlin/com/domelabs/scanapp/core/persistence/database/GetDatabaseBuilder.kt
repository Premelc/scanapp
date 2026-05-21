package com.domelabs.scanapp.core.persistence.database

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(ctx: Any): RoomDatabase.Builder<ScanAppDatabase>

