package org.ligi.ticketviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.ticketviewer.helper.BarcodeHelper;
import org.ligi.ticketviewer.helper.FileHelper;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: ligi
 * Date: 2/9/13
 * Time: 10:48 PM
 */
public class PassbookParser {

    private String path;
    private String problem_str = "";
    private boolean passbook_valid = true; // be positive
    private String barcode_msg;
    private Bitmap barcodeBitmap = null;
    private com.google.zxing.BarcodeFormat barcodeFormat;
    private Bitmap icon_bitmap;
    private int bgcolor;
    private String description;
    private String type;
    private List<Field> primaryFields, secondaryFields, backFields, auxiliaryFields, headerFields;
    private List<PassLocation> locations = new ArrayList<PassLocation>();
    private int fgcolor;
    private JSONObject eventTicket = null;

    private JSONObject getJSONObjectWithFixing(String str) throws JSONException {
        /**
         * I got a really broken pass with invalid json from a user. The source was Virgin Australia
         * the bad part looked like this:
         *
         * "value": "NTL",}
         *
         * this code fixes this problem
         */
        try {
            return new JSONObject(str);
        } catch (JSONException e) {
            // I got a pass with invalid json
            if (e.getMessage().startsWith("Expected")) {
                return new JSONObject(str.replaceAll(",[\n\r ]*\\}", "}"));
            } else {
                throw e;
            }
        }
    }

    public PassbookParser(String path, Context ctx) {

        this.path = path;

        JSONObject pass_json = null;

        try {
            pass_json = getJSONObjectWithFixing(FileHelper.file2String(new File(path + "/pass.json")));
        } catch (Exception e) {
            Log.i("PassParse Exception " + e);
        }

        if (pass_json == null) {
            // I had got a strange passbook with UCS-2 which could not be parsed before
            // was searching for a auto-detection, but could not find one with support for this encoding
            // and the right license

            String[] encodings = {"UTF-8", "UTF-16", "UCS-2", "UTF-8", "UTF-16BE", "UTF-16LE"};

            for (String encoding : encodings) {
                try {
                    pass_json = getJSONObjectWithFixing(FileHelper.file2String(new File(path + "/pass.json"), Charset.forName(encoding)));
                } catch (Exception e) {
                }

                if (pass_json != null)
                    break;
            }
            ;
        }

        if (pass_json == null) {
            Log.w("could not load pass.json from passcode ");
            EasyTracker.getTracker().trackEvent("problem_event", "pass", "without_pass_json", null);
            problem_str += "Problem with pass.json ";
            passbook_valid = false;
            return;
        }

        try {
            JSONObject barcode_json = pass_json.getJSONObject("barcode");

            barcodeFormat = com.google.zxing.BarcodeFormat.QR_CODE; // DEFAULT

            barcode_msg = barcode_json.getString("message");

            if (barcode_json.getString("format").contains("417"))
                barcodeFormat = com.google.zxing.BarcodeFormat.PDF_417;

            // TODO should check a bit more with barcode here - this can be dangerous

        } catch (Exception e) {
            problem_str += "Problem with pass.json";
        }

        if (pass_json != null) {


            try {
                JSONArray locations_json = pass_json.getJSONArray("locations");
                for (int i = 0; i < locations_json.length(); i++) {
                    JSONObject obj = locations_json.getJSONObject(i);

                    PassLocation location = new PassLocation();
                    location.latlng = new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude"));
                    location.description = obj.getString("relevantText");
                    locations.add(location);
                }


            } catch (JSONException e) {
            }

            try {
                String backgroundColor = pass_json.getString("backgroundColor");
                bgcolor = parseColor(backgroundColor, 0);
            } catch (JSONException e) {
            }

            try {
                String foregroundColor = pass_json.getString("foregroundColor");
                fgcolor = parseColor(foregroundColor, 0xffffffff);
            } catch (JSONException e) {
            }

            try {
                description = pass_json.getString("description");
            } catch (JSONException e) {
            }


            // try to find in a predefined set of tickets
            String[] types = {"coupon", "eventTicket", "boardingPass", "generic", "storeCard"};

            for (String atype : types) {
                if (pass_json.has(atype))
                    type = atype;
            }

            // try to rescue the situation and find types
            if (type == null) {
                type = findType(pass_json);
                EasyTracker.getTracker().trackEvent("problem_event", "strange_type", type, null);
            }

            Log.i("got typee" + type);

            if (type == null) {
                try {
                    EasyTracker.getTracker().trackEvent("problem_event", "pass", "without_type", null);
                    Log.i("pass without type " + pass_json.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else try {
                eventTicket = pass_json.getJSONObject(type);
            } catch (JSONException e) {
            }

        }

        primaryFields = getFieldListFromJsonArr(eventTicket, "primaryFields");
        secondaryFields = getFieldListFromJsonArr(eventTicket, "secondaryFields");
        auxiliaryFields = getFieldListFromJsonArr(eventTicket, "auxiliaryFields");
        backFields = getFieldListFromJsonArr(eventTicket, "backFields");
        headerFields = getFieldListFromJsonArr(eventTicket, "headerFields");

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
        return description;
    }

    public String getType() {
        return type;
    }

    public List<Field> getPrimaryFields() {
        return primaryFields;
    }

    public List<Field> getSecondaryFields() {
        return secondaryFields;
    }

    public List<Field> getBackFields() {
        return backFields;
    }

    public List<Field> getAuxiliaryFields() {
        return auxiliaryFields;
    }

    public List<Field> getHeaderFields() {
        return headerFields;
    }

    public List<PassLocation> getLocations() {
        return locations;
    }

    /**
     * returns a list of Fields for the key - empty list when no elements - not nul
     *
     * @param obj
     * @param key
     * @return
     */
    public List<Field> getFieldListFromJsonArr(JSONObject obj, String key) {
        ArrayList<Field> res = new ArrayList<Field>();


        JSONArray arr = null;

        if (obj != null) try {
            arr = obj.getJSONArray(key);
            for (int i = 0; i < arr.length(); i++) {
                Field f = new Field();
                try {
                    f.label = arr.getJSONObject(i).getString("label");
                    f.value = arr.getJSONObject(i).getString("value");
                    res.add(f);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
        }
        return res;
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

    public Bitmap getBarcodeBitmap() {
        if (barcodeBitmap == null) {
            if (barcode_msg != null && barcodeFormat != null)
                barcodeBitmap = BarcodeHelper.generateBarCode(barcode_msg, barcodeFormat);
            else
                Log.w("Barcode msg or format is null");
        }
        return barcodeBitmap;
    }

    public Bitmap getIconBitmap() {
        if (icon_bitmap == null && path != null) {
            /*
            icon_bitmap = BitmapFactory.decodeFile(path + "/logo@2x.png");

            if (icon_bitmap == null)
                icon_bitmap = BitmapFactory.decodeFile(path + "/logo.png");
                                                 */
            if (icon_bitmap == null)
                icon_bitmap = BitmapFactory.decodeFile(path + "/icon@2x.png");

            if (icon_bitmap == null)
                icon_bitmap = BitmapFactory.decodeFile(path + "/icon.png");


        }
        return icon_bitmap;
    }

    public int getBgcolor() {
        return bgcolor;
    }

    public String getPath() {
        return path;
    }

    public int getFGcolor() {
        return fgcolor;
    }

    public class PassLocation {
        public LatLng latlng;
        public String description = "";
    }

    public class Field {
        public String label;
        public String value;
    }

}
