package com.domelabs.scanapp.feature.settings.impl.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinInject(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NeoBrutalButton(
            text = "Back",
            style = NeoBrutalButtonStyle.Secondary,
            onClick = { viewModel.onInteraction(SettingsInteraction.NavigateBack) },
        )

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = "Scan feedback",
            style = MaterialTheme.typography.bodyMedium,
        )

        if (state.capabilities.supportsSound) {
            SettingToggleCard(
                title = "Sound",
                description = "Play a short sound after accepted scans",
                checked = state.soundEnabled,
                onCheckedChange = { viewModel.onInteraction(SettingsInteraction.ToggleSound(it)) },
            )
        }

        if (state.capabilities.supportsVibration) {
            SettingToggleCard(
                title = "Vibration",
                description = "Play a subtle vibration after accepted scans",
                checked = state.vibrationEnabled,
                onCheckedChange = { viewModel.onInteraction(SettingsInteraction.ToggleVibration(it)) },
            )
        }

        if (!state.capabilities.supportsSound && !state.capabilities.supportsVibration) {
            NeoBrutalCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No scan feedback controls are available on this device.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun SettingToggleCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    NeoBrutalCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}
