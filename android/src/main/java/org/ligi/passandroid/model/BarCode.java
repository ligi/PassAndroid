package org.ligi.passandroid.model;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;

import com.google.zxing.BarcodeFormat;

import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.helper.BarcodeHelper;
import org.ligi.tracedroid.logging.Log;

import java.io.Serializable;
import java.util.Locale;

import lombok.Data;

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

    public BitmapDrawable getBitmap(Resources resources) {
        if (message == null) {
            // no message -> no barcode
            Tracker.get().trackException("No Barcode in pass - strange", false);
            return null;
        }

        if (format == null) {
            Log.w("Barcode format is null - fallback to QR");
            Tracker.get().trackException("Barcode format is null - fallback to QR", false);
            return BarcodeHelper.generateBitmapDrawable(resources,message, BarcodeFormat.QR_CODE);
        }

        return BarcodeHelper.generateBitmapDrawable(resources, message, format);

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
