package com.domelabs.scanapp

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.domelabs.scanapp.showcase.ButtonsShowcaseScreen
import com.domelabs.scanapp.showcase.CardsShowcaseScreen
import com.domelabs.scanapp.showcase.CategoryCardsShowcaseScreen
import com.domelabs.scanapp.showcase.CheckboxesShowcaseScreen
import com.domelabs.scanapp.showcase.ChipsShowcaseScreen
import com.domelabs.scanapp.showcase.DashedBoxShowcaseScreen
import com.domelabs.scanapp.showcase.FabShowcaseScreen
import com.domelabs.scanapp.showcase.IconBoxesShowcaseScreen
import com.domelabs.scanapp.showcase.SearchBarShowcaseScreen
import com.domelabs.scanapp.showcase.ShadowsShowcaseScreen
import com.domelabs.scanapp.showcase.ShowcaseButtons
import com.domelabs.scanapp.showcase.ShowcaseCards
import com.domelabs.scanapp.showcase.ShowcaseCategoryCards
import com.domelabs.scanapp.showcase.ShowcaseCheckboxes
import com.domelabs.scanapp.showcase.ShowcaseChips
import com.domelabs.scanapp.showcase.ShowcaseDashedBox
import com.domelabs.scanapp.showcase.ShowcaseFab
import com.domelabs.scanapp.showcase.ShowcaseHub
import com.domelabs.scanapp.showcase.ShowcaseIconBoxes
import com.domelabs.scanapp.showcase.ShowcaseSearchBar
import com.domelabs.scanapp.showcase.ShowcaseShadows
import com.domelabs.scanapp.showcase.ShowcaseTextFields
import com.domelabs.scanapp.showcase.TextFieldsShowcaseScreen
import com.domelabs.scanapp.uiComponent.theme.ProvideScanAppTheme

@Composable
fun App() {
    ProvideScanAppTheme {
        Surface(
            modifier = Modifier.systemBarsPadding()
        ) {
            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = ShowcaseHub) {
                composable<ShowcaseHub> {
                    ComponentShowcaseScreen(navController)
                }
                composable<ShowcaseButtons> {
                    ButtonsShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseTextFields> {
                    TextFieldsShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseSearchBar> {
                    SearchBarShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseChips> {
                    ChipsShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseCheckboxes> {
                    CheckboxesShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseCards> {
                    CardsShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseCategoryCards> {
                    CategoryCardsShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseFab> {
                    FabShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseIconBoxes> {
                    IconBoxesShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseDashedBox> {
                    DashedBoxShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseShadows> {
                    ShadowsShowcaseScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
