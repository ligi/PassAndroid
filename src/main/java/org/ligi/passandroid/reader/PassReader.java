package org.ligi.passandroid.reader;

import android.graphics.BitmapFactory;
import android.graphics.Color;

import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.helper.SafeJSONReader;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.tracedroid.logging.Log;

import java.io.File;

public class PassReader {

    public static Pass read(String path) {
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


        } catch (Exception e) {
            Log.i("PassParse Exception " + e);
        }

        pass.setIconBitmapFile(findBitmapFile(path, "icon"));
        pass.setLogoBitmapFile(findBitmapFile(path, "logo"));
        pass.setThumbnailBitmapFile(findBitmapFile(path, "thumbnail"));
        pass.setStripBitmapFile(findBitmapFile(path, "strip"));

        return pass;
    }

    private static String findBitmapFile(String path, String bitmap) {

        String res = path + "/" + bitmap + ".png";
        if (BitmapFactory.decodeFile(res) != null) {
            return res;
        }

        return null;
    }

}
