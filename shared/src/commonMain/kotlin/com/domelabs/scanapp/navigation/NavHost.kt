package com.domelabs.scanapp.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.feature.collections.api.CollectionDetailsEntryPoint
import com.domelabs.scanapp.feature.collections.api.CollectionsScreenEntryPoint
import com.domelabs.scanapp.feature.scan.api.ItemDetailsEntryPoint
import com.domelabs.scanapp.feature.scan.api.ScanHistoryEntryPoint
import com.domelabs.scanapp.feature.scan.api.ScanScreenEntryPoint
import com.domelabs.scanapp.feature.settings.api.AboutScreenEntryPoint
import com.domelabs.scanapp.feature.settings.api.LicensesScreenEntryPoint
import com.domelabs.scanapp.feature.settings.api.SettingsScreenEntryPoint

@Composable
internal fun NavHost() {
    val navState = LocalNavState.current
    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 300)).togetherWith(
                fadeOut(animationSpec = tween(durationMillis = 300))
            )
        },
        popTransitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 300)).togetherWith(
                fadeOut(animationSpec = tween(durationMillis = 300))
            )
        },
        predictivePopTransitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 300)).togetherWith(
                fadeOut(animationSpec = tween(durationMillis = 300))
            )
        },
        backStack = navState.backStack,
        entryProvider = entryProvider {
            coreNavigation()
        }
    )
}

internal fun EntryProviderScope<NavRoute>.coreNavigation() {
    entry<NavRoute.Scan> {
        ScanScreenEntryPoint()
    }
    entry<NavRoute.History> {
        ScanHistoryEntryPoint()
    }
    entry<NavRoute.Settings> {
        SettingsScreenEntryPoint()
    }
    entry<NavRoute.Collections> {
        CollectionsScreenEntryPoint()
    }
    entry<NavRoute.About> {
        AboutScreenEntryPoint()
    }
    entry<NavRoute.Licenses> {
        LicensesScreenEntryPoint()
    }
    entry<NavRoute.ItemDetails> { route ->
        ItemDetailsEntryPoint(route)
    }
    entry<NavRoute.CollectionDetails> { route ->
        CollectionDetailsEntryPoint(route)
    }
}
