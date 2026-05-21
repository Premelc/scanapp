package com.domelabs.scanapp.uiComponent.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism
import com.domelabs.scanapp.uiComponent.theme.NeoWhite

@Composable
fun NeoBrutalCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = NeoWhite,
    showShadow: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    var cardModifier = modifier
        .fillMaxWidth()
        .neoBrutalStyle(
            backgroundColor = backgroundColor,
            cornerRadius = NeoBrutalism.CornerRadius,
            showShadow = showShadow,
        )
        .clip(RoundedCornerShape(NeoBrutalism.CornerRadius))
        .padding(16.dp)

    if (onClick != null) {
        cardModifier = cardModifier.clickable(onClick = onClick)
    }

    Column(modifier = cardModifier, content = content)
}
