package com.domelabs.scanapp.feature.settings.impl.platform

import platform.Foundation.NSBundle

private class IosAppInfo : AppInfo {
    override val versionName: String =
        NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
            ?: "Unknown"
}

internal actual fun createAppInfo(platformContext: Any): AppInfo = IosAppInfo()
