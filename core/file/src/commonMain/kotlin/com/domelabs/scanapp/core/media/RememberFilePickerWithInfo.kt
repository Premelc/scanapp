package com.domelabs.scanapp.core.media

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFilePickerWithInfo(
    onPicked: (PickedFileInfo) -> Unit
): () -> Unit

data class PickedFileInfo(
    val path: String,
    val name: String,
    val size: Long,
)
