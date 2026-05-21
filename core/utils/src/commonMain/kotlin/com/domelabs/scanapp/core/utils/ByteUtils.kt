package com.domelabs.scanapp.core.utils

fun Byte.toUnsignedInt(): Int = this.toInt() and 0xFF
