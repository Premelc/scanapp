package com.domelabs.scanapp.core.utils

import android.content.ClipData
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.toClipEntry
import androidx.fragment.app.FragmentActivity

actual typealias PlatformActivity = FragmentActivity

@Composable
actual fun platformActivity(): PlatformActivity {
    return LocalActivity.current as FragmentActivity
}

actual fun String.toClipEntry() = ClipData.newPlainText(this, this).toClipEntry()
