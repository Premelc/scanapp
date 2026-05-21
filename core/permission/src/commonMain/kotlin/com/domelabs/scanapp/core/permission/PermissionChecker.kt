package com.domelabs.scanapp.core.permission

import androidx.compose.runtime.Composable

@Composable
fun PermissionChecker() {
    PermissionType.entries.forEach { type ->
        CheckPermission(
            permissionType = type,
            update = {
                PermissionDispatcher.updatePermissionResult(
                    permission = type,
                    state = if (it) PermissionState.GRANTED else PermissionState.DENIED
                )
            }
        )
    }
}
