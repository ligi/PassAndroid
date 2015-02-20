package org.ligi.passandroid.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import org.joda.time.DateTime;
import org.ligi.axt.AXT;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PassImpl implements FiledPass, Serializable {
    public static final String FNAME_ICON = "icon";
    public static final String FNAME_THUMBNAIL = "thumbnail";
    public static final String FNAME_STRIP = "strip";
    public static final String FNAME_LOGO = "logo";
    public static final String FILETYPE_IMAGES = ".png";

    private Optional<String> organisation = Optional.absent();
    private String type;
    private boolean valid = true; // be positive

    private Optional<BarCode> barCode = Optional.absent();

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

    private Optional<String> authToken = Optional.absent();
    private Optional<String> webServiceURL = Optional.absent();
    private Optional<String> serial = Optional.absent();

    public static final String[] TYPES = new String[]{"coupon", "eventTicket", "boardingPass", "generic", "storeCard"};
    private Optional<String> passTypeIdent = Optional.absent();

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


    private Optional<Bitmap> getBitmapFromFileNameString(String in) {
        return Optional.fromNullable(BitmapFactory.decodeFile(getPath() + "/" + in + PassImpl.FILETYPE_IMAGES));
    }

    public Optional<Bitmap> getIconBitmap() {
        return getBitmapFromFileNameString(FNAME_ICON);
    }

    public Optional<Bitmap> getThumbnailImage() {
        return getBitmapFromFileNameString(FNAME_THUMBNAIL);
    }

    @Override
    public Optional<Bitmap> getStripImage() {
        return getBitmapFromFileNameString(FNAME_STRIP);
    }

    @Override
    public Optional<String> getWebServiceURL() {
        return webServiceURL;
    }

    @Override
    public Optional<String> getAuthToken() {
        return authToken;
    }

    @Override
    public Optional<String> getSerial() {
        return serial;
    }

    @Override
    public Optional<String> getPassIdent() {
        return passTypeIdent;
    }

    public Optional<Bitmap> getLogoBitmap() {
        return getBitmapFromFileNameString(FNAME_LOGO);
    }


    public String getTypeNotNull() {
        if (type == null) {
            return "none";
        } else {
            return type;
        }
    }


    @Override
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

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void save(PassStore store) {
        final String jsonString = PassWriter.toJSON(this);

        path = store.getPathForID(getId());
        new File(path).mkdirs();
        final File file = new File(path, "data.json");
        store.deleteCache(getId());
        AXT.at(file).writeString(jsonString);

    }

    public void setInvalid() {
        valid = false;
    }

    public void setRelevantDate(Optional<DateTime> relevantDate) {
        this.relevantDate = relevantDate;
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

    @Override
    public Optional<BarCode> getBarCode() {
        return barCode;
    }

    public void setBarCode(BarCode barCode) {
        this.barCode = Optional.fromNullable(barCode);
    }

    public void setSerial(@NonNull Optional<String> serial) {
        this.serial = serial;
    }

    public void setAuthToken(@NonNull Optional<String> authToken) {
        this.authToken = authToken;
    }

    public void setWebserviceURL(@NonNull Optional<String> webServiceURL) {
        this.webServiceURL = webServiceURL;
    }

    public void setPassTypeIdent(@NonNull Optional<String> passTypeIdent) {
        this.passTypeIdent = passTypeIdent;
    }
}
