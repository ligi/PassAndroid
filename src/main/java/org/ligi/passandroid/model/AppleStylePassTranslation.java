package org.ligi.passandroid.model;

import org.ligi.axt.AXT;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Properties;

public class AppleStylePassTranslation extends HashMap<String, String> {

    public String translate(String key) {
        if (containsKey(key)) {
            return get(key);
        }
        return key;
    }

    public void loadFromFile(final File file) {
        for (Charset charset : Charset.availableCharsets().values()) {
            try {
                String localizationString = AXT.at(file).readToString(charset);
                if (localizationString.startsWith("\"")) {
                    Log.i("", localizationString);
                }
                Properties p = new Properties();
                p.load(new StringReader(localizationString));
                for (Object key : p.keySet()) {

                    if (key instanceof String && ((String) key).startsWith("\"")) {
                        final Object value = p.get(key);
                        if (value instanceof String && ((String) value).startsWith("\"")) {
                            // TODO think about just replacing first and last "
                            put(((String) key).replace("\"", ""), ((String) value).replace("\"", ""));
                        }
                    }
                }
            } catch (Exception e) {
            }

            if (keySet().size() > 0) {
                break;
            }
        }
    }

}
