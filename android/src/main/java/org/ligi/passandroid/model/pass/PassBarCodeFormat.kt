package org.ligi.passandroid.model.pass

import com.google.zxing.BarcodeFormat

enum class PassBarCodeFormat {

    AZTEC,
    CODE_39,
    CODE_128,
    DATA_MATRIX,
    EAN_13,
    ITF,
    PDF_417,
    QR_CODE;

    fun isQuadratic() = when (this) {
        QR_CODE, AZTEC -> true
        else -> false
    }

    fun zxingBarCodeFormat() = when (this) {
        AZTEC -> BarcodeFormat.AZTEC
        CODE_39 -> BarcodeFormat.CODE_39
        CODE_128 -> BarcodeFormat.CODE_128
        DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
        EAN_13 -> BarcodeFormat.EAN_13
        ITF -> BarcodeFormat.ITF
        PDF_417 -> BarcodeFormat.PDF_417
        QR_CODE -> BarcodeFormat.QR_CODE

        else -> BarcodeFormat.QR_CODE
    }
}
