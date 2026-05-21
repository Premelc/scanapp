package com.domelabs.scanapp

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.domelabs.scanapp.uiComponent.theme.ProvideScanAppTheme
import kotlinx.serialization.Serializable

@Serializable
data object ComponentShowcase

@Composable
fun App() {
    ProvideScanAppTheme {
        Surface {
            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = ComponentShowcase) {
                composable<ComponentShowcase> {
                    ComponentShowcaseScreen()
                }
            }
        }
    }
}
