package com.domelabs.scanapp.core.permission

import androidx.compose.runtime.Composable

@Composable
expect fun PermissionHandler(
    key: Int = 0,
    permissionType: PermissionType,
    onPermissionStateChange: (PermissionState) -> Unit = {}
)

@Composable
expect fun CheckPermission(
    permissionType: PermissionType,
    update: (Boolean) -> Unit,
)
