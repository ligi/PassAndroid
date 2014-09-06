package org.ligi.passandroid.model;

import com.google.common.base.Optional;
import com.google.common.io.Files;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;

public class AppleStylePassTranslation extends HashMap<String, String> {

    public String translate(String key) {
        if (containsKey(key)) {
            return get(key);
        }
        return key;
    }

    public void loadFromFile(final File file) {

        final Optional<String> content = loadStringWithCorrectCharset(file);

        if (!content.isPresent()) {
            return;
        }

        final String contentString = content.get();

        for (String pair : contentString.split("\";")) {
            final String[] kv = pair.split("\" ?= ?\"");
            if (kv.length == 2) {
                put(removeLeadingClutter(kv[0]), kv[1]);
            }
        }

    }

    private final static String removeLeadingClutter(String s) {
        if (s.startsWith("\"") || s.startsWith("\n") || s.startsWith("\r") || s.startsWith(" ")) {
            return removeLeadingClutter(s.substring(1));
        } else {
            return s;
        }
    }


    private Optional<String> loadStringWithCorrectCharset(File file) {
        for (Charset charset : Charset.availableCharsets().values()) {
            try {
                String localizationString = Files.toString(file, charset);
                if (localizationString.startsWith("\"")) { // this is kind of how we detect the charset
                    return Optional.of(localizationString);
                }
            } catch (Exception e) {
            }
        }

        return Optional.absent();
    }
}
