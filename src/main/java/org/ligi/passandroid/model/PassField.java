package org.ligi.passandroid.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PassField implements Serializable{

    public final String key;
    public String label;
    public String value;

    public PassField(JSONObject jsonObject, AppleStylePassTranslation translation) throws JSONException {
        label = jsonObject.getString("label");
        value = translation.translate(jsonObject.getString("value"));
        if (jsonObject.has("key")) {
            key = translation.translate(jsonObject.getString("key"));
        } else {
            key = null;
        }

    }
}
