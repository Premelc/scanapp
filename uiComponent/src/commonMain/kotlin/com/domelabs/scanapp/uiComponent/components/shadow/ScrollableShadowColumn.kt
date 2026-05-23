package com.domelabs.scanapp.uiComponent.components.shadow

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScrollableShadowColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollState: ScrollState = rememberScrollState(),
    topShadow: Boolean = true,
    bottomShadow: Boolean = false,
    scrollOffset: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollValueDp = with(LocalDensity.current) { scrollState.value.toDp() }
    val scrollMaxValueDp = with(LocalDensity.current) { scrollState.maxValue.toDp() }

    Column(
        modifier = Modifier
            .thenIf(
                predicate = topShadow,
                modifierBuilder = {
                    fadingTop(
                        alpha = if (isSystemInDarkTheme()) 0.5f else 0.15f,
                        fadingHeight = (scrollValueDp - 2.dp).coerceAtMost(4.dp),
                        condition = { scrollValueDp-scrollOffset > 2.dp },
                    )
                },
            )
            .thenIf(
                predicate = bottomShadow,
                modifierBuilder = {
                    fadingBottom(
                        alpha = if (isSystemInDarkTheme()) 0.5f else 0.15f,
                        fadingHeight = (scrollMaxValueDp - scrollValueDp).coerceAtMost(4.dp),
                        condition = { scrollValueDp-scrollOffset < scrollMaxValueDp },
                    )
                },
            )
            .verticalScroll(scrollState)
            .then(modifier),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}