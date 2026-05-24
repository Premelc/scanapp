package com.domelabs.scanapp.core.persistence.database

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Seeds the [UnspecifiedCollection] row on first database creation so it always
 * exists with a stable, well-known id before any item is inserted.
 */
@OptIn(ExperimentalTime::class)
internal object UnspecifiedCollectionSeedCallback : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        super.onCreate(connection)
        seedUnspecified(connection)
    }

    override fun onOpen(connection: SQLiteConnection) {
        super.onOpen(connection)
        seedUnspecified(connection)
    }

    private fun seedUnspecified(connection: SQLiteConnection) {
        val now = Clock.System.now().toEpochMilliseconds()
        connection.execSQL(
            """
            INSERT OR IGNORE INTO collection (id, name, colorHex, createdAtEpochMillis, updatedAtEpochMillis)
            VALUES (${UnspecifiedCollection.ID}, '${UnspecifiedCollection.NAME}', '${UnspecifiedCollection.COLOR_HEX}', $now, $now)
            """.trimIndent()
        )
    }
}
