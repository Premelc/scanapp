package com.domelabs.scanapp.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    data object Scan : NavRoute()

    @Serializable
    data object Settings : NavRoute()

    @Serializable
    data class ScanDetails(
        val rawValue: String,
        val codeKind: String,
        val codeFormat: String,
        val source: String,
        val scannedAtEpochMillis: Long,
    ) : NavRoute()
}
