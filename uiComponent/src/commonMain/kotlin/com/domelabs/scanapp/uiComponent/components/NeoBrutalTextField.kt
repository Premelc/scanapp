package com.domelabs.scanapp.uiComponent.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism
import com.domelabs.scanapp.uiComponent.theme.NeoGrayPlaceholder
import com.domelabs.scanapp.uiComponent.theme.NeoWhite

@Composable
fun NeoBrutalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = if (enabled) NeoBlack else NeoBlack.copy(alpha = 0.4f),
            fontWeight = FontWeight.Medium,
        ),
        cursorBrush = SolidColor(NeoBlack),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { inner ->
            BoxWithNeoBrutalDecoration(enabled = enabled) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = NeoGrayPlaceholder,
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                }
                inner()
            }
        },
    )
}

@Composable
internal fun BoxWithNeoBrutalDecoration(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .neoBrutalStyle(
                backgroundColor = NeoWhite,
                showShadow = enabled,
                cornerRadius = NeoBrutalism.CornerRadius,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        content()
    }
}
