package com.domelabs.scanapp.core.utils

sealed interface OperationStepStatus {
    data object Skip : OperationStepStatus
    data object Pending : OperationStepStatus
    data object InProgress : OperationStepStatus
    data class Success(val data: Any) : OperationStepStatus
    data class Error(val message: String) : OperationStepStatus
}