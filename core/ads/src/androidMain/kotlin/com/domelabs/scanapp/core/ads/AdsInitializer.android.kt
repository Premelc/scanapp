package com.domelabs.scanapp.core.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds

actual fun initializeAds(platformContext: Any?) {
    val context = platformContext as? Context ?: return
    MobileAds.initialize(context)
}
