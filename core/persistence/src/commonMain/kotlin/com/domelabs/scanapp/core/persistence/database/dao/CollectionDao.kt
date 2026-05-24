package com.domelabs.scanapp.core.persistence.database.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.domelabs.scanapp.core.persistence.database.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Query(
        """
        SELECT c.*,
               COUNT(i.id) AS itemCount,
               MAX(i.timestampEpochMillis) AS lastItemTimestampEpochMillis
        FROM collection c
        LEFT JOIN scanned_item i ON i.collectionId = c.id
        GROUP BY c.id
        ORDER BY c.name COLLATE NOCASE ASC
        """
    )
    fun observeAllWithStats(): Flow<List<CollectionWithStats>>

    @Query("SELECT * FROM collection WHERE id = :id")
    suspend fun getById(id: Long): CollectionEntity?

    @Query("SELECT * FROM collection WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<CollectionEntity?>

    @Query("SELECT * FROM collection WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun findByName(name: String): CollectionEntity?

    @Insert
    suspend fun insert(item: CollectionEntity): Long

    @Update
    suspend fun update(item: CollectionEntity)

    @Query("UPDATE collection SET updatedAtEpochMillis = :timestamp WHERE id = :id")
    suspend fun touchUpdatedAt(id: Long, timestamp: Long)

    @Query("DELETE FROM collection WHERE id = :id")
    suspend fun deleteById(id: Long)
}

/**
 * Projection used by the collections list: an embedded [CollectionEntity] plus
 * an aggregated item count and the timestamp of the most recent item in the
 * collection (null when the collection is empty).
 */
data class CollectionWithStats(
    @Embedded val collection: CollectionEntity,
    val itemCount: Int,
    val lastItemTimestampEpochMillis: Long?,
)
