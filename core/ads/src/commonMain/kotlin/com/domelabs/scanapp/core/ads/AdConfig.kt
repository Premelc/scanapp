package com.domelabs.scanapp.core.ads

/**
 * AdMob configuration. Replace test IDs with production values before release.
 *
 * @see <a href="https://developers.google.com/admob/android/test-ads">AdMob test ads</a>
 */
object AdConfig {
    /** Google-provided Android test app ID. */
    const val androidAppId: String = "ca-app-pub-9680718533779299~5709129423"

    /** Google-provided iOS test app ID. */
    const val iosAppId: String = "ca-app-pub-9680718533779299~7102102141"

    /** Google-provided Android adaptive banner test unit. */
    const val androidBannerAdUnitIdTest: String = "ca-app-pub-3940256099942544/6300978111"
    const val androidBannerAdUnitId: String = "ca-app-pub-9680718533779299/3791515317"

    /** Google-provided iOS banner test unit. */
    const val iosBannerAdUnitIdTest: String = "ca-app-pub-3940256099942544/2934735716"
    const val iosBannerAdUnitId: String = "ca-app-pub-9680718533779299/6413809223"

    /** Standard AdMob banner height in dp. */
    const val bannerHeightDp: Int = 60
}
