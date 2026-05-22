package com.domelabs.scanapp.feature.scan.impl.presentation.model.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.core.notification.AppSnackbarDispatcher
import com.domelabs.scanapp.core.notification.AppSnackbarEvent
import com.domelabs.scanapp.core.notification.AppSnackbarKind
import com.domelabs.scanapp.core.permission.PermissionDispatcher
import com.domelabs.scanapp.core.permission.PermissionState
import com.domelabs.scanapp.core.permission.PermissionType
import com.domelabs.scanapp.core.scan.ScanError
import com.domelabs.scanapp.core.scan.ScannedCode
import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistorySource
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.PlayScanFeedbackIfEnabledUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.RegisterScanHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock

class ScanViewModel(
    private val registerScanHistoryUseCase: RegisterScanHistoryUseCase,
    private val playScanFeedbackIfEnabledUseCase: PlayScanFeedbackIfEnabledUseCase,
) : ViewModel() {
    private val permissionState = MutableStateFlow(ScanPermissionState.Unknown)
    private val flashEnabled = MutableStateFlow(false)
    private val lastDetection = MutableStateFlow<ScannedCode?>(null)
    private val errorState = MutableStateFlow<ScanError?>(null)
    private val menuDrawerOpen = MutableStateFlow(false)

    private val scanState = combine(
        permissionState,
        flashEnabled,
        lastDetection,
        errorState,
    ) { permission, flash, lastCode, error ->
        ScanViewState(
            permission = permission,
            flashEnabled = flash,
            lastDetection = lastCode,
            error = error,
        )
    }

    val viewState: StateFlow<ScanViewState> = combine(
        scanState,
        menuDrawerOpen,
    ) { baseState, menuDrawer ->
        ScanViewState(
            permission = baseState.permission,
            flashEnabled = baseState.flashEnabled,
            lastDetection = baseState.lastDetection,
            error = baseState.error,
            isMenuDrawerOpen = menuDrawer,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ScanViewState(),
    )

    init {
        observePermissionState()
    }

    fun onInteraction(interaction: ScanInteraction) {
        when (interaction) {
            ScanInteraction.OpenMenuDrawer -> {
                menuDrawerOpen.value = true
            }

            ScanInteraction.CloseMenuDrawer -> {
                menuDrawerOpen.value = false
            }

            ScanInteraction.NavigateToHistory -> {
                viewModelScope.launch {
                    menuDrawerOpen.value = false
                    NavigationDispatcher.navigate(NavRoute.History)
                }
            }

            ScanInteraction.NavigateToSettings -> {
                viewModelScope.launch {
                    menuDrawerOpen.value = false
                    NavigationDispatcher.navigate(NavRoute.Settings)
                }
            }

            ScanInteraction.NavigateToCollections -> {
                viewModelScope.launch {
                    menuDrawerOpen.value = false
                    NavigationDispatcher.navigate(NavRoute.Collections)
                }
            }

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
            }

            is ScanInteraction.OpenScanDetails -> {
                viewModelScope.launch {
                    NavigationDispatcher.navigate(
                        NavRoute.ScanDetails(
                            rawValue = interaction.rawValue,
                            codeKind = interaction.codeKind,
                            codeFormat = interaction.codeFormat,
                            source = interaction.source,
                            scannedAtEpochMillis = interaction.scannedAtEpochMillis,
                        )
                    )
                }
            }

            is ScanInteraction.CodeDetected -> {
                lastDetection.value = interaction.code
                errorState.value = null
                viewModelScope.launch {
                    val accepted = registerScanHistoryUseCase(
                        rawValue = interaction.code.rawValue,
                        codeKind = interaction.code.kind.name,
                        codeFormat = interaction.code.format.name,
                        source = ScanHistorySource.CAMERA,
                    )
                    if (accepted) {
                        val codeKind = interaction.code.kind.name
                        val codeFormat = interaction.code.format.name
                        val scannedAt = Clock.System.now().toEpochMilliseconds()

                        AppSnackbarDispatcher.dispatch(
                            AppSnackbarEvent(
                                title = if (codeKind == "QR") {
                                    "QR code successfully scanned"
                                } else {
                                    "Barcode successfully scanned"
                                },
                                subtitle = interaction.code.rawValue,
                                actionLabel = "Details",
                                kind = if (codeKind == "QR") {
                                    AppSnackbarKind.QrSuccess
                                } else {
                                    AppSnackbarKind.BarcodeSuccess
                                },
                                durationMillis = 5_000L,
                                onAction = {
                                    NavigationDispatcher.navigate(
                                        NavRoute.ScanDetails(
                                            rawValue = interaction.code.rawValue,
                                            codeKind = codeKind,
                                            codeFormat = codeFormat,
                                            source = ScanHistorySource.CAMERA.name,
                                            scannedAtEpochMillis = scannedAt,
                                        )
                                    )
                                },
                            )
                        )
                        playScanFeedbackIfEnabledUseCase()
                    }
                }
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
