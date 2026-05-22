package com.domelabs.scanapp.feature.scan.api

import androidx.compose.runtime.Composable
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.feature.scan.impl.presentation.model.details.ScanDetailsScreen
import com.domelabs.scanapp.feature.scan.impl.presentation.model.history.ScanHistoryScreen
import com.domelabs.scanapp.feature.scan.impl.presentation.model.scan.ScanScreen

@Composable
fun ScanScreenEntryPoint() {
    ScanScreen()
}

@Composable
fun ScanDetailsEntryPoint(route: NavRoute.ScanDetails) {
    ScanDetailsScreen(route)
}

@Composable
fun ScanHistoryEntryPoint() {
    ScanHistoryScreen()
}
