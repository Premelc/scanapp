package com.domelabs.scanapp.core.scan

import com.google.mlkit.vision.barcode.common.Barcode

internal fun Barcode.resolveRawValue(): String? {
    rawValue?.takeIf { it.isNotBlank() }?.let { return it }
    val bytes = rawBytes?.takeIf { it.isNotEmpty() } ?: return null
    return bytes.toString(Charsets.ISO_8859_1).takeIf { it.isNotBlank() }
}
