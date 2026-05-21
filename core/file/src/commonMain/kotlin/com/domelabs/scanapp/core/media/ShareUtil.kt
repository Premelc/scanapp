package com.domelabs.scanapp.core.media

import androidx.compose.runtime.Composable

@Composable
expect fun rememberShareUtil(): (text: String) -> Unit
