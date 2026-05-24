package com.domelabs.designShowcase.showcase

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.components.shadow.ScrollableShadowColumn

@Composable
fun ScrollShadowsShowcaseScreen(onBack: () -> Unit) {
    var topShadow by remember { mutableStateOf(true) }
    var bottomShadow by remember { mutableStateOf(true) }

    ShowcaseDetailScreen(
        title = "Scroll shadows",
        onBack = onBack,
        controls = {
            ShowcaseToggleRow("Top fade", topShadow) { topShadow = it }
            ShowcaseToggleRow("Bottom fade", bottomShadow) { bottomShadow = it }
        },
        preview = {
            NeoBrutalCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            ) {
                ScrollableShadowColumn(
                    modifier = Modifier.fillMaxWidth(),
                    topShadow = topShadow,
                    bottomShadow = bottomShadow,
                ) {
                    repeat(12) { index ->
                        Text(
                            text = "Scrollable row ${index + 1}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
    )
}
