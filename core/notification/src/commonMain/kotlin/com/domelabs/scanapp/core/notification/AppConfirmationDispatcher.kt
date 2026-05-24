package com.domelabs.scanapp.core.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Payload for the app-wide destructive-action confirmation bottom sheet.
 *
 * @param onConfirm invoked when the primary (usually destructive) action is chosen.
 * @param onSecondaryConfirm optional second confirm action (e.g. "Move to Unspecified").
 * @param onCancel invoked when the user dismisses the sheet or taps cancel.
 */
data class AppConfirmationRequest(
    val title: String,
    val message: String,
    val confirmLabel: String,
    val cancelLabel: String = "Cancel",
    val secondaryConfirmLabel: String? = null,
    val onConfirm: suspend () -> Unit,
    val onSecondaryConfirm: (suspend () -> Unit)? = null,
    val onCancel: (suspend () -> Unit)? = null,
)

data class ActiveAppConfirmation(
    val id: Long,
    val request: AppConfirmationRequest,
)

object AppConfirmationDispatcher {
    private val mutex = Mutex()
    private var nextId = 0L

    private val _active = MutableStateFlow<ActiveAppConfirmation?>(null)
    val active: StateFlow<ActiveAppConfirmation?> = _active.asStateFlow()

    fun show(request: AppConfirmationRequest) {
        _active.value = ActiveAppConfirmation(
            id = ++nextId,
            request = request,
        )
    }

    suspend fun dismiss(invokeCancel: Boolean = true) {
        mutex.withLock {
            val current = _active.value ?: return
            _active.value = null
            if (invokeCancel) {
                current.request.onCancel?.invoke()
            }
        }
    }

    suspend fun confirmPrimary() {
        mutex.withLock {
            val current = _active.value ?: return
            _active.value = null
            current.request.onConfirm()
        }
    }

    suspend fun confirmSecondary() {
        mutex.withLock {
            val current = _active.value ?: return
            val secondary = current.request.onSecondaryConfirm ?: return
            _active.value = null
            secondary()
        }
    }
}
