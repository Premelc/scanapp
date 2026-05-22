package com.domelabs.scanapp.feature.scan.api

import androidx.compose.runtime.Composable
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.feature.scan.impl.presentation.model.ScanDetailsScreen
import com.domelabs.scanapp.feature.scan.impl.presentation.model.ScanScreen

@Composable
fun ScanScreenEntryPoint() {
    ScanScreen()
}

@Composable
fun ScanDetailsEntryPoint(route: NavRoute.ScanDetails) {
    ScanDetailsScreen(route)
}
