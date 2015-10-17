package org.ligi.passandroid.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import org.joda.time.DateTime;

public interface Pass extends Serializable {

    String BITMAP_ICON = "icon";
    String BITMAP_THUMBNAIL = "thumbnail";
    String BITMAP_STRIP = "strip";
    String BITMAP_LOGO = "logo";
    String BITMAP_FOOTER = "footer";


    @StringDef({BITMAP_ICON, BITMAP_THUMBNAIL, BITMAP_STRIP, BITMAP_LOGO, BITMAP_FOOTER})
    @Retention(RetentionPolicy.SOURCE)
    @interface PassBitmap {
    }


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
    // change to Bitmap getBitmap(@PassBitmap String passBitmap);
    // once this is resolved https://code.google.com/p/android/issues/detail?id=175532
    Bitmap getBitmap(String passBitmap);

}
