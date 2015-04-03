package org.ligi.passandroid.model;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class PassWriter {

    public static String toJSON(Pass pass) {

        final JSONObject object = new JSONObject();

        try {
            final JSONObject whatObject = new JSONObject();

            whatObject.put("description", pass.getDescription()); // ok
            object.put("what", whatObject);

            final JSONObject metaObject = new JSONObject();

            metaObject.put("id", pass.getId()); // ok
            metaObject.put("type", pass.getType()); //
            metaObject.put("app", pass.getApp());

            object.put("meta", metaObject);

            if (pass.getBarCode() != null) {
                final JSONObject barcode = new JSONObject();
                barcode.put("message", pass.getBarCode().getMessage());
                barcode.put("type", pass.getBarCode().getBarcodeFormat());

                final Optional<String> alternativeText = pass.getBarCode().getAlternativeText();
                if (alternativeText.isPresent()) {
                    barcode.put("altText", alternativeText.get());
                }

                object.put("barcode", barcode);
            }

            final JSONObject uiObject = new JSONObject();

            object.put("fgColor", "#" + String.format("%08X", pass.getForegroundColor()));
            object.put("bgColor", "#" + String.format("%08X", pass.getBackGroundColor()));

            object.put("ui", uiObject);

            final DateTime relevantDate = pass.getRelevantDate();

            if (relevantDate != null) {
                final JSONObject timeObject = new JSONObject();
                timeObject.put("dateTime", relevantDate.toString());
                object.put("when", timeObject);
            }

            return object.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
