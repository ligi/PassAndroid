package org.ligi.passandroid.model;

import android.graphics.Bitmap;

import com.google.common.base.Optional;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

public interface Pass extends Serializable {

    static final String[] TYPES = new String[]{"generic", "coupon", "eventTicket", "boardingPass", "storeCard"};

    String getDescription();

    String getType();

    String getTypeNotNull();

    PassFieldList getPrimaryFields();

    PassFieldList getSecondaryFields();

    PassFieldList getBackFields();

    PassFieldList getAuxiliaryFields();

    PassFieldList getHeaderFields();

    List<PassLocation> getLocations();

    boolean isValid();

    Optional<Bitmap> getIconBitmap();

    Optional<Bitmap> getThumbnailImage();

    Optional<Bitmap> getLogoBitmap();

    int getBackGroundColor();

    int getForegroundColor();

    Optional<DateTime> getRelevantDate();

    Optional<DateTime> getExpirationDate();

    String getPath();

    String getId();

    Optional<String> getOrganisation();

    Optional<String> getSource();

    Optional<BarCode> getBarCode();

    Optional<Bitmap> getStripImage();

    Optional<String> getWebServiceURL();

    Optional<String> getAuthToken();

    Optional<String> getSerial();

    Optional<String> getPassIdent();

}
