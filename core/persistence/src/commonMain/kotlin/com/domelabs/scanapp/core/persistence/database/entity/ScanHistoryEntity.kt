package com.domelabs.scanapp.core.persistence.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestampEpochMillis: Long,
    val rawValue: String,
    val codeKind: String,
    val codeFormat: String,
    val source: String,
)
