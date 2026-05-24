package com.domelabs.scanapp

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.notification.AppSnackbarHost
import com.domelabs.scanapp.core.permission.PermissionChecker
import com.domelabs.scanapp.core.permission.PermissionRequester
import com.domelabs.scanapp.navigation.LocalNavState
import com.domelabs.scanapp.navigation.NavHost
import com.domelabs.scanapp.navigation.NavState
import com.domelabs.scanapp.navigation.NavigationEffect
import com.domelabs.scanapp.uiComponent.components.MeasuredLayout
import com.domelabs.scanapp.uiComponent.theme.ProvideScanAppTheme

@Composable
fun App() {
    ProvideScanAppTheme {
        val navState = remember { NavState(start = NavRoute.Scan) }
        CompositionLocalProvider(LocalNavState provides navState) {

            PermissionChecker()
            PermissionRequester()

            MeasuredLayout(modifier = Modifier) {
                Scaffold(
                    snackbarHost = { AppSnackbarHost() },
                ) {
                    NavigationEffect()
                    NavHost()
                }
            }
        }
    }
}
