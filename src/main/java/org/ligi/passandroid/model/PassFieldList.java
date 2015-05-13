package org.ligi.passandroid.model;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.tracedroid.logging.Log;

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
            result.append("<b>").append(f.label).append("</b>: ").append(f.value);
            result.append("<br/>");
        }
        return result.toString();
    }

}
