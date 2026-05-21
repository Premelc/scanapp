package com.domelabs.scanapp.core.persistence.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanned_item")
data class ScannedItemEntity(
    @PrimaryKey
    val id: String,
    val content: String,
)
