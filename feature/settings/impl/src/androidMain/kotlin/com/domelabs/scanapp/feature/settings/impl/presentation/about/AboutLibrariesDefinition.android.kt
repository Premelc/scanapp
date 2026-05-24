package com.domelabs.scanapp.feature.settings.impl.presentation.about

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal actual suspend fun readAboutLibrariesDefinition(platformContext: Any): String {
    val context = (platformContext as Context).applicationContext
    val resourceId = context.resources.getIdentifier(
        "aboutlibraries",
        "raw",
        context.packageName,
    )
    require(resourceId != 0) {
        "aboutlibraries.json not found in app res/raw. Run :androidApp:exportLibraryDefinitions."
    }
    return withContext(Dispatchers.IO) {
        context.resources.openRawResource(resourceId).use { input ->
            input.readBytes().decodeToString()
        }
    }
}
