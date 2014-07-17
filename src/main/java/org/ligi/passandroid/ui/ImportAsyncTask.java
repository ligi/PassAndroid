package org.ligi.passandroid.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.model.InputStreamWithSource;

class ImportAsyncTask extends AsyncTask<Void, Void, InputStreamWithSource> {

    private final Uri uri;
    protected final Activity passImportActivity;

    public ImportAsyncTask(Activity passImportActivity, Uri uri) {
        this.passImportActivity = passImportActivity;
        this.uri = uri;
    }

    @Override
    protected InputStreamWithSource doInBackground(Void... params) {
        Tracker.get().trackEvent("protocol", "to_inputstream", uri.getScheme(), null);

        switch (uri.getScheme()) {
            case "content":

                return InputStreamProvider.fromContent(passImportActivity, uri);

            case "http":
            case "https":
                // TODO check if SPDY should be here
                return InputStreamProvider.fromOKHttp(uri);

            default:
                Tracker.get().trackException("unknown scheme in ImportAsyncTask" + uri.getScheme(), false);
            case "file":
                return InputStreamProvider.getDefaultHttpInputStreamForUri(uri);
        }


        // TODO bring back Tracker.get().trackTiming("load_time", System.currentTimeMillis() - start_time, "import", "" + uri);
    }

}
