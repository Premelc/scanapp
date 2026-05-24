package com.domelabs.scanapp.core.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
actual fun TopBannerAd(modifier: Modifier) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(AdConfig.bannerHeightDp.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.FULL_BANNER)
                adUnitId = AdConfig.androidBannerAdUnitIdTest
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
