package com.domelabs.scanapp.core.persistence.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.domelabs.scanapp.core.persistence.Cached

@Dao
interface ScannedItemDao : Cached {
    @Query("DELETE FROM scanned_item")
    override suspend fun clear()
}
