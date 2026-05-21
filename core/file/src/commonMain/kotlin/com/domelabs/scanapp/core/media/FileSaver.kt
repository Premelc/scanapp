package com.domelabs.scanapp.core.media

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFileSaver(): (content: String, defaultFilename: String, onResult: (Boolean) -> Unit) -> Unit
