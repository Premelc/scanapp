package com.domelabs.scanapp.feature.scan.impl.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.core.permission.PermissionDispatcher
import com.domelabs.scanapp.core.permission.PermissionState
import com.domelabs.scanapp.core.permission.PermissionType
import com.domelabs.scanapp.core.scan.ScanError
import com.domelabs.scanapp.core.scan.ScannedCode
import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistorySource
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.ClearScanHistoryUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.DeleteScanHistoryItemUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.ObserveScanHistoryUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.PlayScanFeedbackIfEnabledUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.RegisterScanHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class ScanViewModel(
    observeScanHistoryUseCase: ObserveScanHistoryUseCase,
    private val registerScanHistoryUseCase: RegisterScanHistoryUseCase,
    private val deleteScanHistoryItemUseCase: DeleteScanHistoryItemUseCase,
    private val clearScanHistoryUseCase: ClearScanHistoryUseCase,
    private val playScanFeedbackIfEnabledUseCase: PlayScanFeedbackIfEnabledUseCase,
) : ViewModel() {
    private val permissionState = MutableStateFlow(ScanPermissionState.Unknown)
    private val flashEnabled = MutableStateFlow(false)
    private val lastDetection = MutableStateFlow<ScannedCode?>(null)
    private val errorState = MutableStateFlow<ScanError?>(null)
    private val historyDrawerOpen = MutableStateFlow(false)

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
        historyDrawerOpen,
        observeScanHistoryUseCase(),
    ) { baseState, drawerOpen, history ->
        ScanViewState(
            permission = baseState.permission,
            flashEnabled = baseState.flashEnabled,
            lastDetection = baseState.lastDetection,
            error = baseState.error,
            isHistoryDrawerOpen = drawerOpen,
            historyItems = history.map { it.toUi(Clock.System.now().toEpochMilliseconds()) },
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
            ScanInteraction.OpenHistoryDrawer -> {
                historyDrawerOpen.value = true
            }

            ScanInteraction.CloseHistoryDrawer -> {
                historyDrawerOpen.value = false
            }

            ScanInteraction.OpenSettings -> {
                viewModelScope.launch {
                    historyDrawerOpen.value = false
                    NavigationDispatcher.navigate(NavRoute.Settings)
                }
            }

            is ScanInteraction.DeleteHistoryItem -> {
                viewModelScope.launch {
                    deleteScanHistoryItemUseCase(interaction.id)
                }
            }

            ScanInteraction.ClearHistory -> {
                viewModelScope.launch {
                    clearScanHistoryUseCase()
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
