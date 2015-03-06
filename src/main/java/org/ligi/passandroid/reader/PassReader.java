package org.ligi.passandroid.reader;

import android.graphics.Color;
import com.google.common.base.Optional;
import com.google.zxing.BarcodeFormat;
import java.io.File;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.helper.SafeJSONReader;
import org.ligi.passandroid.model.BarCode;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.tracedroid.logging.Log;

public class PassReader {

    public static FiledPass read(String path) {
        final PassImpl pass = new PassImpl();

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        pass.setPath(path);

        pass.setId(path.substring(path.lastIndexOf('/') + 1));
        final File file = new File(path + "/data.json");

        try {
            final String plainJsonString = AXT.at(file).readToString();
            final JSONObject pass_json = SafeJSONReader.readJSONSafely(plainJsonString);

            pass.setDescription(pass_json.getString("description"));
            pass.setType(pass_json.getString("type"));
            pass.setBackgroundColor(Color.parseColor(pass_json.getString("bgColor")));
            pass.setForegroundColor(Color.parseColor(pass_json.getString("fgColor")));

            if (pass_json.has("barcode")) {
                final JSONObject barcode_json = pass_json.getJSONObject("barcode");
                final String barcodeFormatString = barcode_json.getString("type");

                final BarcodeFormat barcodeFormat = BarCode.getFormatFromString(barcodeFormatString);
                final BarCode barCode = new BarCode(barcodeFormat, barcode_json.getString("message"));
                pass.setBarCode(barCode);

                if (barcode_json.has("altText")) {
                    barCode.setAlternativeText(barcode_json.getString("altText"));
                }

            }

            if (pass_json.has("when")) {
                final JSONObject when = pass_json.getJSONObject("when");
                final String dateTime = when.getString("dateTime");
                pass.setRelevantDate(Optional.of(DateTime.parse(dateTime)));
            }

        } catch (Exception e) {
            Log.i("PassParse Exception " + e);
        }

        return pass;
    }

}
