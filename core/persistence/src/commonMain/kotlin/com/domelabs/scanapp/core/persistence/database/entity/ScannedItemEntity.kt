package com.domelabs.scanapp.core.persistence.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scanned_item",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [Index("collectionId")],
)
data class ScannedItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val collectionId: Long,
    val timestampEpochMillis: Long,
    val rawValue: String,
    val codeKind: String,
    val codeFormat: String,
    val source: String,
    val customName: String? = null,
)
