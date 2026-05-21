package com.domelabs.scanapp.core.media

import androidx.compose.runtime.Composable

@Composable
expect fun rememberSshKeyContentPicker(onPicked: (String) -> Unit): () -> Unit
