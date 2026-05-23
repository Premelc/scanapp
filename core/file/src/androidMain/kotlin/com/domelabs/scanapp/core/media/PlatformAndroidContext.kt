package com.domelabs.scanapp.core.media

import android.content.Context
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatformTools

internal fun platformAndroidContext(): Context {
    return KoinPlatformTools.defaultContext().get().get<Any>(named("platformContext")) as Context
}
