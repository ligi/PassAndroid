package org.ligi.passandroid.model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;

public class FileBackedPassClassifier extends PassClassifier {

    private final JsonAdapter<Map> adapter;
    private final File backed_file;

    public FileBackedPassClassifier(final File backed_file, final PassStore passStore, String defaultTopic) {
        super(getBase(backed_file), passStore, defaultTopic);

        this.backed_file = backed_file;
        adapter = getAdapter();
    }

    private static JsonAdapter<Map> getAdapter() {
        final Moshi build = new Moshi.Builder().build();
        return build.adapter(Map.class);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getBase(final File backed_file) {

        if (backed_file.exists()) {
            try {
                return (Map<String, String>) getAdapter().fromJson(Okio.buffer(Okio.source(backed_file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new HashMap<>();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private BufferedSink getBufferedSinkFromMaybeCreatedFile() {
        try {
            if (!backed_file.exists()) {
                final File parentFile = backed_file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                backed_file.createNewFile();
            }

            return Okio.buffer(Okio.sink(backed_file));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void processDataChange() {
        super.processDataChange();

        // write
        if (adapter != null) {
            final BufferedSink buffer = getBufferedSinkFromMaybeCreatedFile();

            if (buffer != null) {
                try {
                    adapter.toJson(buffer, topic_by_id);
                    buffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
