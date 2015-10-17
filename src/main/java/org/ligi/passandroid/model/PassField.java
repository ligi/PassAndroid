package org.ligi.passandroid.model;

import android.support.annotation.Nullable;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class PassField implements Serializable {

    public final String key;
    public final String label;
    public final String value;

    public PassField(JSONObject jsonObject, AppleStylePassTranslation translation) {
        label = translation.translate(getField(jsonObject, "label"));
        value = translation.translate(getField(jsonObject, "value"));
        key = translation.translate(getField(jsonObject, "key"));
    }

    @Nullable
    private static String getField(final JSONObject object, final String key) {
        if (object.has(key)) {
            try {
                return object.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
