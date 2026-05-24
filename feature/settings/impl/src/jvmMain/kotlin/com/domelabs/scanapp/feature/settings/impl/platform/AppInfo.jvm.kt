package com.domelabs.scanapp.feature.settings.impl.platform

private class JvmAppInfo : AppInfo {
    override val versionName: String = "1.0"
}

internal actual fun createAppInfo(platformContext: Any): AppInfo = JvmAppInfo()
