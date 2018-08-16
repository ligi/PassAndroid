package org.ligi.passandroid.ui.edit

import com.google.zxing.oned.EAN13Writer
import com.google.zxing.oned.EAN8Writer
import com.google.zxing.oned.UPCEANWriter

fun getRandomEAN13(): String {
    val randomString = (0..11).map { (Math.random() * 9).toInt() }.joinToString(separator = "")
    return (0..9).map { randomString + it }.first(::isValidEAN13)
}

fun isValidEAN13(payload: String) = isValidEAN(payload, EAN13Writer())

fun getRandomEAN8(): String {
    val randomString = (0..6).map { (Math.random() * 9).toInt() }.joinToString(separator = "")
    return (0..9).map { randomString + it }.first(::isValidEAN8)
}

fun isValidEAN8(payload: String) = isValidEAN(payload, EAN8Writer())

fun isValidEAN(payload: String, writer: UPCEANWriter) = try {
    writer.encode(payload)
    true
} catch(e: IllegalArgumentException) {
    false
}