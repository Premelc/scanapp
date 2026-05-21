package com.domelabs.scanapp.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    data object Scan : NavRoute()
}
