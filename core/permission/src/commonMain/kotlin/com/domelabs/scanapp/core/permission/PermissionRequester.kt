package com.domelabs.scanapp.core.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch

@Composable
fun PermissionRequester() {
    val keys = remember {
        mutableStateMapOf<PermissionType, Int>().apply {
            PermissionType.entries.forEach { type -> put(type, 0) }
        }
    }

    LaunchedEffect(Unit) {
        PermissionType.entries.forEach { type ->
            launch {
                PermissionDispatcher.getPermissionRequestChannel(type).collect {
                    keys[type] = (keys[type] ?: 0) + 1
                }
            }
        }
    }

    PermissionType.entries.forEach { type ->
        val key = keys[type] ?: return@forEach
        if (key != 0) {
            PermissionHandler(
                key = key,
                permissionType = type,
            )
        }
    }
}
