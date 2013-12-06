package org.ligi.passandroid.helper;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class BarcodeHelper {

    public static Bitmap generateBarCodeBitmap(String dataNotNull, BarcodeFormat typeNotNull, int size) {

        if (dataNotNull == null) {
            throw new IllegalArgumentException("date must not be null");
        }

        if (typeNotNull == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        try {
            final BitMatrix matrix = getBitMatrix(dataNotNull, typeNotNull, size);

            // generate an image from the byte matrix
            int width = matrix.getWidth();
            int height = matrix.getHeight();

            // create buffered image to draw to
            // NTFS Bitmap.Config.ALPHA_8 sounds like an awesome idea - been there - done that ..
            final Bitmap barcode_image = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            // iterate through the matrix and draw the pixels to the image
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    barcode_image.setPixel(x, y, matrix.get(x, y) ? 0 : 0xFFFFFF);
                }
            }

            return barcode_image;
        } catch (com.google.zxing.WriterException e) {
            // TODO check if we should better return some rescue Image here
            return null;
        }

    }

    public static BitMatrix getBitMatrix(String data, BarcodeFormat type, int size) throws WriterException {
        final Writer writer = new MultiFormatWriter();
        return writer.encode(data, type, size, size);
    }
}
