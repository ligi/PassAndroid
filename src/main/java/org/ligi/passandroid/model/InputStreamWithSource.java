package org.ligi.passandroid.model;

import java.io.InputStream;

public class InputStreamWithSource {
    private final InputStream inputStream;
    private final String source;

    public InputStreamWithSource(String source, InputStream inputStream) {
        this.inputStream = inputStream;
        this.source = source;
    }


    public InputStream getInputStream() {
        return inputStream;
    }

    public String getSource() {
        return source;
    }
}
