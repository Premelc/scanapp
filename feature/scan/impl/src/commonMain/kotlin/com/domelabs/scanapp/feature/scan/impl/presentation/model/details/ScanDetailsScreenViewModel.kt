package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.scan.ScanCodePlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

class ScanDetailsScreenViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(ScanDetailsViewState())
    val viewState: StateFlow<ScanDetailsViewState> = _viewState.asStateFlow()

    private var loadedRouteKey: String? = null

    fun load(route: NavRoute.ScanDetails) {
        val newRouteKey = "${route.rawValue}|${route.codeFormat}|${route.scannedAtEpochMillis}"
        if (loadedRouteKey == newRouteKey) return
        loadedRouteKey = newRouteKey

        _viewState.update {
            it.copy(
                matrixState = CodePreviewState.Loading,
                scannedAtLabel = formatScannedAt(route.scannedAtEpochMillis),
            )
        }

        viewModelScope.launch {
            val generated = withContext(Dispatchers.Default) {
                ScanCodePlatform.generateMatrix(route.rawValue, route.codeFormat)
            }
            _viewState.update {
                it.copy(
                    matrixState = if (generated == null) {
                        CodePreviewState.Unavailable
                    } else {
                        CodePreviewState.Ready(generated)
                    }
                )
            }
        }
    }

    suspend fun generateImageForShare(route: NavRoute.ScanDetails): ByteArray? {
        return withContext(Dispatchers.Default) {
            ScanCodePlatform.generatePng(
                rawValue = route.rawValue,
                codeFormat = route.codeFormat,
                sizePx = if (route.codeKind == "QR") 1200 else 1600,
            )
        }
    }

    private fun formatScannedAt(epochMillis: Long): String {
        val dateTime = Instant
            .fromEpochMilliseconds(epochMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return "%04d-%02d-%02d %02d:%02d:%02d".format(
            dateTime.year,
            dateTime.month.number,
            dateTime.day,
            dateTime.hour,
            dateTime.minute,
            dateTime.second,
        )
    }
}