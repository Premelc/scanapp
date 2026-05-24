package com.domelabs.scanapp.core.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun timeNow() = Clock.System.now().toEpochMilliseconds()

@OptIn(ExperimentalTime::class)
fun formatTimestamp(timestampMillis: Long): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val timestamp = Instant.fromEpochMilliseconds(timestampMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val time = "${timestamp.hour.toString().padStart(2, '0')}:${timestamp.minute.toString().padStart(2, '0')}"

    return when (timestamp.date) {
        now.date -> time
        now.date.minus(1, DateTimeUnit.DAY) -> "yesterday $time"
        else -> "${timestamp.day.toString().padStart(2, '0')}." +
            "${timestamp.month.number.toString().padStart(2, '0')}.${timestamp.year}"
    }
}
