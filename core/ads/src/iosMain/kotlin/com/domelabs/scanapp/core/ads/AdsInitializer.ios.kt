package com.domelabs.scanapp.core.ads

import cocoapods.Google_Mobile_Ads_SDK.GADMobileAds

actual fun initializeAds(platformContext: Any?) {
    GADMobileAds.sharedInstance().startWithCompletionHandler(null)
}
