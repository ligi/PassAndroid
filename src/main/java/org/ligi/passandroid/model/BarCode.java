package org.ligi.passandroid.model;

import android.graphics.Bitmap;

import android.support.annotation.Nullable;
import com.google.zxing.BarcodeFormat;

import lombok.Data;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.helper.BarcodeHelper;
import org.ligi.tracedroid.logging.Log;

import java.io.Serializable;
import java.util.Locale;

@Data
public class BarCode implements Serializable {
    private final BarcodeFormat format;
    private final String message;

    @Nullable
    private String alternativeText;

    public BarCode(BarcodeFormat format, String message) {
        this.format = format;
        this.message = message;
    }

    public Bitmap getBitmap(final int size) {
        if (message == null) {
            // no message -> no barcode
            Tracker.get().trackException("No Barcode in pass - strange", false);
            return null;
        }

        if (format == null) {
            Log.w("Barcode format is null - fallback to QR");
            Tracker.get().trackException("Barcode format is null - fallback to QR", false);
            return BarcodeHelper.generateBarCodeBitmap(message, BarcodeFormat.QR_CODE, size);
        }

        return BarcodeHelper.generateBarCodeBitmap(message, format, size);

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

}
