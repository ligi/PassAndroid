package org.ligi.passandroid.model;

import android.graphics.Color;

import com.google.common.base.Optional;
import com.google.zxing.BarcodeFormat;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.helper.SafeJSONReader;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppleStylePassReader {

    private String plainJsonString;

    public static Pass read(String path) {
        PassImpl pass = new PassImpl();

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }


        pass.setPath(path);

        pass.setId(path.substring(path.lastIndexOf('/') + 1));

        JSONObject pass_json = null, ticketJSONObject = null;

        final File file = new File(path + "/pass.json");

        if (file.exists()) {
            try {
                final String plainJsonString = AXT.at(file).readToString();
                pass_json = SafeJSONReader.readJSONSafely(plainJsonString);
            } catch (Exception e) {
                Log.i("PassParse Exception " + e);
            }

            if (pass_json == null) {
                // I had got a strange passbook with UCS-2 which could not be parsed before
                // was searching for a auto-detection, but could not find one with support for this encoding
                // and the right license

                for (Charset charset : Charset.availableCharsets().values()) {
                    try {

                        String json_str = AXT.at(file).readToString(charset);
                        pass_json = SafeJSONReader.readJSONSafely(json_str);
                    } catch (Exception e) {
                    }

                    if (pass_json != null) {
                        break;
                    }
                }
            }
        }
        if (pass_json == null) {
            Log.w("could not load pass.json from passcode ");
            Tracker.get().trackEvent("problem_event", "pass", "without_pass_json", null);
            pass.setInvalid();
            return pass;
        }

        try {
            final JSONObject barcode_json = pass_json.getJSONObject("barcode");

            pass.setBarcodeFormat(BarcodeFormat.QR_CODE); // DEFAULT

            pass.setBarcodeMessage(barcode_json.getString("message"));

            final String barcodeFormatString = barcode_json.getString("format");

            if (barcodeFormatString.contains("417")) {
                pass.setBarcodeFormat(BarcodeFormat.PDF_417);
            }

            if (barcodeFormatString.toUpperCase(Locale.ENGLISH).contains("AZTEC")) {
                pass.setBarcodeFormat(BarcodeFormat.AZTEC);
            }

            // TODO should check a bit more with barcode here - this can be dangerous

        } catch (Exception e) {
        }

        if (pass_json != null) {
            if (pass_json.has("relevantDate")) {
                try {
                    pass.setRelevantDate(Optional.of(new DateTime(pass_json.getString("relevantDate"))));
                } catch (JSONException | IllegalArgumentException e) {
                    // be robust when it comes to bad dates - had a RL crash with "2013-12-25T00:00-57:00" here
                    // OK then we just have no date here
                    Tracker.get().trackException("problem parsing relevant date", e, false);
                }
            }

            if (pass_json.has("expirationDate")) {
                try {
                    pass.setExpirationDate(Optional.of(new DateTime(pass_json.getString("expirationDate"))));
                } catch (JSONException | IllegalArgumentException e) {
                    // be robust when it comes to bad dates - had a RL crash with "2013-12-25T00:00-57:00" here
                    // OK then we just have no date here
                    Tracker.get().trackException("problem parsing expiration date", e, false);
                }
            }

            List<PassLocation> locations = new ArrayList<>();
            try {

                JSONArray locations_json = pass_json.getJSONArray("locations");
                for (int i = 0; i < locations_json.length(); i++) {
                    JSONObject obj = locations_json.getJSONObject(i);

                    PassLocation location = new PassLocation(pass);
                    location.latlng.lat = obj.getDouble("latitude");
                    location.latlng.lon = obj.getDouble("longitude");

                    if (obj.has("relevantText")) {
                        location.setDescription(obj.getString("relevantText"));
                    }

                    locations.add(location);
                }

            } catch (JSONException e) {
            }
            pass.setLocations(locations);

            try {
                String backgroundColor = pass_json.getString("backgroundColor");
                pass.setBackgroundColor(parseColor(backgroundColor, 0));
            } catch (JSONException e) {
            }

            try {
                String foregroundColor = pass_json.getString("foregroundColor");
                pass.setForegroundColor(parseColor(foregroundColor, 0xffffffff));
            } catch (JSONException e) {
            }

            try {
                pass.setDescription(pass_json.getString("description"));
            } catch (JSONException e) {
            }


            // try to find in a predefined set of tickets

            for (String atype : PassImpl.TYPES) {
                if (pass_json.has(atype)) {
                    pass.setType(atype);
                }
            }

            // try to rescue the situation and find types
            if (pass.getType() == null) {
                pass.setType(findType(pass_json));
                Tracker.get().trackEvent("problem_event", "strange_type", pass.getType(), null);
            }

            if (pass.getType() == null) {
                try {
                    Tracker.get().trackEvent("problem_event", "pass", "without_type", null);
                    Log.i("pass without type " + pass_json.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else try {
                ticketJSONObject = pass_json.getJSONObject(pass.getType());
            } catch (JSONException e) {
            }

        }

        if (ticketJSONObject != null) {
            pass.setPrimaryFields(new PassFieldList(ticketJSONObject, "primaryFields"));
            pass.setSecondaryFields(new PassFieldList(ticketJSONObject, "secondaryFields"));
            pass.setAuxiliaryFields(new PassFieldList(ticketJSONObject, "auxiliaryFields"));
            pass.setBackFields(new PassFieldList(ticketJSONObject, "backFields"));
            pass.setHeaderFields(new PassFieldList(ticketJSONObject, "headerFields"));
        }

        try {
            pass.setOrganization(Optional.of(pass_json.getString("organizationName")));

        } catch (JSONException e) {
            // ok - we have no organisation - big deal ..-)
        }

        ApplePassbookQuirkCorrector.correctQuirks(pass);

        return pass;
    }

    public static String findType(JSONObject obj) {

        final Iterator keys = obj.keys();
        for (String key = ""; keys.hasNext(); key = (String) (keys.next())) {
            try {
                JSONObject pass_obj = obj.getJSONObject(key);
                JSONArray arr = null;
                try {
                    arr = pass_obj.getJSONArray("primaryFields");
                } catch (JSONException e) {
                }

                try {
                    arr = pass_obj.getJSONArray("backFields");
                } catch (JSONException e) {
                }

                if (arr != null) {
                    Log.i("foundtype " + key);
                    return key;
                }
            } catch (JSONException e) {
            }
        }

        return null;

    }

    private static int parseColor(String color_str, int defaultValue) {
        if (color_str == null) {
            return defaultValue;
        }

        if (color_str.startsWith("rgb")) {
            return parseColorRGBStyle(color_str, defaultValue);
        }

        if (color_str.startsWith("#")) {
            return parseColorPoundStyle(color_str, defaultValue);
        }

        return defaultValue;
    }

    private static int parseColorPoundStyle(String color_str, int defaultValue) {
        return Color.parseColor(color_str);
    }

    private static int parseColorRGBStyle(String color_str, int defaultValue) {
        Pattern pattern = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
        Matcher matcher = pattern.matcher(color_str);

        if (matcher.matches()) {
            return (255 << 24 |
                    Integer.valueOf(matcher.group(1)) << 16 |  // r
                    Integer.valueOf(matcher.group(2)) << 8 |  // g
                    Integer.valueOf(matcher.group(3))); // b

        }

        return defaultValue;
    }

}
