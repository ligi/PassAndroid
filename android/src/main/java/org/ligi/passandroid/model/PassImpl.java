package org.ligi.passandroid.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.joda.time.DateTime;
import org.ligi.axt.AXT;
import org.ligi.tracedroid.logging.Log;

@Data
public class PassImpl implements FiledPass, Serializable {

    public static final String FILETYPE_IMAGES = ".png";

    private String organisation = null;
    private String type;
    private boolean valid = true; // be positive

    @Nullable
    private BarCode barCode;

    private int backgroundColor;
    private int foregroundColor;
    private String description;

    @Nullable
    private DateTime relevantDate;

    @Nullable
    private DateTime expirationDate;

    private PassFieldList primaryFields = new PassFieldList();
    private PassFieldList backFields = new PassFieldList();
    private PassFieldList secondaryFields = new PassFieldList();
    private PassFieldList auxiliaryFields = new PassFieldList();
    private PassFieldList headerFields = new PassFieldList();
    private List<PassLocation> locations = new ArrayList<>();
    private String path;
    private String id;

    private String app;

    @Nullable
    private String authToken;

    @Nullable
    private String webServiceURL;

    @Nullable
    private String serial;

    public static final String[] TYPES = new String[]{"coupon", "eventTicket", "boardingPass", "generic", "storeCard"};

    @Nullable
    private String passIdent;

    @Override
    public boolean isValid() {
        return valid;
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

    @Nullable
    @Override
    public Bitmap getBitmap(@PassBitmap final String passBitmap) {
        final String fileWithPathString = getPath() + "/" + passBitmap + PassImpl.FILETYPE_IMAGES;

        try {
            final File file = new File(fileWithPathString);
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException expectedInSomeCases_willJustReturnNull) {
            return null;
        }
    }

    @NonNull
    public String getTypeNotNull() {
        return (type == null) ? "none" : type;
    }

    @Override
    public void save(PassStore store) {
        final String jsonString = PassWriter.toJSON(this);

        path = store.getPathForID(getId());
        new File(path).mkdirs();
        final File file = new File(path, "data.json");
        store.deleteCacheForId(getId());
        AXT.at(file).writeString(jsonString);

    }

    public void setInvalid() {
        valid = false;
    }

    @Override
    @Nullable
    public String getSource() {
        final File file = new File(getPath() + "/source.obj");
        if (file.exists()) {
            try {
                return (String) AXT.at(file).loadToObject();
            } catch (IOException | ClassNotFoundException e) {
                // OK no source info - no big deal - but log
                Log.w("pass source info file exists but not readable" + e);
            }
        }
        return null;
    }

}
