package com.domelabs.scanapp.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    data object Scan : NavRoute()

    @Serializable
    data object History : NavRoute()

    @Serializable
    data object Settings : NavRoute()

    @Serializable
    data object Collections : NavRoute()

    @Serializable
    data object About : NavRoute()

    @Serializable
    data object Licenses : NavRoute()

    @Serializable
    data class ScanDetails(
        val id: Long,
    ) : NavRoute()
}
