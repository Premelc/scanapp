package com.domelabs.scanapp

import android.app.Application
import com.domelabs.scanapp.core.ads.initializeAds
import com.domelabs.scanapp.di.initKoin

class ScanApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
        initializeAds(this)
    }
}
