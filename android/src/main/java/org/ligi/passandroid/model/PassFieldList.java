package org.ligi.passandroid.model;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.tracedroid.logging.Log;

import java.util.ArrayList;

public class PassFieldList extends ArrayList<PassField> {

    public PassFieldList() {

    }

    public PassFieldList(JSONObject passJSON, String fieldsName, AppleStylePassTranslation translation) {
        try {
            final JSONArray jsonArray = passJSON.getJSONArray(fieldsName);
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    final PassField field = new PassField(jsonArray.getJSONObject(i), translation);
                    add(field);
                } catch (JSONException e) {
                    Log.w("could not process PassField from JSON for " + fieldsName + " cause: " + e);
                }

            }
        } catch (JSONException e) {
            Log.w("could not process PassFields " + fieldsName + " from JSON " + e);
        }
    }

    @Nullable
    public PassField getPassFieldForKey(String key) {

        for (PassField field : this) {
            if (field.key != null && field.key.equals(key)) {
                return field;
            }
        }
        return null;
    }


    public PassField getPassFieldThatMatchesLabel(String matcher) {

        for (PassField field : this) {
            if (field.label != null && field.label.matches(matcher)) {
                return field;
            }
        }
        return null;
    }

    public String toHTMLString() {
        final StringBuilder result = new StringBuilder();
        for (PassField f : this) {
            if (f.label != null) {
                result.append("<b>").append(f.label).append("</b> ");
            }
            if (f.value != null) {
                result.append(f.value);
            }

            if ((f.label != null) || (f.value != null)) {
                result.append("<br/>");
            }
        }
        return result.toString();
    }

}
