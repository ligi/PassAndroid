package org.ligi.passandroid.model;

import android.graphics.BitmapFactory;
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

    public static Pass read(String path, String language) {
        final PassImpl pass = new PassImpl();

        final AppleStylePassTranslation translation = new AppleStylePassTranslation();

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        pass.setPath(path);

        pass.setId(path.substring(path.lastIndexOf('/') + 1));

        JSONObject pass_json = null, type_json = null;

        Optional<String> localized_path = findLocalizedPath(path, language);

        if (localized_path.isPresent()) {
            final File file = new File(localized_path.get(), "pass.strings");
            translation.loadFromFile(file);
        }

        pass.setIconBitmapFile(findBitmapFile(path, localized_path, "icon"));
        pass.setLogoBitmapFile(findBitmapFile(path, localized_path, "logo"));
        pass.setThumbnailBitmapFile(findBitmapFile(path, localized_path, "thumbnail"));

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

            final List<PassLocation> locations = new ArrayList<>();
            try {

                final JSONArray locations_json = pass_json.getJSONArray("locations");
                for (int i = 0; i < locations_json.length(); i++) {
                    final JSONObject obj = locations_json.getJSONObject(i);

                    final PassLocation location = new PassLocation(pass);
                    location.latlng.lat = obj.getDouble("latitude");
                    location.latlng.lon = obj.getDouble("longitude");

                    if (obj.has("relevantText")) {
                        location.setDescription(translation.translate(obj.getString("relevantText")));
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
                pass.setDescription(translation.translate(pass_json.getString("description")));
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
                type_json = pass_json.getJSONObject(pass.getType());
            } catch (JSONException e) {
            }

        }

        if (type_json != null) {
            pass.setPrimaryFields(new PassFieldList(type_json, "primaryFields", translation));
            pass.setSecondaryFields(new PassFieldList(type_json, "secondaryFields", translation));
            pass.setAuxiliaryFields(new PassFieldList(type_json, "auxiliaryFields", translation));
            pass.setBackFields(new PassFieldList(type_json, "backFields", translation));
            pass.setHeaderFields(new PassFieldList(type_json, "headerFields", translation));
        }

        try {
            pass.setOrganization(Optional.of(pass_json.getString("organizationName")));

        } catch (JSONException e) {
            // ok - we have no organisation - big deal ..-)
        }

        ApplePassbookQuirkCorrector.correctQuirks(pass);

        return pass;
    }

    private static Optional<String> findLocalizedPath(String path, String language) {

        final File localized = new File(path, language + ".lproj");

        if (localized.exists() && localized.isDirectory()) {
            return Optional.of(localized.getPath());
        }

        final File fallback = new File(path, "en.lproj");

        if (fallback.exists() && fallback.isDirectory()) {
            return Optional.of(fallback.getPath());
        }

        return Optional.absent();
    }

    private static String findBitmapFile(String path, Optional<String> localizedPath, String bitmap) {
        String res;
        if (localizedPath.isPresent()) {
            res = localizedPath.get() + "/" + bitmap + "@2x.png";
            if (BitmapFactory.decodeFile(res) != null) {
                return res;
            }

            res = localizedPath.get() + "/" + bitmap + ".png";
            if (BitmapFactory.decodeFile(res) != null) {
                return res;
            }
        }

        res = path + "/" + bitmap + "@2x.png";
        if (BitmapFactory.decodeFile(res) != null) {
            return res;
        }

        res = path + "/" + bitmap + ".png";
        if (BitmapFactory.decodeFile(res) != null) {
            return res;
        }
        return null;
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
            return Color.parseColor(color_str);
        }

        return defaultValue;
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
