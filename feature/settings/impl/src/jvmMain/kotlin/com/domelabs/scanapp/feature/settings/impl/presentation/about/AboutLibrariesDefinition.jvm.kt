package com.domelabs.scanapp.feature.settings.impl.presentation.about

import scan_app.feature.settings.impl.generated.resources.Res

internal actual suspend fun readAboutLibrariesDefinition(platformContext: Any): String {
    return Res.readBytes("files/aboutlibraries.json").decodeToString()
}
