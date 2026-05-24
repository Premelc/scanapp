package com.domelabs.scanapp

import androidx.compose.ui.window.ComposeUIViewController
import com.domelabs.scanapp.core.ads.initializeAds

fun MainViewController() = ComposeUIViewController {
    initializeAds()
    App()
}
