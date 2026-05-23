package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

sealed interface ScanDetailsInteraction {
    data object Share: ScanDetailsInteraction
    data object DismissShare: ScanDetailsInteraction
    data class Delete(val id:Long): ScanDetailsInteraction
    data object Back: ScanDetailsInteraction
}