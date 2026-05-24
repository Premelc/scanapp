package com.domelabs.scanapp.feature.collections.impl.data.repository

import com.domelabs.scanapp.core.persistence.database.UnspecifiedCollection
import com.domelabs.scanapp.core.persistence.database.entity.CollectionEntity

internal fun CollectionEntity.isUnspecifiedCollection(): Boolean =
    name.equals(UnspecifiedCollection.NAME, ignoreCase = true)
