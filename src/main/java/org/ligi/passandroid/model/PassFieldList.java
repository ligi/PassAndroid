package org.ligi.passandroid.model;

import com.google.common.base.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.tracedroid.logging.Log;

import java.util.ArrayList;

public class PassFieldList extends ArrayList<PassField> {

    public PassFieldList() {
    }

    public PassFieldList(JSONObject passJSON, String fieldsName) {
        try {
            final JSONArray jsonArray = passJSON.getJSONArray(fieldsName);
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    final PassField field = new PassField(jsonArray.getJSONObject(i));
                    add(field);
                } catch (JSONException e) {
                    Log.w("could not process PassField from JSON for " + fieldsName + " cause: " + e);
                }

            }
        } catch (JSONException e) {
            Log.w("could not process PassFields " + fieldsName + " from JSON " + e);
        }
    }

    public Optional<PassField> getPassFieldForKey(String key) {

        for (PassField field : this) {
            if (field.key != null && field.key.equals(key)) {
                return Optional.of(field);
            }
        }
        return Optional.absent();
    }


    public Optional<PassField> getPassFieldThatMatchesLabel(String matcher) {

        for (PassField field : this) {
            if (field.label != null && field.label.matches(matcher)) {
                return Optional.of(field);
            }
        }
        return Optional.absent();
    }
}
