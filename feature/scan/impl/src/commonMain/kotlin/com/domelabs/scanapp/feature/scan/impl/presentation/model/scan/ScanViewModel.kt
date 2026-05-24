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
import com.domelabs.scanapp.core.scan.CodeKind
import com.domelabs.scanapp.core.scan.ScanError
import com.domelabs.scanapp.core.scan.ScannedCode
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItemSource
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.RegisterScannedItemUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.PlayScanFeedbackIfEnabledUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScanViewModel(
    private val registerScannedItemUseCase: RegisterScannedItemUseCase,
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

            ScanInteraction.NavigateToAbout -> {
                viewModelScope.launch {
                    menuDrawerOpen.value = false
                    NavigationDispatcher.navigate(NavRoute.About)
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

            ScanInteraction.GalleryCodeNotFound -> {
                viewModelScope.launch {
                    AppSnackbarDispatcher.dispatch(
                        AppSnackbarEvent(
                            title = "No code found in selected image",
                            kind = AppSnackbarKind.Warning,
                            durationMillis = 3_500L,
                        )
                    )
                }
            }

            is ScanInteraction.GalleryCodeDetected -> {
                lastDetection.value = interaction.code
                errorState.value = null
                viewModelScope.launch {
                    processDetectedCode(interaction.code, ScannedItemSource.GALLERY)
                }
            }

            ScanInteraction.RetryAfterError -> {
                errorState.value = null
            }

            is ScanInteraction.CodeDetected -> {
                lastDetection.value = interaction.code
                errorState.value = null
                viewModelScope.launch {
                    processDetectedCode(interaction.code, ScannedItemSource.CAMERA)
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

    private suspend fun processDetectedCode(
        code: ScannedCode,
        source: ScannedItemSource,
    ) {
        val accepted = registerScannedItemUseCase(
            rawValue = code.rawValue,
            codeKind = code.kind.name,
            codeFormat = code.format.name,
            source = source,
        )
        if (accepted != null) {
            AppSnackbarDispatcher.dispatch(
                AppSnackbarEvent(
                    title = if (accepted.codeKind == CodeKind.QR) {
                        "QR code scanned!"
                    } else {
                        "Barcode scanned!"
                    },
                    subtitle = code.rawValue,
                    actionLabel = "Details",
                    kind = if (accepted.codeKind == CodeKind.QR) {
                        AppSnackbarKind.QrSuccess
                    } else {
                        AppSnackbarKind.BarcodeSuccess
                    },
                    durationMillis = 5_000L,
                    onAction = {
                        NavigationDispatcher.navigate(
                            NavRoute.ItemDetails(id = accepted.id)
                        )
                    },
                )
            )
            playScanFeedbackIfEnabledUseCase()
        }
    }
}
