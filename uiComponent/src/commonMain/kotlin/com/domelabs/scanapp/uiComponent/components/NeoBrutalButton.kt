package com.domelabs.scanapp.uiComponent.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism
import com.domelabs.scanapp.uiComponent.theme.NeoWhite

enum class NeoBrutalButtonStyle {
    Primary,
    Secondary,
    Pastel,
}

@Composable
fun NeoBrutalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: NeoBrutalButtonStyle = NeoBrutalButtonStyle.Primary,
    enabled: Boolean = true,
    pastelColor: Color = MaterialTheme.colorScheme.tertiary,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val background = when (style) {
        NeoBrutalButtonStyle.Primary -> if (enabled) NeoBlack else Color(0xFF4A4A4A)
        NeoBrutalButtonStyle.Secondary -> if (enabled) NeoWhite else NeoWhite.copy(alpha = 0.6f)
        NeoBrutalButtonStyle.Pastel -> if (enabled) pastelColor else pastelColor.copy(alpha = 0.5f)
    }
    val contentColor = when (style) {
        NeoBrutalButtonStyle.Primary -> NeoWhite
        NeoBrutalButtonStyle.Secondary, NeoBrutalButtonStyle.Pastel -> NeoBlack
    }

    val shadowX = if (pressed && enabled) NeoBrutalism.ShadowOffsetPressedX else NeoBrutalism.ShadowOffsetX
    val shadowY = if (pressed && enabled) NeoBrutalism.ShadowOffsetPressedY else NeoBrutalism.ShadowOffsetY

    Box(
        modifier = modifier
            .neoBrutalStyle(
                backgroundColor = background,
                shadowOffsetX = if (enabled) shadowX else 0.dp,
                shadowOffsetY = if (enabled) shadowY else 0.dp,
                showShadow = enabled,
            )
            .clip(RoundedCornerShape(NeoBrutalism.CornerRadius))
            .semantics { role = Role.Button }
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = contentColor.copy(alpha = if (enabled) 1f else 0.5f),
                ),
            )
        }
    }
}
