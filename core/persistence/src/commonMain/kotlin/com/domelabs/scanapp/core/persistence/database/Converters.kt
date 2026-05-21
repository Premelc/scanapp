package com.domelabs.scanapp.core.persistence.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

internal class Converters {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String =
        json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        json.decodeFromString(value)
}
