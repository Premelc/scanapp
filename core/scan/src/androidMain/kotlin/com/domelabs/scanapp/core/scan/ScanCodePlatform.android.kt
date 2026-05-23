package com.domelabs.scanapp.core.scan

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import qrgenerator.generateCode
import java.io.ByteArrayOutputStream
import java.io.File

private class AndroidCodeShareActions(
    private val context: android.content.Context,
) : CodeShareActions {
    override fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share code text"))
    }

    override fun shareImage(pngBytes: ByteArray, fileName: String) {
        val file = File(context.cacheDir, fileName)
        file.writeBytes(pngBytes)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share code image"))
    }
}

@Composable
actual fun rememberCodeShareActions(): CodeShareActions {
    val context = LocalContext.current
    return AndroidCodeShareActions(context)
}

actual object ScanCodePlatform {
    actual fun generateMatrix(rawValue: String, codeFormat: String): GeneratedCodeMatrix? {
        if (codeFormat == "QR_CODE") {
            return runCatching {
                generateQrKitBitmap(rawValue).toCodeMatrix()
            }.getOrNull()
        }
        val format = codeFormat.toBarcodeFormat() ?: return null
        val dimensions = dimensionsFor(format)
        return runCatching {
            val matrix = MultiFormatWriter().encode(
                rawValue,
                format,
                dimensions.first,
                dimensions.second,
            )
            matrix.toCodeMatrix()
        }.getOrNull()
    }

    actual fun generatePng(rawValue: String, codeFormat: String, sizePx: Int): ByteArray? {
        if (codeFormat == "QR_CODE") {
            return runCatching {
                generateQrKitBitmap(rawValue).toScaledPngBytes(sizePx = sizePx)
            }.getOrNull()
        }
        val format = codeFormat.toBarcodeFormat() ?: return null
        val dimensions = dimensionsFor(format, sizePx)
        return runCatching {
            val matrix = MultiFormatWriter().encode(
                rawValue,
                format,
                dimensions.first,
                dimensions.second,
            )
            matrix.toPngBytes()
        }.getOrNull()
    }
}

private fun generateQrKitBitmap(rawValue: String): Bitmap {
    return requireNotNull(generateCode(rawValue)) {
        "qr-kit failed to generate QR image"
    }.asAndroidBitmap()
}

private fun String.toBarcodeFormat(): BarcodeFormat? = when (this) {
    "QR_CODE" -> BarcodeFormat.QR_CODE
    "AZTEC" -> BarcodeFormat.AZTEC
    "DATA_MATRIX" -> BarcodeFormat.DATA_MATRIX
    "PDF_417" -> BarcodeFormat.PDF_417
    "GS1_DATABAR" -> BarcodeFormat.RSS_14
    "GS1_DATABAR_EXPANDED" -> BarcodeFormat.RSS_EXPANDED
    "CODE_39" -> BarcodeFormat.CODE_39
    "CODE_93" -> BarcodeFormat.CODE_93
    "CODE_128" -> BarcodeFormat.CODE_128
    "EAN_8" -> BarcodeFormat.EAN_8
    "EAN_13" -> BarcodeFormat.EAN_13
    "UPC_A" -> BarcodeFormat.UPC_A
    "UPC_E" -> BarcodeFormat.UPC_E
    "ITF" -> BarcodeFormat.ITF
    "CODABAR" -> BarcodeFormat.CODABAR
    else -> null
}

private fun dimensionsFor(format: BarcodeFormat, baseSize: Int = 840): Pair<Int, Int> = when (format) {
    BarcodeFormat.QR_CODE,
    BarcodeFormat.AZTEC,
    BarcodeFormat.DATA_MATRIX,
    -> baseSize to baseSize

    else -> baseSize to (baseSize * 0.42f).toInt()
}

private fun BitMatrix.toCodeMatrix(): GeneratedCodeMatrix {
    val payload = BooleanArray(width * height)
    for (y in 0 until height) {
        for (x in 0 until width) {
            payload[y * width + x] = get(x, y)
        }
    }
    return GeneratedCodeMatrix(width = width, height = height, bits = payload)
}

private fun BitMatrix.toPngBytes(): ByteArray {
    val pixels = IntArray(width * height)
    for (y in 0 until height) {
        for (x in 0 until width) {
            pixels[y * width + x] = if (get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        }
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

private fun Bitmap.toCodeMatrix(): GeneratedCodeMatrix {
    val widthPx = width
    val heightPx = height
    val payload = BooleanArray(widthPx * heightPx)
    val pixels = IntArray(widthPx * heightPx)
    getPixels(pixels, 0, widthPx, 0, 0, widthPx, heightPx)
    for (y in 0 until heightPx) {
        for (x in 0 until widthPx) {
            val pixel = pixels[y * widthPx + x]
            val red = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF
            val blue = pixel and 0xFF
            val luminance = ((red * 299) + (green * 587) + (blue * 114)) / 1000
            payload[y * widthPx + x] = luminance < 128
        }
    }
    return GeneratedCodeMatrix(
        width = widthPx,
        height = heightPx,
        bits = payload,
    )
}

private fun Bitmap.toScaledPngBytes(sizePx: Int): ByteArray {
    val targetSize = sizePx.coerceAtLeast(width.coerceAtLeast(height))
    val bitmap = Bitmap.createScaledBitmap(this, targetSize, targetSize, false)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
