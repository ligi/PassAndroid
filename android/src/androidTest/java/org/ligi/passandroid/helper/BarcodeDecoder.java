package org.ligi.passandroid.helper;

import android.graphics.Bitmap;

import android.support.annotation.Nullable;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public class BarcodeDecoder {

    @Nullable
    public static String decodeBitmap(Bitmap bMap) {
        final int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        final LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        final Reader reader = new MultiFormatReader();// use this otherwise ChecksumException
        try {
            final Result result = reader.decode(bitmap);
            return result.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

}
