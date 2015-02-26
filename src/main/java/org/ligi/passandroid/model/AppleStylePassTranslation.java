package org.ligi.passandroid.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class AppleStylePassTranslation extends HashMap<String, String> {

    public String translate(String key) {
        if (containsKey(key)) {
            return get(key);
        }
        return key;
    }

    public void loadFromFile(final File file) {

        final String content = readFileAsStringGuessEncoding(file);

        if (content == null) {
            return;
        }

        for (String pair : content.split("\";")) {
            final String[] kv = pair.split("\" ?= ?\"");
            if (kv.length == 2) {
                put(removeLeadingClutter(kv[0]), kv[1]);
            }
        }

    }

    private static String removeLeadingClutter(String s) {
        if (s.startsWith("\"") || s.startsWith("\n") || s.startsWith("\r") || s.startsWith(" ")) {
            return removeLeadingClutter(s.substring(1));
        } else {
            return s;
        }
    }


    @Nullable
    public static String readFileAsStringGuessEncoding(final @NonNull File file) {
        try {
            final byte[] fileData = new byte[(int) file.length()];
            final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            dataInputStream.readFully(fileData);
            dataInputStream.close();

            final CharsetMatch match = new CharsetDetector().setText(fileData).detect();

            if (match != null) try {
                return new String(fileData, match.getName());
            } catch (UnsupportedEncodingException ignored) {
            }
            return new String(fileData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
