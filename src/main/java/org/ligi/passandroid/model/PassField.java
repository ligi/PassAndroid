package org.ligi.passandroid.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PassField {

    public final String key;
    public final String label;
    public final String value;

    public PassField(JSONObject jsonObject) throws JSONException {
        label = jsonObject.getString("label");
        value = jsonObject.getString("value");
        if (jsonObject.has("key")) {
            key = jsonObject.getString("key");
        } else {
            key = null;
        }
    }


}
