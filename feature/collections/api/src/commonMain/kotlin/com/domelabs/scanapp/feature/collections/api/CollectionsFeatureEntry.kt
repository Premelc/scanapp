package com.domelabs.scanapp.feature.collections.api

import androidx.compose.runtime.Composable
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.feature.collections.impl.presentation.CollectionsScreen
import com.domelabs.scanapp.feature.collections.impl.presentation.detail.CollectionDetailScreen

@Composable
fun CollectionsScreenEntryPoint() {
    CollectionsScreen()
}

@Composable
fun CollectionDetailsEntryPoint(route: NavRoute.CollectionDetails) {
    CollectionDetailScreen(id = route.id)
}
