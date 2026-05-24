package com.domelabs.scanapp.feature.settings.impl.platform

interface AppInfo {
    val versionName: String
}

internal expect fun createAppInfo(platformContext: Any): AppInfo
