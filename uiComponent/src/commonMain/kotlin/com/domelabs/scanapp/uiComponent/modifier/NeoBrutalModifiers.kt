package com.domelabs.scanapp.uiComponent.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism

/**
 * Hard offset shadow (no blur) — classic neobrutalism depth.
 * Apply before background/border so the shadow sits behind the component.
 */
@Stable
fun Modifier.neoBrutalShadow(
    offsetX: Dp = NeoBrutalism.ShadowOffsetX,
    offsetY: Dp = NeoBrutalism.ShadowOffsetY,
    color: Color = NeoBlack,
    cornerRadius: Dp = NeoBrutalism.CornerRadius,
): Modifier = drawBehind {
    val radius = cornerRadius.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(offsetX.toPx(), offsetY.toPx()),
        size = size,
        cornerRadius = CornerRadius(radius, radius),
    )
}

@Stable
fun Modifier.neoBrutalBorder(
    width: Dp = NeoBrutalism.BorderWidth,
    color: Color = NeoBlack,
    cornerRadius: Dp = NeoBrutalism.CornerRadius,
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return border(width, color, shape)
}

/**
 * Full neobrutal container: hard shadow + fill + thick border.
 */
@Stable
fun Modifier.neoBrutalStyle(
    backgroundColor: Color = Color.White,
    borderColor: Color = NeoBlack,
    borderWidth: Dp = NeoBrutalism.BorderWidth,
    cornerRadius: Dp = NeoBrutalism.CornerRadius,
    shadowOffsetX: Dp = NeoBrutalism.ShadowOffsetX,
    shadowOffsetY: Dp = NeoBrutalism.ShadowOffsetY,
    shadowColor: Color = NeoBlack,
    showShadow: Boolean = true,
    shape: Shape = RoundedCornerShape(cornerRadius),
): Modifier = composed {
    val shadowModifier = if (showShadow) {
        Modifier.neoBrutalShadow(
            offsetX = shadowOffsetX,
            offsetY = shadowOffsetY,
            color = shadowColor,
            cornerRadius = cornerRadius,
        )
    } else {
        Modifier
    }
    this
        .then(shadowModifier)
        .background(backgroundColor, shape)
        .border(borderWidth, borderColor, shape)
}
