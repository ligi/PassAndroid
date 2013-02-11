package org.ligi.ticketviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.ticketviewer.helper.BarcodeHelper;
import org.ligi.ticketviewer.helper.FileHelper;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.util.ArrayList;
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
    private List<Field> primaryFields, secondaryFields, backFields, auxiliaryFields;
    private List<PassLocation> locations = new ArrayList<PassLocation>();
    private int fgcolor;


    public PassbookParser(String path) {

        this.path = path;

        JSONObject pass_json = null;

        try {
            pass_json = new JSONObject(FileHelper.file2String(new File(path + "/pass.json")));
        } catch (Exception e) {
            problem_str += "Problem with pass.json";
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
                bgcolor = parseColor(pass_json.getString("backgroundColor"));
            } catch (JSONException e) {
            }

            try {
                fgcolor = parseColor(pass_json.getString("foregroundColor"));
            } catch (JSONException e) {
            }

            try {
                description = pass_json.getString("description");
            } catch (JSONException e) {
            }


            String[] types = {"coupon", "eventTicket", "boardingPass", "generic", "storeCard"};

            for (String atype : types) {
                if (pass_json.has(atype))
                    type = atype;
            }

            Log.i("got typee" + type);
            if (type == null) {
                try {
                    Log.i("" + pass_json.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else
                try {
                    JSONObject eventTicket = pass_json.getJSONObject(type);
                    Log.i("got typee prime");
                    primaryFields = getFieldListFromJsonArr(eventTicket, "primaryFields");
                    Log.i("got typee Sec");
                    secondaryFields = getFieldListFromJsonArr(eventTicket, "secondaryFields");
                    Log.i("got typee aux");
                    auxiliaryFields = getFieldListFromJsonArr(eventTicket, "auxiliaryFields");
                    Log.i("got typee back");
                    backFields = getFieldListFromJsonArr(eventTicket, "backFields");
                    Log.i("got typee OK");
                } catch (JSONException e) {
                }

        }
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
        try {
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

    private Integer parseColor(String color_str) {
        Pattern pattern = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
        Matcher matcher = pattern.matcher(color_str);

        if (matcher.matches()) {
            return (255 << 24 |
                    Integer.valueOf(matcher.group(1)) << 16 |  // r
                    Integer.valueOf(matcher.group(2)) << 8 |  // g
                    Integer.valueOf(matcher.group(3))); // b

        }

        return null;
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
