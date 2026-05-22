package com.domelabs.scanapp.core.scan

import androidx.compose.runtime.Composable

data class GeneratedCodeMatrix(
    val width: Int,
    val height: Int,
    val bits: BooleanArray,
)

interface CodeShareActions {
    fun shareText(text: String)
    fun shareImage(pngBytes: ByteArray, fileName: String)
}

@Composable
expect fun rememberCodeShareActions(): CodeShareActions

expect object ScanCodePlatform {
    fun generateMatrix(rawValue: String, codeFormat: String): GeneratedCodeMatrix?
    fun generatePng(rawValue: String, codeFormat: String, sizePx: Int): ByteArray?
}
