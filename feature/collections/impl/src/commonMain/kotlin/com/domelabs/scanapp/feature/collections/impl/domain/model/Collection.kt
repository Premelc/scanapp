package com.domelabs.scanapp.feature.collections.impl.domain.model

/**
 * A user-defined (or system-managed) box for grouping [ScannedItem]s.
 *
 * @property isSystemManaged true for the special "Unspecified" collection that
 *           cannot be renamed, recolored, or deleted.
 */
data class Collection(
    val id: Long,
    val name: String,
    val colorHex: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val isSystemManaged: Boolean,
)

/**
 * Aggregate view of a [Collection] augmented with display metadata used by the
 * collections list (item count + timestamp of the most recent item, or null
 * when the collection is empty).
 */
data class CollectionSummary(
    val collection: Collection,
    val itemCount: Int,
    val lastItemTimestampEpochMillis: Long?,
)
