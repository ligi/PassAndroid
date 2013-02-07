package org.ligi.ticketviewer.helper;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.encoder.PDF417Writer;
import com.google.zxing.qrcode.QRCodeWriter;

public class BarcodeHelper {
    public static Bitmap generateBarCode(String data, com.google.zxing.BarcodeFormat type) {
        Bitmap barcode_image;
        // get a byte matrix for the data
        BitMatrix matrix;
        com.google.zxing.Writer writer = null;

        switch (type) {
            case QR_CODE:
                writer = new QRCodeWriter();
                break;
            case PDF_417:
                writer = new PDF417Writer();
                break;
            default:
                writer = new QRCodeWriter();
                type = com.google.zxing.BarcodeFormat.QR_CODE;
                Log.w("Barcode", "invalid barcode - fallback to QR");
                break;
        }

        try {
            matrix = writer.encode(data, type, 512, 512);
        } catch (com.google.zxing.WriterException e) {
            // exit the method
            return null;
        }

        // generate an image from the byte matrix
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        // create buffered image to draw to
        barcode_image = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        // iterate through the matrix and draw the pixels to the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (matrix.get(x, y))
                    barcode_image.setPixel(x, y, 0);
                else
                    barcode_image.setPixel(x, y, 0xFFFFFF);

            }
        }

        return barcode_image;
    }

}
