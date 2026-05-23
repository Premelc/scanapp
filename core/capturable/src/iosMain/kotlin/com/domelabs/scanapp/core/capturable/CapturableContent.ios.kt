package com.domelabs.scanapp.core.capturable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun CapturableContent(
    content: @Composable () -> Unit,
    captureButton: @Composable ((onClick: () -> Unit) -> Unit),
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier) {
            content()
        }

        captureButton {
            scope.launch {
                // TODO(IOS MISSING)
            }
        }
    }
}