package com.domelabs.scanapp.feature.collections.impl.data.mapper

import com.domelabs.scanapp.core.persistence.database.UnspecifiedCollection
import com.domelabs.scanapp.core.persistence.database.dao.CollectionWithStats
import com.domelabs.scanapp.core.persistence.database.entity.CollectionEntity
import com.domelabs.scanapp.core.persistence.database.entity.ScannedItemEntity
import com.domelabs.scanapp.core.scan.CodeFormat
import com.domelabs.scanapp.core.scan.CodeKind
import com.domelabs.scanapp.feature.collections.impl.domain.model.Collection
import com.domelabs.scanapp.feature.collections.impl.domain.model.CollectionSummary
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItemSource

internal fun CollectionEntity.toDomain(): Collection = Collection(
    id = id,
    name = name,
    colorHex = colorHex,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
    isSystemManaged = name.equals(UnspecifiedCollection.NAME, ignoreCase = true),
)

internal fun CollectionWithStats.toDomain(): CollectionSummary = CollectionSummary(
    collection = collection.toDomain(),
    itemCount = itemCount,
    lastItemTimestampEpochMillis = lastItemTimestampEpochMillis,
)

internal fun ScannedItemEntity.toDomain(): ScannedItem = ScannedItem(
    id = id,
    collectionId = collectionId,
    timestampEpochMillis = timestampEpochMillis,
    rawValue = rawValue,
    codeKind = CodeKind.valueOf(codeKind),
    codeFormat = CodeFormat.valueOf(codeFormat),
    source = source.toScannedItemSource(),
    customName = customName,
)

private fun String.toScannedItemSource(): ScannedItemSource =
    ScannedItemSource.entries.firstOrNull { it.name == this } ?: ScannedItemSource.CAMERA
