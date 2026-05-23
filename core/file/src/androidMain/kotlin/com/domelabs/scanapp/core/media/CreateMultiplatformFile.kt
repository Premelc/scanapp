package com.domelabs.scanapp.core.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import androidx.core.net.toUri
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

actual fun createMultiplatformFile(path: String, mimeType: String): MultiplatformFile {
    return AndroidFileFromPath(
        path = path,
        contentType = mimeType
    ) as MultiplatformFile
}

actual suspend fun MediaItem.compress(): MediaItem {
    when (this) {
        is MediaItem.Image -> {
            val newUri = compressAndScaleImage(this.uri)
            return newUri?.let { this.copy(uri = it) } ?: this
        }

        is MediaItem.Video -> return this
    }
}


actual suspend fun compressAndScaleImage(
    imageUri: String,
    maxDimension: Int,
    compressQuality: Int,
): String? {
    val context = platformAndroidContext()

    return try {
        // Open input stream from URI
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri.toUri())
        val imageBytes = inputStream?.readBytes() ?: return null

        // Read EXIF rotation
        val exif = ExifInterface(ByteArrayInputStream(imageBytes))
        val rotation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        // Decode bitmap
        val originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return null

        // Apply rotation
        val rotatedBitmap = when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(originalBitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(originalBitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(originalBitmap, 270f)
            else -> originalBitmap
        }

        // Scale down if needed
        val scaledBitmap = scaleDownBitmap(rotatedBitmap, maxDimension)
        val sharedImagesDir = File(context.cacheDir, "shared_images").apply {
            if (!exists()) mkdirs()
        }

        val compressedFile = File(sharedImagesDir, "compressed_${System.currentTimeMillis()}.jpg")

        compressedFile.outputStream().use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, out)
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            compressedFile
        ).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
private fun scaleDownBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val largestSide = maxOf(width, height)
    if (largestSide <= maxDimension) return bitmap

    val scale = maxDimension.toFloat() / largestSide
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    return bitmap.scale(newWidth, newHeight)
}