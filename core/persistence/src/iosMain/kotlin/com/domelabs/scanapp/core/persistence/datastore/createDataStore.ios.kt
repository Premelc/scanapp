@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.domelabs.scanapp.core.persistence.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun createDataStore(context: Any): DataStore<Preferences> {
    return createDataStore(
        producePath = {
            val directory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )!!
            "${directory.path}/$DATA_STORE_FILE_NAME"
        },
    )
}
