package com.domelabs.scanapp.feature.scan.impl.presentation.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ScanDetailsScreen(route: NavRoute.ScanDetails) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NeoBrutalButton(
            text = "Back",
            style = NeoBrutalButtonStyle.Secondary,
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    NavigationDispatcher.back()
                }
            },
        )

        Text(
            text = "Scanned item details",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )

        DetailRow("Raw value", route.rawValue)
        DetailRow("Code kind", route.codeKind)
        DetailRow("Code format", route.codeFormat)
        DetailRow("Source", route.source)
        DetailRow("Scanned at (epoch millis)", route.scannedAtEpochMillis.toString())
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    NeoBrutalCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
