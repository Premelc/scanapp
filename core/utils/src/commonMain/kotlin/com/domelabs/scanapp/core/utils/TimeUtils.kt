package com.domelabs.scanapp.core.utils

import androidx.compose.runtime.Composable
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun timeNow() = Clock.System.now().toEpochMilliseconds()

@OptIn(ExperimentalTime::class)
@Composable
fun formatTimestamp(timestampMillis: Long): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val timestamp = Instant.fromEpochMilliseconds(timestampMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    return when (timestamp.date) {
        now.date -> stringResource(
            Res.string.timestamp_exact,
            timestamp.hour.toString().padStart(2, '0'),
            timestamp.minute.toString().padStart(2, '0')
        )

        now.date.minus(1, DateTimeUnit.DAY) -> {
            stringResource(
                Res.string.timestamp_yesterday,
                timestamp.hour.toString().padStart(2, '0'),
                timestamp.minute.toString().padStart(2, '0')
            )
        }

        else -> stringResource(
            Res.string.timestamp_older,
            timestamp.day.toString().padStart(2, '0'),
            timestamp.month.number.toString().padStart(2, '0'),
            timestamp.year
        )
    }
}
