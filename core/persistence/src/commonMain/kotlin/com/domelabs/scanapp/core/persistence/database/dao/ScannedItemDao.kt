package com.domelabs.scanapp.core.persistence.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.domelabs.scanapp.core.persistence.database.entity.ScannedItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedItemDao {

    @Query("SELECT * FROM scanned_item ORDER BY timestampEpochMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<ScannedItemEntity>>

    @Query(
        "SELECT * FROM scanned_item WHERE collectionId = :collectionId ORDER BY timestampEpochMillis DESC"
    )
    fun observeByCollection(collectionId: Long): Flow<List<ScannedItemEntity>>

    @Query(
        """
        SELECT * FROM scanned_item
        WHERE collectionId = :collectionId
          AND (rawValue LIKE '%' || :query || '%' COLLATE NOCASE
               OR (customName IS NOT NULL AND customName LIKE '%' || :query || '%' COLLATE NOCASE))
        ORDER BY timestampEpochMillis DESC
        """
    )
    fun searchInCollection(collectionId: Long, query: String): Flow<List<ScannedItemEntity>>

    @Query("SELECT * FROM scanned_item WHERE id = :id")
    suspend fun getById(id: Long): ScannedItemEntity?

    @Query("SELECT * FROM scanned_item WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<ScannedItemEntity?>

    @Query(
        "SELECT * FROM scanned_item WHERE rawValue = :rawValue ORDER BY timestampEpochMillis DESC LIMIT 1"
    )
    suspend fun getLatestByRawValue(rawValue: String): ScannedItemEntity?

    @Insert
    suspend fun insert(item: ScannedItemEntity): Long

    @Update
    suspend fun update(item: ScannedItemEntity)

    @Query("UPDATE scanned_item SET customName = :customName WHERE id = :id")
    suspend fun setCustomName(id: Long, customName: String?)

    @Query("UPDATE scanned_item SET collectionId = :targetCollectionId WHERE id = :id")
    suspend fun setCollection(id: Long, targetCollectionId: Long)

    @Query(
        "UPDATE scanned_item SET collectionId = :targetCollectionId WHERE collectionId = :sourceCollectionId"
    )
    suspend fun reassignAllInCollection(sourceCollectionId: Long, targetCollectionId: Long)

    @Query("DELETE FROM scanned_item WHERE collectionId = :collectionId")
    suspend fun deleteAllInCollection(collectionId: Long)

    @Query("DELETE FROM scanned_item WHERE id = :id")
    suspend fun deleteById(id: Long)
}
