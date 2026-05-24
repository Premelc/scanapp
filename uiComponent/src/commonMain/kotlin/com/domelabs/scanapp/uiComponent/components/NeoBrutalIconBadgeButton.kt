package com.domelabs.scanapp.uiComponent.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import com.domelabs.scanapp.uiComponent.theme.PastelSalmon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun NeoBrutalIconBadgeButton(
    icon: DrawableResource,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PastelSalmon,
    iconTint: Color = NeoBlack,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .neoBrutalStyle(
                backgroundColor = backgroundColor,
                cornerRadius = NeoBrutalism.CornerRadiusSmall,
                shadowOffsetX = NeoBrutalism.ShadowOffsetPressedX,
                shadowOffsetY = NeoBrutalism.ShadowOffsetPressedY,
            )
            .clip(RoundedCornerShape(NeoBrutalism.CornerRadiusSmall))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(22.dp),
        )
    }
}
