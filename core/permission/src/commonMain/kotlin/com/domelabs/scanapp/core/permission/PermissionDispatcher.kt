package com.domelabs.scanapp.core.permission

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.plus

object PermissionDispatcher {
    private val _permissions = MutableStateFlow<Map<PermissionType, PermissionState>>(emptyMap())
    val permissions: StateFlow<Map<PermissionType, PermissionState>> = _permissions.asStateFlow()

    private val _permissionRequestChannels = mutableMapOf<PermissionType, Channel<Unit>>()

    fun updatePermissionResult(permission: PermissionType, state: PermissionState) {
        _permissions.update { current ->
            current + (permission to state)
        }
    }

    fun getPermissionState(type: PermissionType) = permissions.map { it[type] }.filterNotNull()

    fun getPermissionRequestChannel(type: PermissionType) =
        _permissionRequestChannels.getOrPut(type) {
            Channel(Channel.BUFFERED)
        }.receiveAsFlow()

    suspend fun requestPermission(type: PermissionType) {
        val channel = _permissionRequestChannels.getOrPut(type) {
            Channel(Channel.BUFFERED)
        }
        channel.send(Unit)
    }
}
