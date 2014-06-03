package org.ligi.passandroid.model;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;

import org.joda.time.DateTime;

import java.util.List;

public interface Passbook {


    static final String[] TYPES = new String[]{"coupon", "eventTicket", "boardingPass", "generic", "storeCard"};

    String getDescription();

    String getType();

    PassFieldList getPrimaryFields();

    PassFieldList getSecondaryFields();

    PassFieldList getBackFields();

    PassFieldList getAuxiliaryFields();

    PassFieldList getHeaderFields();

    List<PassLocation> getLocations();

    boolean isValid();

    BarcodeFormat getBarcodeFormat();

    Bitmap getBarcodeBitmap(final int size);

    String getIconPath();

    Bitmap getIconBitmap();

    Bitmap getThumbnailImage();

    Bitmap getLogoBitmap();

    int getBackGroundColor();

    int getForegroundColor();

    boolean hasRelevantDate();

    DateTime getRelevantDate();

    String getId();

    String getPlainJsonString();
}
