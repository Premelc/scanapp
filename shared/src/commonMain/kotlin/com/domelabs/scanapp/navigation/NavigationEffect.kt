package com.domelabs.scanapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.core.navigation.NavigationEvent

@Composable
internal fun NavigationEffect() {
    val navState = LocalNavState.current

    LaunchedEffect(Unit) {
        NavigationDispatcher.getNavFlow().collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    val currentRoute = navState.current
                    if (currentRoute == event.route) return@collect
                    when {
                        event.popBackStack -> navState.backStack.clear()
                        event.popUpTo != null -> {
                            val index = navState.backStack.indexOf(event.popUpTo)
                            if (index != -1) {
                                navState.backStack.removeRange(
                                    index + 1,
                                    navState.backStack.size
                                )
                            }
                        }
                    }

                    navState.navigate(event.route)
                }

                is NavigationEvent.NavigateBack -> navState.pop()
            }
        }
    }
}
