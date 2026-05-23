package com.domelabs.scanapp.uiComponent.components.shadow

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LazyShadowColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    bottomShadow: Boolean = true,
    topShadow: Boolean = true,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        modifier = modifier
            .then(
                if (bottomShadow) Modifier.Companion.fadingBottom(
                    alpha = if (isSystemInDarkTheme()) 0.5f else 0.15f,
                    condition = {
                        if (reverseLayout) state.canScrollBackward else state.canScrollForward
                    },
                ) else Modifier
            )
            .then(
                if (topShadow) Modifier.Companion
                    .fadingTop(
                        alpha = if (isSystemInDarkTheme()) 0.5f else 0.15f,
                        condition = {
                            if (reverseLayout) state.canScrollForward else state.canScrollBackward
                        },
                    ) else Modifier
            )
    ) {
        content()
    }
}