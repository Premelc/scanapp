package com.domelabs.scanapp.core.utils

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToLong

fun Float.toRadians(): Float =
    (this * PI / 180f).toFloat()

fun Double.toRadians(): Float =
    (this * PI / 180f).toFloat()

fun hectaresToAreaString(hectares: Double): String {
    val squareMeters = hectares * 10_000

    return if (squareMeters >= 1_000_000) {
        val km2 = squareMeters / 1_000_000
        "${km2.format(2)} km²"
    } else {
        "${squareMeters.format(2)} m²"
    }
}

fun Double.format(decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = (this * factor).roundToLong() / factor
    return rounded.toString().let {
        if (decimals == 0) it.substringBefore(".")
        else it
    }
}

fun roundUpToNearestFive(value: Int): Int {
    return ((value + 4) / 5) * 5
}

fun Double.roundTo(decimals: Int): String {
    val multiplier = buildString {
        append(1)
        repeat(decimals) { append(0) }
    }.toDoubleOrNull() ?: 100.0
    val result = round(this * multiplier) / multiplier
    return result.toString()
}
