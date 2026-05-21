package com.domelabs.scanapp

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.domelabs.scanapp.uiComponent.theme.ProvideScanAppTheme

@Composable
fun App() {
    ProvideScanAppTheme {
        Surface(
            modifier = Modifier.systemBarsPadding()
        ) {
            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = ShowcaseHub) {

            }
        }
    }
}
