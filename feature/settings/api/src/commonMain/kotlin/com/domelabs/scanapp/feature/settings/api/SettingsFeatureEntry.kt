package com.domelabs.scanapp.feature.settings.api

import androidx.compose.runtime.Composable
import com.domelabs.scanapp.feature.settings.impl.presentation.SettingsScreen
import com.domelabs.scanapp.feature.settings.impl.presentation.about.AboutScreen
import com.domelabs.scanapp.feature.settings.impl.presentation.about.LicensesScreen

@Composable
fun SettingsScreenEntryPoint() {
    SettingsScreen()
}

@Composable
fun AboutScreenEntryPoint() {
    AboutScreen()
}

@Composable
fun LicensesScreenEntryPoint() {
    LicensesScreen()
}
