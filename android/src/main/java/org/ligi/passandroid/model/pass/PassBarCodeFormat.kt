package org.ligi.passandroid.model.pass

import com.google.zxing.BarcodeFormat

enum class PassBarCodeFormat {
    PDF_417,
    AZTEC,
    CODE_39,
    CODE_128,
    QR_CODE,
    EAN_13,
    ITF;

    fun isQuadratic() = when (this) {
        QR_CODE, AZTEC -> true
        else -> false
    }

    fun zxingBarCodeFormat() = when (this) {
        QR_CODE -> BarcodeFormat.QR_CODE
        AZTEC -> BarcodeFormat.AZTEC
        CODE_39 -> BarcodeFormat.CODE_39
        CODE_128 -> BarcodeFormat.CODE_128
        PDF_417 -> BarcodeFormat.PDF_417
        EAN_13 -> BarcodeFormat.EAN_13
        ITF -> BarcodeFormat.ITF

        else -> BarcodeFormat.QR_CODE
    }
}
