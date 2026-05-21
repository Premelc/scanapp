package com.domelabs.scanapp.uiComponent.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import com.domelabs.scanapp.uiComponent.theme.PastelBlue

@Composable
fun NeoBrutalChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val shadowX = if (pressed && enabled) NeoBrutalism.ShadowOffsetPressedX else NeoBrutalism.ShadowOffsetX
    val shadowY = if (pressed && enabled) NeoBrutalism.ShadowOffsetPressedY else NeoBrutalism.ShadowOffsetY

    val background = when {
        !enabled -> NeoWhite.copy(alpha = 0.5f)
        selected -> PastelBlue
        else -> NeoWhite
    }

    Row(
        modifier = modifier
            .neoBrutalStyle(
                backgroundColor = background,
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
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        icon?.let {
            Icon(imageVector = it, contentDescription = null, tint = NeoBlack)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = NeoBlack.copy(alpha = if (enabled) 1f else 0.4f),
            ),
        )
    }
}
