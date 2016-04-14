package org.ligi.passandroid.model.pass;

import com.google.zxing.BarcodeFormat;

public enum PassBarCodeFormat {
    PDF_417,
    AZTEC,
    CODE_39,
    CODE_128,
    QR_CODE;

    public boolean isQuadratic() {
        switch (this) {
            case QR_CODE:
            case AZTEC:
                return true;

            default:
                return false;
        }
    }

    public BarcodeFormat getZxingBarCodeFormat() {
        switch (this) {
            case QR_CODE:
                return BarcodeFormat.QR_CODE;
            case AZTEC:
                return BarcodeFormat.AZTEC;
            case CODE_39:
                return BarcodeFormat.CODE_39;
            case CODE_128:
                return BarcodeFormat.CODE_128;
            case PDF_417:
                return BarcodeFormat.PDF_417;

            default:
                return BarcodeFormat.QR_CODE;
        }
    }
}
