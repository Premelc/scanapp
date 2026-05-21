package com.domelabs.scanapp.core.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NavigationDispatcher {
    private val _navigationChannel = MutableSharedFlow<NavigationEvent>()

    fun getNavFlow() = _navigationChannel.asSharedFlow()

    suspend fun navigate(
        route: NavRoute,
        popBackStack: Boolean = false,
        popUpTo: NavRoute? = null,
    ) {
        _navigationChannel.emit(
            NavigationEvent.NavigateTo(
                route = route,
                popBackStack = popBackStack,
                popUpTo = popUpTo
            )
        )
    }

    suspend fun navigate(event: NavigationEvent) {
        _navigationChannel.emit(event)
    }

    suspend fun back() {
        _navigationChannel.emit(NavigationEvent.NavigateBack)
    }
}

sealed interface NavigationEvent {
    data class NavigateTo(
        val route: NavRoute,
        val popBackStack: Boolean = false,
        val popUpTo: NavRoute? = null,
    ) : NavigationEvent

    data object NavigateBack : NavigationEvent
}
