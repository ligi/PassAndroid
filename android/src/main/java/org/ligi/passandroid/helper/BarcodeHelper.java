package org.ligi.passandroid.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.ligi.passandroid.model.pass.PassBarCodeFormat;
import org.ligi.tracedroid.logging.Log;

public class BarcodeHelper {

    @Nullable
    public static BitmapDrawable generateBitmapDrawable(@NonNull Resources resources, @NonNull String data, @NonNull PassBarCodeFormat type) {
        final Bitmap bitmap = generateBarCodeBitmap(data, type);
        if (bitmap == null) {
            return null;
        }

        final BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
        bitmapDrawable.setFilterBitmap(false);
        return bitmapDrawable;
    }

    @Nullable
    public static Bitmap generateBarCodeBitmap(@NonNull String data, @NonNull PassBarCodeFormat type) {

        if (data.isEmpty()) {
            return null;
        }

        try {
            final BitMatrix matrix = getBitMatrix(data, type);

            // generate an image from the byte matrix
            final int width = matrix.getWidth();

            final int height;
            final boolean is1D = matrix.getHeight() == 1;
            if (is1D) {
                height = width / 5;
            } else {
                height = matrix.getHeight();
            }


            // create buffered image to draw to
            // NTFS Bitmap.Config.ALPHA_8 sounds like an awesome idea - been there - done that ..
            final Bitmap barcode_image = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            // iterate through the matrix and draw the pixels to the image
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    barcode_image.setPixel(x, y, matrix.get(x, is1D ? 0 : y) ? 0 : 0xFFFFFF);
                }
            }

            return barcode_image;
        } catch (com.google.zxing.WriterException | IllegalArgumentException e) {
            Log.w("could not write image " + e);
            // TODO check if we should better return some rescue Image here
            return null;
        }

    }

    public static BitMatrix getBitMatrix(String data, PassBarCodeFormat type) throws WriterException {
        final Writer writer = new MultiFormatWriter();
        return writer.encode(data, getZxingBarCodeFormat(type), 0, 0);
    }

    public static boolean isBarcodeFormatQuadratic(PassBarCodeFormat format) {
        switch (format) {
            case QR_CODE:
            case AZTEC:
                return true;

            default:
                return false;
        }
    }

    public static BarcodeFormat getZxingBarCodeFormat(PassBarCodeFormat format) {
        switch (format) {
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
