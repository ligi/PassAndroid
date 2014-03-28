package org.ligi.passandroid.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import org.ligi.passandroid.Tracker;

import java.io.InputStream;

class ImportAsyncTask extends AsyncTask<Void, Void, InputStream> {

    private final Uri intent_uri;
    protected final Activity ticketImportActivity;

    public ImportAsyncTask(Activity ticketImportActivity, Uri intent_uri) {
        this.ticketImportActivity = ticketImportActivity;
        this.intent_uri = intent_uri;
    }

    @Override
    protected InputStream doInBackground(Void... params) {
        Tracker.get().trackEvent("protocol", "to_inputstream", intent_uri.getScheme(), null);

        switch (intent_uri.getScheme()) {
            case "content":

                return InputStreamProvider.fromContent(ticketImportActivity, intent_uri);

            case "http":
            case "https":
                // TODO check if SPDY should be here
                return InputStreamProvider.fromOKHttp(intent_uri);

            default:
                Tracker.get().trackException("unknown scheme in ImportAsyncTask" + intent_uri.getScheme(), false);
            case "file":
                return InputStreamProvider.getDefaultHttpInputStreamForUri(intent_uri);
        }


        // TODO bring back Tracker.get().trackTiming("load_time", System.currentTimeMillis() - start_time, "import", "" + intent_uri);
    }

}
