package org.ligi.passandroid.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import org.joda.time.DateTime;

public interface Pass extends Serializable {

    String[] TYPES = new String[]{"generic", "coupon", "eventTicket", "boardingPass", "storeCard"};

    String getDescription();

    @Nullable
    String getType();

    @NonNull
    String getTypeNotNull();

    PassFieldList getPrimaryFields();

    PassFieldList getSecondaryFields();

    PassFieldList getBackFields();

    PassFieldList getAuxiliaryFields();

    PassFieldList getHeaderFields();

    List<PassLocation> getLocations();

    boolean isValid();

    int getBackgroundColor();

    int getForegroundColor();

    @Nullable
    DateTime getRelevantDate();

    @Nullable
    DateTime getExpirationDate();

    @Deprecated
    String getPath();

    String getId();

    @Nullable
    String getOrganisation();

    @Nullable
    String getSource();

    @Nullable
    BarCode getBarCode();

    @Nullable
    String getWebServiceURL();

    @Nullable
    String getAuthToken();

    @Nullable
    String getSerial();

    @Nullable
    String getPassIdent();

    @Nullable
    String getApp();

    @Nullable
    Bitmap getIconBitmap();

    @Nullable
    Bitmap getThumbnailImage();

    @Nullable
    Bitmap getStripBitmap();

    @Nullable
    Bitmap getLogoBitmap();


}
