package org.ligi.passandroid.injections;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;

import org.joda.time.DateTime;
import org.ligi.passandroid.helper.BarcodeHelper;
import org.ligi.passandroid.model.PassFieldList;
import org.ligi.passandroid.model.PassLocation;
import org.ligi.passandroid.model.Passbook;

import java.util.ArrayList;
import java.util.List;

public class FixedPassBook implements Passbook {

    public DateTime relevantDate;
    public BarcodeFormat barcodeFormat = BarcodeFormat.AZTEC;

    @Override
    public String getDescription() {
        return "desc";
    }

    @Override
    public String getType() {
        return "type";
    }

    @Override
    public PassFieldList getPrimaryFields() {
        return new PassFieldList();
    }

    @Override
    public PassFieldList getSecondaryFields() {
        return new PassFieldList();
    }

    @Override
    public PassFieldList getBackFields() {
        return new PassFieldList();
    }

    @Override
    public PassFieldList getAuxiliaryFields() {
        return new PassFieldList();
    }

    @Override
    public PassFieldList getHeaderFields() {
        return new PassFieldList();
    }

    @Override
    public List<PassLocation> getLocations() {
        return new ArrayList<>();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public BarcodeFormat getBarcodeFormat() {
        return barcodeFormat;
    }

    @Override
    public Bitmap getBarcodeBitmap(int size) {
        return BarcodeHelper.generateBarCodeBitmap("foo", getBarcodeFormat(), size);
    }

    @Override
    public String getIconPath() {
        return null;
    }

    @Override
    public Bitmap getIconBitmap() {
        return null;
    }

    @Override
    public Bitmap getThumbnailImage() {
        return null;
    }

    @Override
    public Bitmap getLogoBitmap() {
        return null;
    }

    @Override
    public int getBackGroundColor() {
        return 0;
    }

    @Override
    public int getForegroundColor() {
        return 0;
    }

    @Override
    public boolean hasRelevantDate() {
        return relevantDate != null;
    }

    @Override
    public DateTime getRelevantDate() {
        return relevantDate;
    }

    @Override
    public String getId() {
        return "ABC";
    }

    @Override
    public String getPlainJsonString() {
        return null;
    }
}
