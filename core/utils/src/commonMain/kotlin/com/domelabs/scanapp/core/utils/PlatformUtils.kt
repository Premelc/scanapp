package com.domelabs.scanapp.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipEntry

expect class PlatformActivity

@Composable
expect fun platformActivity(): PlatformActivity

expect fun String.toClipEntry(): ClipEntry
