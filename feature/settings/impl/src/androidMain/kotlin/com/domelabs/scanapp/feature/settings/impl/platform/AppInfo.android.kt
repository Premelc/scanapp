package com.domelabs.scanapp.feature.settings.impl.platform

import android.content.Context
import android.content.pm.PackageManager

private class AndroidAppInfo(
    context: Context,
) : AppInfo {
    override val versionName: String = run {
        val packageManager = context.packageManager
        val packageName = context.packageName
        @Suppress("DEPRECATION")
        val packageInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
        packageInfo.versionName ?: "Unknown"
    }
}

internal actual fun createAppInfo(platformContext: Any): AppInfo {
    val context = platformContext as Context
    return AndroidAppInfo(context.applicationContext)
}
