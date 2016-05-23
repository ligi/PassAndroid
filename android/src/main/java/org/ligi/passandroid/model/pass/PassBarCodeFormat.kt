package org.ligi.passandroid.model.pass

import com.google.zxing.BarcodeFormat

enum class PassBarCodeFormat {
    PDF_417,
    AZTEC,
    CODE_39,
    CODE_128,
    QR_CODE,
    EAN_13;

    fun isQuadratic(): Boolean {
        when (this) {
            QR_CODE, AZTEC -> return true
            else -> return false
        }
    }

    fun zxingBarCodeFormat(): BarcodeFormat {
        when (this) {
            QR_CODE -> return BarcodeFormat.QR_CODE
            AZTEC -> return BarcodeFormat.AZTEC
            CODE_39 -> return BarcodeFormat.CODE_39
            CODE_128 -> return BarcodeFormat.CODE_128
            PDF_417 -> return BarcodeFormat.PDF_417
            EAN_13 -> return BarcodeFormat.EAN_13

            else -> return BarcodeFormat.QR_CODE
        }
    }
}
