package org.ligi.passandroid.model;

import android.graphics.Bitmap;

import com.google.common.base.Optional;
import com.google.zxing.BarcodeFormat;

import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.helper.BarcodeHelper;
import org.ligi.tracedroid.logging.Log;

import java.io.Serializable;
import java.util.Locale;

public class BarCode implements Serializable {
    private final BarcodeFormat barcodeFormat;
    private final String barcodeMessage;
    private Optional<String> alternativeText;

    public BarCode(BarcodeFormat barcodeFormat, String barcodeMessage) {
        this.barcodeFormat = barcodeFormat;
        this.barcodeMessage = barcodeMessage;
        alternativeText = Optional.absent();
    }

    public Bitmap getBitmap(final int size) {
        if (barcodeMessage == null) {
            // no message -> no barcode
            Tracker.get().trackException("No Barcode in pass - strange", false);
            return null;
        }

        if (barcodeFormat == null) {
            Log.w("Barcode format is null - fallback to QR");
            Tracker.get().trackException("Barcode format is null - fallback to QR", false);
            BarcodeHelper.generateBarCodeBitmap(barcodeMessage, BarcodeFormat.QR_CODE, size);
        }

        return BarcodeHelper.generateBarCodeBitmap(barcodeMessage, barcodeFormat, size);

    }

    public BarcodeFormat getFormat() {
        return barcodeFormat;
    }

    public static BarcodeFormat getFormatFromString(String format) {
        if (format.contains("417")) {
            return BarcodeFormat.PDF_417;
        }

        if (format.toUpperCase(Locale.ENGLISH).contains("AZTEC")) {
            return BarcodeFormat.AZTEC;
        }

        return BarcodeFormat.QR_CODE;

    }

    public void setAlternativeText(final String alternativeText) {
        this.alternativeText = Optional.fromNullable(alternativeText);
    }

    public Optional<String> getAlternativeText() {
        return alternativeText;
    }

    public String getMessage() {
        return barcodeMessage;
    }

    public BarcodeFormat getBarcodeFormat() {
        return barcodeFormat;
    }
}
