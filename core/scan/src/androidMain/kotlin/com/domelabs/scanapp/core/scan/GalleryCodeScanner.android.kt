package com.domelabs.scanapp.core.scan

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_AZTEC
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODABAR
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODE_128
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODE_39
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODE_93
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_DATA_MATRIX
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_EAN_13
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_EAN_8
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_ITF
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_PDF417
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_UPC_A
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_UPC_E
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
actual fun rememberGalleryCodeScanner(): GalleryCodeScanner {
    val context = LocalContext.current
    return remember(context) {
        AndroidGalleryCodeScanner(context)
    }
}

private class AndroidGalleryCodeScanner(
    private val context: Context,
) : GalleryCodeScanner {
    override suspend fun scanCodeFromImageUri(uri: String): ScannedCode? {
        val image = runCatching { InputImage.fromFilePath(context, uri.toUri()) }.getOrNull() ?: return null
        val scanner = BarcodeScanning.getClient()
        val barcodes = scanner.process(image).awaitResult() ?: return decodeGs1DataBarFromBitmapUri(context, uri)
        val barcode = barcodes.firstOrNull() ?: return decodeGs1DataBarFromBitmapUri(context, uri)

        val rawValue = barcode.resolveRawValue()
            ?: return decodeGs1DataBarFromBitmapUri(context, uri)

        return ScannedCode(
            kind = barcode.format.toCodeKind(),
            format = barcode.format.toCodeFormat(),
            rawValue = rawValue,
        )
    }
}

private suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitResult(): T? =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener {
            continuation.resume(it)
        }.addOnFailureListener {
            continuation.resume(null)
        }.addOnCanceledListener {
            continuation.resume(null)
        }
    }

private fun decodeGs1DataBarFromBitmapUri(context: Context, uri: String): ScannedCode? {
    val bitmap = context.contentResolver.openInputStream(uri.toUri())?.use {
        BitmapFactory.decodeStream(it)
    } ?: return null

    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    val reader = MultiFormatReader().apply {
        setHints(
            mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(
                    BarcodeFormat.RSS_14,
                    BarcodeFormat.RSS_EXPANDED,
                ),
                DecodeHintType.TRY_HARDER to true,
            )
        )
    }
    val source = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
    val result = runCatching { reader.decodeWithState(BinaryBitmap(HybridBinarizer(source))) }.getOrNull()
    reader.reset()
    val format = result?.barcodeFormat?.toGs1CodeFormat() ?: return null
    val value = result.text?.takeIf { it.isNotBlank() } ?: return null
    return ScannedCode(
        kind = CodeKind.BARCODE,
        format = format,
        rawValue = value,
    )
}

private fun Int.toCodeFormat(): CodeFormat = when (this) {
    FORMAT_QR_CODE -> CodeFormat.QR_CODE
    FORMAT_AZTEC -> CodeFormat.AZTEC
    FORMAT_DATA_MATRIX -> CodeFormat.DATA_MATRIX
    FORMAT_PDF417 -> CodeFormat.PDF_417
    FORMAT_CODE_39 -> CodeFormat.CODE_39
    FORMAT_CODE_93 -> CodeFormat.CODE_93
    FORMAT_CODE_128 -> CodeFormat.CODE_128
    FORMAT_EAN_8 -> CodeFormat.EAN_8
    FORMAT_EAN_13 -> CodeFormat.EAN_13
    FORMAT_UPC_A -> CodeFormat.UPC_A
    FORMAT_UPC_E -> CodeFormat.UPC_E
    FORMAT_ITF -> CodeFormat.ITF
    FORMAT_CODABAR -> CodeFormat.CODABAR
    else -> CodeFormat.UNKNOWN
}

private fun Int.toCodeKind(): CodeKind = when (toCodeFormat()) {
    CodeFormat.QR_CODE,
    CodeFormat.AZTEC,
    CodeFormat.DATA_MATRIX,
    CodeFormat.PDF_417,
    -> CodeKind.QR

    else -> CodeKind.BARCODE
}

private fun BarcodeFormat.toGs1CodeFormat(): CodeFormat? = when (this) {
    BarcodeFormat.RSS_14 -> CodeFormat.GS1_DATABAR
    BarcodeFormat.RSS_EXPANDED -> CodeFormat.GS1_DATABAR_EXPANDED
    else -> null
}
