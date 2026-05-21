package com.domelabs.scanapp.uiComponent.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import com.domelabs.scanapp.uiComponent.theme.PastelMint

@Composable
fun NeoBrutalFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    backgroundColor: Color = PastelMint,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val shadowX = if (pressed && enabled) NeoBrutalism.ShadowOffsetPressedX else NeoBrutalism.ShadowOffsetX
    val shadowY = if (pressed && enabled) NeoBrutalism.ShadowOffsetPressedY else NeoBrutalism.ShadowOffsetY

    Box(
        modifier = modifier
            .size(56.dp)
            .neoBrutalStyle(
                backgroundColor = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f),
                cornerRadius = NeoBrutalism.CornerRadius,
                shadowOffsetX = if (enabled) shadowX else 0.dp,
                shadowOffsetY = if (enabled) shadowY else 0.dp,
                showShadow = enabled,
            )
            .clip(RoundedCornerShape(NeoBrutalism.CornerRadius))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = NeoBlack,
        )
    }
}
