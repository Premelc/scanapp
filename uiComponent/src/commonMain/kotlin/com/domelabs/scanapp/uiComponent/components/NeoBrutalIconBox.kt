package com.domelabs.scanapp.uiComponent.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism

@Composable
fun NeoBrutalIconBox(
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    contentDescription: String? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .neoBrutalStyle(
                backgroundColor = backgroundColor,
                cornerRadius = NeoBrutalism.CornerRadiusSmall,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = NeoBlack,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}
