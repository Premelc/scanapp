package com.domelabs.designShowcase

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.domelabs.designShowcase.showcase.ButtonsShowcaseScreen
import com.domelabs.designShowcase.showcase.CardsShowcaseScreen
import com.domelabs.designShowcase.showcase.CategoryCardsShowcaseScreen
import com.domelabs.designShowcase.showcase.CheckboxesShowcaseScreen
import com.domelabs.designShowcase.showcase.ChipsShowcaseScreen
import com.domelabs.designShowcase.showcase.ColorsShowcaseScreen
import com.domelabs.designShowcase.showcase.ComponentShowcaseScreen
import com.domelabs.designShowcase.showcase.DashedBoxShowcaseScreen
import com.domelabs.designShowcase.showcase.FabShowcaseScreen
import com.domelabs.designShowcase.showcase.IconBoxesShowcaseScreen
import com.domelabs.designShowcase.showcase.IconsShowcaseScreen
import com.domelabs.designShowcase.showcase.ScrollShadowsShowcaseScreen
import com.domelabs.designShowcase.showcase.SearchBarShowcaseScreen
import com.domelabs.designShowcase.showcase.ShadowsShowcaseScreen
import com.domelabs.designShowcase.showcase.ShowcaseButtons
import com.domelabs.designShowcase.showcase.ShowcaseCards
import com.domelabs.designShowcase.showcase.ShowcaseCategoryCards
import com.domelabs.designShowcase.showcase.ShowcaseCheckboxes
import com.domelabs.designShowcase.showcase.ShowcaseChips
import com.domelabs.designShowcase.showcase.ShowcaseColors
import com.domelabs.designShowcase.showcase.ShowcaseDashedBox
import com.domelabs.designShowcase.showcase.ShowcaseFab
import com.domelabs.designShowcase.showcase.ShowcaseHub
import com.domelabs.designShowcase.showcase.ShowcaseIconBoxes
import com.domelabs.designShowcase.showcase.ShowcaseIcons
import com.domelabs.designShowcase.showcase.ShowcaseScrollShadows
import com.domelabs.designShowcase.showcase.ShowcaseSearchBar
import com.domelabs.designShowcase.showcase.ShowcaseShadows
import com.domelabs.designShowcase.showcase.ShowcaseSnackbars
import com.domelabs.designShowcase.showcase.ShowcaseTextFields
import com.domelabs.designShowcase.showcase.SnackbarsShowcaseScreen
import com.domelabs.designShowcase.showcase.TextFieldsShowcaseScreen
import com.domelabs.scanapp.uiComponent.theme.ProvideScanAppTheme

@Composable
fun App() {
    ProvideScanAppTheme {
        Surface(
            modifier = Modifier.navigationBarsPadding()
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
                composable<ShowcaseSnackbars> {
                    SnackbarsShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseIcons> {
                    IconsShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseColors> {
                    ColorsShowcaseScreen(onBack = { navController.popBackStack() })
                }
                composable<ShowcaseScrollShadows> {
                    ScrollShadowsShowcaseScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
