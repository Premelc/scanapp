package com.domelabs.scanapp.feature.scan.impl.presentation.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import kotlinx.coroutines.flow.drop
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private data class MenuNavItem(
    val title: String,
    val description: String,
    val startIcon: DrawableResource,
    val onClick: () -> Unit,
)

@Composable
internal fun ScanMenuDrawerLayout(
    isMenuDrawerOpen: Boolean,
    close: () -> Unit,
    navigateHistory: () -> Unit,
    navigateSettings: () -> Unit,
    navigateCollections: () -> Unit,
    content: @Composable () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(isMenuDrawerOpen) {
        if (isMenuDrawerOpen && drawerState.currentValue != DrawerValue.Open) {
            drawerState.open()
        } else if (!isMenuDrawerOpen && drawerState.currentValue != DrawerValue.Closed) {
            drawerState.close()
        }
    }

    LaunchedEffect(drawerState, isMenuDrawerOpen) {
        snapshotFlow { drawerState.currentValue }
            .drop(1)
            .collect { drawerValue ->
                if (drawerValue == DrawerValue.Closed && isMenuDrawerOpen) {
                    close()
                }
            }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        drawerContainerColor = Color.Transparent,
                        modifier = Modifier.fillMaxWidth(0.82f),
                    ) {
                        MenuDrawerSheetContent(
                            onClose = close,
                            navigateHistory = navigateHistory,
                            navigateSettings = navigateSettings,
                            navigateCollections = navigateCollections,
                        )
                    }
                }
            },
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                content()
            }
        }
    }
}

@Composable
private fun MenuDrawerSheetContent(
    onClose: () -> Unit,
    navigateHistory: () -> Unit,
    navigateSettings: () -> Unit,
    navigateCollections: () -> Unit,
) {
    val items = listOf(
        MenuNavItem(
            title = "History",
            description = "View full scan history and manage records",
            startIcon = ScanAppTheme.Icons.history,
            onClick = navigateHistory,
        ),
        MenuNavItem(
            title = "Settings",
            description = "Configure scan behavior and feedback",
            startIcon = ScanAppTheme.Icons.gear,
            onClick = navigateSettings,
        ),
        MenuNavItem(
            title = "Collections",
            description = "Browse saved collections and groups",
            startIcon = ScanAppTheme.Icons.folder,
            onClick = navigateCollections,
        ),
    )

    NeoBrutalCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        backgroundColor = NeoWhite.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Main Menu",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
            NeoBrutalButton(
                text = "Close",
                style = NeoBrutalButtonStyle.Secondary,
                onClick = onClose,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { item ->
                NeoBrutalCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = item.onClick),
                    showShadow = false,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(item.startIcon),
                            contentDescription = item.title,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            )
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        Icon(
                            painter = painterResource(ScanAppTheme.Icons.chevronRight),
                            contentDescription = "${item.title} forward",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
