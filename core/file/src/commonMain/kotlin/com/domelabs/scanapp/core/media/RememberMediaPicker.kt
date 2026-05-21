package com.domelabs.scanapp.core.media

import androidx.compose.runtime.Composable

@Composable
expect fun rememberMediaPicker(
    onMediaPicked: (List<MediaItem>) -> Unit
): () -> Unit