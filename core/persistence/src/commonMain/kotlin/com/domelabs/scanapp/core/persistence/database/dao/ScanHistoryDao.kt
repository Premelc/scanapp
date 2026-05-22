package com.domelabs.scanapp.core.persistence.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.domelabs.scanapp.core.persistence.database.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestampEpochMillis DESC LIMIT 50")
    fun observeLatest(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE rawValue = :rawValue ORDER BY timestampEpochMillis DESC LIMIT 1")
    suspend fun getLatestByRawValue(rawValue: String): ScanHistoryEntity?

    @Insert
    suspend fun insert(item: ScanHistoryEntity): Long

    @Query(
        """
        DELETE FROM scan_history 
        WHERE id NOT IN (
            SELECT id FROM scan_history 
            ORDER BY timestampEpochMillis DESC 
            LIMIT :keep
        )
        """
    )
    suspend fun trimToLimit(keep: Int)

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM scan_history")
    suspend fun clear()
}
