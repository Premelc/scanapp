package com.domelabs.scanapp.core.ads

/**
 * Initializes the Google Mobile Ads SDK. Call once at app startup.
 *
 * @param platformContext Android [android.content.Context] (typically the Application instance).
 *        Ignored on iOS and JVM.
 */
expect fun initializeAds(platformContext: Any? = null)
