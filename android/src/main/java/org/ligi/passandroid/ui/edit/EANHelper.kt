package org.ligi.passandroid.ui.edit

import com.google.zxing.oned.EAN13Writer

fun getRandomEAN13(): String {
    val randomString = 0.rangeTo(11).map { "" + (Math.random() * 9).toInt() }.joinToString(separator = "") { it }
    return 0.rangeTo(9).map { randomString + it }.first(::isValidEAN13)
}

fun isValidEAN13(payload: String) = try {
    EAN13Writer().encode(payload)
    true
} catch(e: IllegalArgumentException) {
    false
}