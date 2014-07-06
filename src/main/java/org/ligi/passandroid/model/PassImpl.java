package org.ligi.passandroid.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.base.Optional;
import com.google.zxing.BarcodeFormat;

import org.joda.time.DateTime;
import org.ligi.axt.AXT;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.helper.BarcodeHelper;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PassImpl implements Pass, Serializable {
    private Optional<String> organisation = Optional.absent();
    private String type;
    private boolean valid = true; // be positive
    private String barcodeMessage;
    private BarcodeFormat barcodeFormat;
    private int backGroundColor;
    private int foregroundColor;
    private String description;
    private Optional<DateTime> relevantDate = Optional.absent();
    private Optional<DateTime> expirationDate = Optional.absent();
    private PassFieldList primaryFields = new PassFieldList();
    private PassFieldList backFields = new PassFieldList();
    private PassFieldList secondaryFields = new PassFieldList();
    private PassFieldList auxiliaryFields = new PassFieldList();
    private PassFieldList headerFields = new PassFieldList();
    private List<PassLocation> locations = new ArrayList<>();
    private String path;
    private String id;

    public static final String[] TYPES = new String[]{"coupon", "eventTicket", "boardingPass", "generic", "storeCard"};

    @Override
    public Optional<String> getOrganisation() {
        return organisation;
    }

    @Override
    public int getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public Optional<DateTime> getRelevantDate() {
        return relevantDate;
    }

    @Override
    public Optional<DateTime> getExpirationDate() {
        return expirationDate;
    }

    @Override
    public int getBackGroundColor() {
        return backGroundColor;
    }


    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public BarcodeFormat getBarcodeFormat() {
        return barcodeFormat;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public PassFieldList getPrimaryFields() {
        return primaryFields;
    }

    @Override
    public PassFieldList getSecondaryFields() {
        return secondaryFields;
    }

    @Override
    public PassFieldList getBackFields() {
        return backFields;
    }

    @Override
    public PassFieldList getAuxiliaryFields() {
        return auxiliaryFields;
    }

    @Override
    public PassFieldList getHeaderFields() {
        return headerFields;
    }

    @Override
    public List<PassLocation> getLocations() {
        return locations;
    }

    @Override
    public String getDescription() {
        if (description == null) {
            return ""; // better way of returning no description - so we can avoid optional / null checks and it is kind of the same thing
            // an empty description - we can do kind of all String operations safely this way and do not have to care about the existence of a real description
            // if we want to know if one is there we can check length for being 0 still ( which we would have to do anyway for empty descriptions )
            // See no way at the moment where we would have to distinguish between an empty and an missing description
        }
        return description;
    }


    public Bitmap getIconBitmap() {
        Bitmap result = null;

        if (path != null) {

            // first we try to fetch the small icon
            result = BitmapFactory.decodeFile(path + "/icon@2x.png");

            // if that failed we use the small one
            if (result == null) {
                result = BitmapFactory.decodeFile(path + "/icon.png");
            }


        }
        return result;
    }

    public Bitmap getThumbnailImage() {
        Bitmap result = null;

        if (path != null) {

            // first we try to fetch the small icon
            result = BitmapFactory.decodeFile(path + "/thumbnail@2x.png");

            // if that failed we use the small one
            if (result == null) {
                result = BitmapFactory.decodeFile(path + "/thumbnail.png");
            }


        }
        return result;
    }

    public Bitmap getLogoBitmap() {
        Bitmap result = null;

        if (path != null) {
            result = BitmapFactory.decodeFile(path + "/logo@2x.png");

            if (result == null) {
                result = BitmapFactory.decodeFile(path + "/logo.png");
            }

        }
        return result;
    }

    public Bitmap getBarcodeBitmap(final int size) {
        if (barcodeMessage == null) {
            // no message -> no barcode
            Tracker.get().trackException("No Barcode in pass - strange", false);
            return null;
        }

        if (barcodeFormat == null) {
            Log.w("Barcode format is null - fallback to QR");
            Tracker.get().trackException("Barcode format is null - fallback to QR", false);
            BarcodeHelper.generateBarCodeBitmap(barcodeMessage, BarcodeFormat.QR_CODE, size);
        }

        return BarcodeHelper.generateBarCodeBitmap(barcodeMessage, barcodeFormat, size);

    }


    public String getTypeNotNull() {
        if (type == null) {
            return "none";
        } else {
            return type;
        }
    }


    public String getPath() {
        return path;
    }

    @Override
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setInvalid() {
        valid = false;
    }

    public void setBarcodeFormat(BarcodeFormat barcodeFormat) {
        this.barcodeFormat = barcodeFormat;
    }

    public void setBarcodeMessage(String barcodeMessage) {
        this.barcodeMessage = barcodeMessage;
    }

    public void setRelevantDate(Optional<DateTime> relevantDaterele) {
        this.relevantDate = relevantDaterele;
    }

    public void setExpirationDate(Optional<DateTime> expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setBackgroundColor(final int backgroundColor) {
        this.backGroundColor = backgroundColor;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOrganization(Optional<String> organization) {
        this.organisation = organization;
    }

    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setLocations(List<PassLocation> locations) {
        this.locations = locations;
    }

    public void setPrimaryFields(PassFieldList primaryFields) {
        this.primaryFields = primaryFields;
    }

    public void setSecondaryFields(PassFieldList secondaryFields) {
        this.secondaryFields = secondaryFields;
    }

    public void setAuxiliaryFields(PassFieldList auxiliaryFields) {
        this.auxiliaryFields = auxiliaryFields;
    }

    public void setHeaderFields(PassFieldList headerFields) {
        this.headerFields = headerFields;
    }

    public void setBackFields(PassFieldList backFields) {
        this.backFields = backFields;
    }

    @Override
    public Optional<String> getSource() {
        final File file = new File(getPath() + "/source.obj");
        if (file.exists()) {
            try {
                return Optional.of((String) AXT.at(file).loadToObject());
            } catch (IOException | ClassNotFoundException e) {
                // OK no source info - no big deal - but log
                Log.w("pass source info file exists but not readable" + e);
            }
        }
        return Optional.absent();
    }
}
