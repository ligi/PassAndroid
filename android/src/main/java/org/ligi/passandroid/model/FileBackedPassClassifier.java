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

    public FileBackedPassClassifier(final File backed_file, final PassStore passStore, final Moshi moshi) {
        super(loadMap(backed_file, moshi), passStore);

        this.backed_file = backed_file;
        adapter = getAdapter(moshi);
    }

    private static JsonAdapter<Map> getAdapter(Moshi moshi) {
        return moshi.adapter(Map.class);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> loadMap(final File backed_file, final Moshi moshi) {

        if (backed_file.exists()) {
            try {
                return (Map<String, String>) getAdapter(moshi).fromJson(Okio.buffer(Okio.source(backed_file)));
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
                    adapter.toJson(buffer, getTopicByIdMap());
                    buffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
