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
import com.domelabs.scanapp.feature.scan.api.ScanScreenEntryPoint

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
}
