package com.domelabs.scanapp.core.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.Google_Mobile_Ads_SDK.GADAdSizeBanner
import cocoapods.Google_Mobile_Ads_SDK.GADBannerView
import cocoapods.Google_Mobile_Ads_SDK.GADRequest
import platform.UIKit.UIApplication

@Composable
actual fun TopBannerAd(modifier: Modifier) {
    UIKitView(
        factory = {
            GADBannerView().apply {
                adUnitID = AdConfig.iosBannerAdUnitIdTest
                adSize = GADAdSizeBanner
                rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                loadRequest(GADRequest())
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(AdConfig.bannerHeightDp.dp),
        update = {},
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true,
        ),
    )
}
