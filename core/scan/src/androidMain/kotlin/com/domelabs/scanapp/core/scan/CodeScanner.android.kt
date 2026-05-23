package com.domelabs.scanapp.core.scan

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.PlanarYUVLuminanceSource
import java.util.concurrent.Executors

private const val SCAN_LOG_TAG = "ScanCodeScanner"

@Composable
actual fun CodeScanner(
    onDetected: (ScannedCode) -> Unit,
    onError: (ScanError) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    flashEnabled: Boolean,
    cooldownMillis: Long,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var boundCamera by remember { mutableStateOf<Camera?>(null) }
    var lastDetectedAt by remember { mutableLongStateOf(0L) }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    DisposableEffect(enabled, lifecycleOwner) {
        if (!enabled) {
            runCatching {
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            }
            onDispose { }
        } else {
            val providerFuture = ProcessCameraProvider.getInstance(context)
            val scanner = BarcodeScanning.getClient()
            val dataBarReader = MultiFormatReader().apply {
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
            val listener = Runnable {
                val provider = runCatching { providerFuture.get() }.getOrNull()
                if (provider == null) {
                    onError(ScanError.CameraUnavailable)
                    return@Runnable
                }

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    processFrame(
                        imageProxy = imageProxy,
                        scanner = scanner,
                        dataBarReader = dataBarReader,
                        cooldownMillis = cooldownMillis,
                        lastDetectedAt = lastDetectedAt,
                        onDetectedTimestampUpdate = { lastDetectedAt = it },
                        onDetected = onDetected,
                        onError = onError,
                    )
                }

                try {
                    provider.unbindAll()
                    boundCamera = provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis,
                    )
                } catch (_: Exception) {
                    onError(ScanError.CameraUnavailable)
                }
            }
            providerFuture.addListener(listener, ContextCompat.getMainExecutor(context))
            onDispose {
                runCatching {
                    ProcessCameraProvider.getInstance(context).get().unbindAll()
                }
            }
        }
    }

    LaunchedEffect(boundCamera, flashEnabled, enabled) {
        if (!enabled) return@LaunchedEffect
        runCatching { boundCamera?.cameraControl?.enableTorch(flashEnabled) }
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView },
    )
}

@SuppressLint("UnsafeOptInUsageError")
private fun processFrame(
    imageProxy: androidx.camera.core.ImageProxy,
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    dataBarReader: MultiFormatReader,
    cooldownMillis: Long,
    lastDetectedAt: Long,
    onDetectedTimestampUpdate: (Long) -> Unit,
    onDetected: (ScannedCode) -> Unit,
    onError: (ScanError) -> Unit,
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            val mlkitAccepted = if (barcodes.isNotEmpty()) {
                val barcode = barcodes.firstOrNull()
                if (barcode == null) {
                    false
                } else {
                    val format = barcode.format.toCodeFormat()
                    val kind = barcode.format.toCodeKind()
                    val rawValue = barcode.rawValue
                    val rawBytesLength = barcode.rawBytes?.size ?: 0

                    Log.d(
                        SCAN_LOG_TAG,
                        "Detected barcode format=$format kind=$kind rawValuePresent=${!rawValue.isNullOrBlank()} rawBytes=$rawBytesLength",
                    )

                    if (rawValue.isNullOrBlank()) {
                        Log.w(
                            SCAN_LOG_TAG,
                            "Dropping MLKit detection because rawValue is null/blank for format=$format (rawBytes=$rawBytesLength)",
                        )
                        false
                    } else {
                        val now = System.currentTimeMillis()
                        if (now - lastDetectedAt < cooldownMillis) {
                            Log.d(
                                SCAN_LOG_TAG,
                                "Cooldown drop for format=$format elapsed=${now - lastDetectedAt}ms cooldown=${cooldownMillis}ms",
                            )
                            true
                        } else {
                            onDetectedTimestampUpdate(now)
                            Log.d(
                                SCAN_LOG_TAG,
                                "Accepted detection format=$format kind=$kind valueLength=${rawValue.length}",
                            )
                            onDetected(
                                ScannedCode(
                                    kind = kind,
                                    format = format,
                                    rawValue = rawValue,
                                )
                            )
                            true
                        }
                    }
                }
            } else {
                false
            }

            if (mlkitAccepted) return@addOnSuccessListener

            val dataBarResult = imageProxy.tryDecodeGs1DataBar(dataBarReader)
            if (dataBarResult != null) {
                val (format, rawValue) = dataBarResult
                val now = System.currentTimeMillis()
                if (now - lastDetectedAt < cooldownMillis) {
                    Log.d(
                        SCAN_LOG_TAG,
                        "Cooldown drop for ZXing GS1 format=$format elapsed=${now - lastDetectedAt}ms cooldown=${cooldownMillis}ms",
                    )
                    return@addOnSuccessListener
                }
                onDetectedTimestampUpdate(now)
                Log.d(
                    SCAN_LOG_TAG,
                    "Accepted ZXing GS1 detection format=$format valueLength=${rawValue.length}",
                )
                onDetected(
                    ScannedCode(
                        kind = CodeKind.BARCODE,
                        format = format,
                        rawValue = rawValue,
                    )
                )
            }
        }
        .addOnFailureListener {
            Log.e(SCAN_LOG_TAG, "MLKit scan failed: ${it.message}", it)
            onError(ScanError.AnalysisFailed(it.message))
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

private fun androidx.camera.core.ImageProxy.tryDecodeGs1DataBar(
    dataBarReader: MultiFormatReader,
): Pair<CodeFormat, String>? {
    return runCatching {
        val yPlane = planes.firstOrNull() ?: return null
        val yBuffer = yPlane.buffer
        val yBytes = ByteArray(yBuffer.remaining())
        yBuffer.get(yBytes)
        val width = width
        val height = height

        var source: LuminanceSource = PlanarYUVLuminanceSource(
            yBytes,
            width,
            height,
            0,
            0,
            width,
            height,
            false,
        )

        val rotationTurns = ((imageInfo.rotationDegrees % 360) + 360) % 360 / 90
        repeat(rotationTurns) {
            if (source.isRotateSupported) {
                source = source.rotateCounterClockwise()
            }
        }

        val result = dataBarReader.decodeWithState(BinaryBitmap(HybridBinarizer(source)))
        dataBarReader.reset()

        val format = result.barcodeFormat.toGs1CodeFormat() ?: return null
        val value = result.text.orEmpty()
        if (value.isBlank()) return null
        format to value
    }.onFailure {
        dataBarReader.reset()
    }.getOrNull()
}

private fun BarcodeFormat.toGs1CodeFormat(): CodeFormat? = when (this) {
    BarcodeFormat.RSS_14 -> CodeFormat.GS1_DATABAR
    BarcodeFormat.RSS_EXPANDED -> CodeFormat.GS1_DATABAR_EXPANDED
    else -> null
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
