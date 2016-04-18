package org.ligi.passandroid.ui.edit

import com.google.zxing.oned.EAN13Writer

object EANHelper {
    fun getRandomEAN13(): String {
        val randomString = 0.rangeTo(11).map { "" + (Math.random() * 9).toInt() }.joinToString(separator = "") { it }
        return 0.rangeTo(9).map { randomString + it }.first() { isValidEAN13(it) }
    }

    fun isValidEAN13(payload: String): Boolean {
        try {
            EAN13Writer().encode(payload)
            return true
        } catch(e: IllegalArgumentException) {
            return false
        }
    }
}