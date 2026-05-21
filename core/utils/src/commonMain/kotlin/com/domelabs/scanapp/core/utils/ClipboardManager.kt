package com.domelabs.scanapp.core.utils

import androidx.compose.runtime.Composable

@Composable
expect fun rememberClipboardManager(): (String) -> Unit
