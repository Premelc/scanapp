package com.domelabs.scanapp.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import com.domelabs.scanapp.core.navigation.NavRoute

internal class NavState(
    start: NavRoute,
) {
    val backStack: SnapshotStateList<NavRoute> = mutableStateListOf(start)

    val current: NavRoute
        get() = backStack.last()

    fun navigate(destination: NavRoute) {
        backStack += destination
    }

    fun setStart(destination: NavRoute) {
        backStack += destination
        backStack.removeAt(0)
    }

    fun pop() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
    fun popToRoot() {
        if (backStack.size <= 1) return
        val root = backStack.first()
        backStack.clear()
        backStack.add(root)
    }
}

internal val LocalNavState = staticCompositionLocalOf<NavState> {
    error("NavState not provided")
}
