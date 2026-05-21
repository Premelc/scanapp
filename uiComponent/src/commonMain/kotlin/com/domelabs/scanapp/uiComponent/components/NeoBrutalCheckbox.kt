package com.domelabs.scanapp.uiComponent.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import com.domelabs.scanapp.uiComponent.theme.PastelGreen

@Composable
fun NeoBrutalCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .neoBrutalStyle(
                backgroundColor = if (checked) PastelGreen else NeoWhite,
                cornerRadius = NeoBrutalism.CornerRadiusSmall,
                showShadow = enabled,
            )
            .clip(RoundedCornerShape(NeoBrutalism.CornerRadiusSmall))
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = NeoBlack,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
