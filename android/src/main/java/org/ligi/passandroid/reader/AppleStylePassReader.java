package org.ligi.passandroid.reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.google.zxing.BarcodeFormat;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.helper.SafeJSONReader;
import org.ligi.passandroid.model.ApplePassbookQuirkCorrector;
import org.ligi.passandroid.model.AppleStylePassTranslation;
import org.ligi.passandroid.model.BarCode;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.PassFieldList;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.model.PassLocation;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppleStylePassReader {

    public static FiledPass read(String path, String language) {
        final PassImpl pass = new PassImpl();

        final AppleStylePassTranslation translation = new AppleStylePassTranslation();

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        pass.setPath(path);

        pass.setId(path.substring(path.lastIndexOf('/') + 1));

        JSONObject pass_json = null, type_json = null;

        final String localized_path = findLocalizedPath(path, language);

        if (localized_path != null) {
            final File file = new File(localized_path, "pass.strings");
            translation.loadFromFile(file);
        }

        copyBitmapFile(path, localized_path, PassImpl.BITMAP_ICON);
        copyBitmapFile(path, localized_path, PassImpl.BITMAP_LOGO);
        copyBitmapFile(path, localized_path, PassImpl.BITMAP_STRIP);
        copyBitmapFile(path, localized_path, PassImpl.BITMAP_THUMBNAIL);
        copyBitmapFile(path, localized_path, PassImpl.BITMAP_FOOTER);

        final File file = new File(path + "/pass.json");

        if (file.exists()) {
            try {
                final String plainJsonString = AppleStylePassTranslation.readFileAsStringGuessEncoding(file);
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
                        final String json_str = AXT.at(file).readToString(charset);
                        pass_json = SafeJSONReader.readJSONSafely(json_str);
                    } catch (Exception ignored) {
                        // we try with next charset
                    }

                    if (pass_json != null) {
                        break;
                    }
                }
            }
        }
        if (pass_json == null) {
            Log.w("could not load pass.json from passcode ");
            App.component().tracker().trackEvent("problem_event", "pass", "without_pass_json", null);
            pass.setInvalid();
            return pass;
        }

        try {
            final JSONObject barcode_json = pass_json.getJSONObject("barcode");
            final String barcodeFormatString = barcode_json.getString("format");

            App.component().tracker().trackEvent("measure_event", "barcode_format", barcodeFormatString, 0L);
            final BarcodeFormat barcodeFormat = BarCode.getFormatFromString(barcodeFormatString);
            final BarCode barCode = new BarCode(barcodeFormat, barcode_json.getString("message"));
            pass.setBarCode(barCode);

            if (barcode_json.has("altText")) {
                pass.getBarCode().setAlternativeText(barcode_json.getString("altText"));
            }

            // TODO should check a bit more with barcode here - this can be dangerous

        } catch (Exception ignored) {
        }

        if (pass_json != null) {
            if (pass_json.has("relevantDate")) {
                try {
                    pass.setRelevantDate(new DateTime(pass_json.getString("relevantDate")));
                } catch (JSONException | IllegalArgumentException e) {
                    // be robust when it comes to bad dates - had a RL crash with "2013-12-25T00:00-57:00" here
                    // OK then we just have no date here
                    App.component().tracker().trackException("problem parsing relevant date", e, false);
                }
            }

            if (pass_json.has("expirationDate")) {
                try {
                    pass.setExpirationDate(new DateTime(pass_json.getString("expirationDate")));
                } catch (JSONException | IllegalArgumentException e) {
                    // be robust when it comes to bad dates - had a RL crash with "2013-12-25T00:00-57:00" here
                    // OK then we just have no date here
                    App.component().tracker().trackException("problem parsing expiration date", e, false);
                }
            }

            pass.setSerial(readJsonSafeAsOptional(pass_json, "serialNumber"));
            pass.setAuthToken(readJsonSafeAsOptional(pass_json, "authenticationToken"));
            pass.setWebServiceURL(readJsonSafeAsOptional(pass_json, "webServiceURL"));
            pass.setPassIdent(readJsonSafeAsOptional(pass_json, "passTypeIdentifier"));

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

            } catch (JSONException ignored) {
            }
            pass.setLocations(locations);


            readJsonSafe(pass_json, "backgroundColor", new JsonStringReadCallback() {
                @Override
                public void onString(String string) {
                    pass.setBackgroundColor(AXT.at(string).parseColor(0));
                }
            });

            readJsonSafe(pass_json, "foregroundColor", new JsonStringReadCallback() {
                @Override
                public void onString(String string) {
                    pass.setForegroundColor(AXT.at(string).parseColor(0xffffffff));
                }
            });

            readJsonSafe(pass_json, "description", new JsonStringReadCallback() {
                @Override
                public void onString(String string) {
                    pass.setDescription(translation.translate(string));
                }
            });


            // try to find in a predefined set of tickets

            for (String atype : PassImpl.TYPES) {
                if (pass_json.has(atype)) {
                    pass.setType(atype);
                }
            }

            // try to rescue the situation and find types
            if (pass.getType() == null) {
                pass.setType(findType(pass_json));
                App.component().tracker().trackEvent("problem_event", "strange_type", pass.getType(), null);
            }

            if (pass.getType() == null) {
                try {
                    App.component().tracker().trackEvent("problem_event", "pass", "without_type", null);
                    Log.i("pass without type " + pass_json.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else try {
                type_json = pass_json.getJSONObject(pass.getType());
            } catch (JSONException ignored) {
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
            pass.setOrganisation(pass_json.getString("organizationName"));
            App.component().tracker().trackEvent("measure_event", "organisation_parse", pass.getOrganisation(), 1L);
        } catch (JSONException ignored) {
            // ok - we have no organisation - big deal ..-)
        }

        ApplePassbookQuirkCorrector.correctQuirks(pass);

        return pass;
    }

    @Nullable
    private static String findLocalizedPath(String path, String language) {

        final File localized = new File(path, language + ".lproj");

        if (localized.exists() && localized.isDirectory()) {
            App.component().tracker().trackEvent("measure_event", "pass", language + "_native_lproj", null);
            return localized.getPath();
        }

        final File fallback = new File(path, "en.lproj");

        if (fallback.exists() && fallback.isDirectory()) {
            App.component().tracker().trackEvent("measure_event", "pass", "en_lproj", null);
            return fallback.getPath();
        }

        return null;
    }

    interface JsonStringReadCallback {
        void onString(String string);
    }

    @Nullable
    private static String readJsonSafeAsOptional(JSONObject json, String key) {
        if (json.has(key)) {
            try {
                return json.getString(key);
            } catch (JSONException e) {
                // some passes just do not have the field
            }
        }
        return null;
    }

    private static void readJsonSafe(JSONObject json, String key, JsonStringReadCallback callback) {
        if (json.has(key)) {
            try {
                callback.onString(json.getString(key));
            } catch (JSONException e) {
                // some passes just do not have the field
            }
        }
    }

    private static void copyBitmapFile(String path, @Nullable String localizedPath, String bitmap) {
        final Bitmap bitmap1 = findBitmap(path, localizedPath, bitmap);
        if (bitmap1 != null) {
            try {
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(path, bitmap + PassImpl.FILETYPE_IMAGES)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    private static Bitmap findBitmap(String path, @Nullable String localizedPath, String bitmap) {

        final List<String> searchList = new ArrayList<>();
        if (localizedPath != null) {
            searchList.add(localizedPath + "/" + bitmap + "@2x.png");
            searchList.add(localizedPath + "/" + bitmap + ".png");
        }

        searchList.add(path + "/" + bitmap + "@2x.png");
        searchList.add(path + "/" + bitmap + ".png");

        for (String current : searchList) {
            final Bitmap res = BitmapFactory.decodeFile(current);
            if (res != null) {
                return res;
            }
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
                } catch (JSONException ignored) {
                }

                try {
                    arr = pass_obj.getJSONArray("backFields");
                } catch (JSONException ignored) {
                }

                if (arr != null) {
                    Log.i("foundtype " + key);
                    return key;
                }
            } catch (JSONException ignored) {
            }
        }

        return null;

    }


}
