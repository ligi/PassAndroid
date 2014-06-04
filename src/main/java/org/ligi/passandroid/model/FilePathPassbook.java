package org.ligi.passandroid.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.helper.BarcodeHelper;
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

public class FilePathPassbook implements Passbook {

    private String path;
    private String id;
    private String type;
    private boolean passbook_valid = true; // be positive
    private String barcodeMessage;
    private BarcodeFormat barcodeFormat;
    private int backGroundColor;
    private int foregroundColor;
    private String description;
    private DateTime relevantDate;
    private PassFieldList primaryFields, secondaryFields, backFields, auxiliaryFields, headerFields;
    private List<PassLocation> locations = new ArrayList<PassLocation>();
    private JSONObject ticketJSONObject = null;
    private String plainJsonString;

    public static final String[] TYPES = new String[]{"coupon", "eventTicket", "boardingPass", "generic", "storeCard"};


    public FilePathPassbook(String path) {
        this.path = path;

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        id = path.substring(path.lastIndexOf('/') + 1);

        JSONObject pass_json = null;
        final File file = new File(path + "/pass.json");

        if (file.exists()) {
            try {
                plainJsonString = AXT.at(file).readToString();
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
            passbook_valid = false;
            return;
        }

        try {
            final JSONObject barcode_json = pass_json.getJSONObject("barcode");

            barcodeFormat = BarcodeFormat.QR_CODE; // DEFAULT

            barcodeMessage = barcode_json.getString("message");

            final String barcodeFormatString = barcode_json.getString("format");

            if (barcodeFormatString.contains("417")) {
                barcodeFormat = BarcodeFormat.PDF_417;
            }

            if (barcodeFormatString.toUpperCase(Locale.ENGLISH).contains("AZTEC")) {
                barcodeFormat = BarcodeFormat.AZTEC;
            }

            // TODO should check a bit more with barcode here - this can be dangerous

        } catch (Exception e) {
        }

        if (pass_json != null) {
            try {
                relevantDate = new DateTime(pass_json.getString("relevantDate"));
            } catch (JSONException e) {
            } catch (IllegalArgumentException e) {
                // be robust when it comes to bad dates - had a RL crash with "2013-12-25T00:00-57:00" here
                // OK then we just have no date here
            }

            try {
                JSONArray locations_json = pass_json.getJSONArray("locations");
                for (int i = 0; i < locations_json.length(); i++) {
                    JSONObject obj = locations_json.getJSONObject(i);

                    PassLocation location = new PassLocation(this);
                    location.latlng.lat = obj.getDouble("latitude");
                    location.latlng.lon = obj.getDouble("longitude");

                    if (obj.has("relevantText")) {
                        location.setDescription(obj.getString("relevantText"));
                    }

                    locations.add(location);
                }
            } catch (JSONException e) {
            }

            try {
                String backgroundColor = pass_json.getString("backgroundColor");
                backGroundColor = parseColor(backgroundColor, 0);
            } catch (JSONException e) {
            }

            try {
                String foregroundColor = pass_json.getString("foregroundColor");
                this.foregroundColor = parseColor(foregroundColor, 0xffffffff);
            } catch (JSONException e) {
            }

            try {
                description = pass_json.getString("description");
            } catch (JSONException e) {
            }


            // try to find in a predefined set of tickets

            for (String atype : TYPES) {
                if (pass_json.has(atype)) {
                    type = atype;
                }
            }

            // try to rescue the situation and find types
            if (type == null) {
                type = findType(pass_json);
                Tracker.get().trackEvent("problem_event", "strange_type", type, null);
            }

            if (type == null) {
                try {
                    Tracker.get().trackEvent("problem_event", "pass", "without_type", null);
                    Log.i("pass without type " + pass_json.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else try {
                ticketJSONObject = pass_json.getJSONObject(type);
            } catch (JSONException e) {
            }

        }

        if (ticketJSONObject != null) {
            primaryFields = new PassFieldList(ticketJSONObject, "primaryFields");
            secondaryFields = new PassFieldList(ticketJSONObject, "secondaryFields");
            auxiliaryFields = new PassFieldList(ticketJSONObject, "auxiliaryFields");
            backFields = new PassFieldList(ticketJSONObject, "backFields");
            headerFields = new PassFieldList(ticketJSONObject, "headerFields");
        }

    }

    public String findType(JSONObject obj) {

        Iterator keys = obj.keys();
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

    public String getDescription() {
        if (description == null) {
            return ""; // better way of returning no description - so we can avoid optional / null checks and it is kind of the same thing
            // an empty description - we can do kind of all String operations safely this way and do not have to care about the existence of a real description
            // if we want to know if one is there we can check length for being 0 still ( which we would have to do anyway for empty descriptions )
            // See no way at the moment where we would have to distinguish between an empty and an missing description
        }
        return description;
    }

    public String getType() {
        return type;
    }

    public PassFieldList getPrimaryFields() {
        return primaryFields;
    }

    public PassFieldList getSecondaryFields() {
        return secondaryFields;
    }

    public PassFieldList getBackFields() {
        return backFields;
    }

    public PassFieldList getAuxiliaryFields() {
        return auxiliaryFields;
    }

    public PassFieldList getHeaderFields() {
        return headerFields;
    }

    public List<PassLocation> getLocations() {
        return locations;
    }

    private int parseColor(String color_str, int defaultValue) {
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

    private int parseColorPoundStyle(String color_str, int defaultValue) {
        return Color.parseColor(color_str);
    }

    private int parseColorRGBStyle(String color_str, int defaultValue) {
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

    public boolean isValid() {
        return passbook_valid;
    }

    public BarcodeFormat getBarcodeFormat() {
        return barcodeFormat;
    }

    public Bitmap getBarcodeBitmap(final int size) {
        if (barcodeMessage == null) {
            // no message -> no barcode
            Tracker.get().trackException("No Barcode in pass - strange", false);
            return null;
        }

        if (barcodeFormat == null) {
            Log.w("Barcode format is null - fallback to QR");
            Tracker.get().trackException("Barcode format is null - fallback to QR", false);
            BarcodeHelper.generateBarCodeBitmap(barcodeMessage, BarcodeFormat.QR_CODE, size);
        }

        return BarcodeHelper.generateBarCodeBitmap(barcodeMessage, barcodeFormat, size);

    }

    public String getIconPath() {
        if (new File(path + "/icon@2x.png").exists()) {
            return path + "/icon@2x.png";
        }
        if (new File(path + "/icon.png").exists()) {
            return path + "/icon.png";
        }
        return null;
    }

    public Bitmap getIconBitmap() {
        Bitmap result = null;

        if (path != null) {

            // first we try to fetch the small icon
            result = BitmapFactory.decodeFile(path + "/icon@2x.png");

            // if that failed we use the small one
            if (result == null) {
                result = BitmapFactory.decodeFile(path + "/icon.png");
            }


        }
        return result;
    }

    public Bitmap getThumbnailImage() {
        Bitmap result = null;

        if (path != null) {

            // first we try to fetch the small icon
            result = BitmapFactory.decodeFile(path + "/thumbnail@2x.png");

            // if that failed we use the small one
            if (result == null) {
                result = BitmapFactory.decodeFile(path + "/thumbnail.png");
            }


        }
        return result;
    }

    public Bitmap getLogoBitmap() {
        Bitmap result = null;

        if (path != null) {
            result = BitmapFactory.decodeFile(path + "/logo@2x.png");

            if (result == null) {
                result = BitmapFactory.decodeFile(path + "/logo.png");
            }

        }
        return result;
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public String getPath() {
        return path;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public boolean hasRelevantDate() {
        return relevantDate != null;
    }

    public DateTime getRelevantDate() {
        return relevantDate;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getPlainJsonString() {
        return plainJsonString;
    }
}
