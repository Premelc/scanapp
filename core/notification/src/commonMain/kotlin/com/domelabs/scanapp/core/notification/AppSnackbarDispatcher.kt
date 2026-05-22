package com.domelabs.scanapp.core.notification

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class AppSnackbarKind {
    Info,
    Success,
    QrSuccess,
    BarcodeSuccess,
    Warning,
    Error,
}

data class AppSnackbarEvent(
    val title: String,
    val subtitle: String? = null,
    val actionLabel: String? = null,
    val kind: AppSnackbarKind = AppSnackbarKind.Info,
    val durationMillis: Long = 5_000L,
    val dismissible: Boolean = true,
    val onAction: (suspend () -> Unit)? = null,
)

object AppSnackbarDispatcher {
    private val eventFlow = MutableSharedFlow<AppSnackbarEvent>(extraBufferCapacity = 16)

    fun events() = eventFlow.asSharedFlow()

    suspend fun dispatch(event: AppSnackbarEvent) {
        eventFlow.emit(event)
    }

    fun tryDispatch(event: AppSnackbarEvent): Boolean = eventFlow.tryEmit(event)
}
