package com.domelabs.scanapp.feature.scan.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.permission.PermissionDispatcher
import com.domelabs.scanapp.core.permission.PermissionState
import com.domelabs.scanapp.core.permission.PermissionType
import com.domelabs.scanapp.core.scan.ScanError
import com.domelabs.scanapp.core.scan.ScannedCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ScanViewModel() : ViewModel() {
    private val permissionState = MutableStateFlow(ScanPermissionState.Unknown)
    private val flashEnabled = MutableStateFlow(false)
    private val lastDetection = MutableStateFlow<ScannedCode?>(null)
    private val errorState = MutableStateFlow<ScanError?>(null)
    private val scannerActive = MutableStateFlow(true)

    val viewState: StateFlow<ScanViewState> = combine(
        permissionState,
        flashEnabled,
        lastDetection,
        errorState,
        scannerActive,
    ) { permission, flash, lastCode, error, active ->
        ScanViewState(
            permission = permission,
            flashEnabled = flash,
            lastDetection = lastCode,
            error = error,
            isScannerActive = active,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = ScanViewState(),
    )

    init {
        observePermissionState()
    }

    fun onInteraction(interaction: ScanInteraction) {
        when (interaction) {
            ScanInteraction.ToggleFlashlight -> {
                flashEnabled.value = !flashEnabled.value
            }

            ScanInteraction.RequestCameraPermission -> {
                viewModelScope.launch {
                    PermissionDispatcher.requestPermission(PermissionType.CAMERA)
                }
            }

            ScanInteraction.OpenGalleryPicker -> Unit

            ScanInteraction.RetryAfterError -> {
                errorState.value = null
                scannerActive.value = true
            }

            ScanInteraction.Resumed -> {
                scannerActive.value = true
            }

            ScanInteraction.Paused -> {
                scannerActive.value = false
            }

            is ScanInteraction.CodeDetected -> {
                lastDetection.value = interaction.code
                errorState.value = null
            }

            is ScanInteraction.ScanFailed -> {
                errorState.value = interaction.error
            }
        }
    }

    private fun observePermissionState() {
        viewModelScope.launch {
            PermissionDispatcher.getPermissionState(PermissionType.CAMERA).collect { permission ->
                permissionState.value = when (permission) {
                    PermissionState.GRANTED -> ScanPermissionState.Granted
                    PermissionState.SHOW_RATIONALE -> ScanPermissionState.ShowRationale
                    PermissionState.DENIED -> ScanPermissionState.Denied
                }
            }
        }
    }
}
