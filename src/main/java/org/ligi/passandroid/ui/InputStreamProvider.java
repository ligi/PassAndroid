package org.ligi.passandroid.ui;

import android.content.Context;
import android.net.Uri;

import com.squareup.okhttp.OkHttpClient;

import org.ligi.passandroid.Tracker;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class InputStreamProvider {
    public final static InputStream fromOKHttp(Uri intent_uri) {
        try {
            final OkHttpClient client = new OkHttpClient();
            final URL url = new URL(intent_uri.toString());
            final HttpURLConnection connection = client.open(url);

            return connection.getInputStream();
        } catch (MalformedURLException e) {
            Tracker.get().trackException("MalformedURLException in ImportAsyncTask", e, false);
        } catch (IOException e) {
            Tracker.get().trackException("IOException in ImportAsyncTask", e, false);
        }
        return null;
    }

    public final static InputStream fromContent(Context ctx, Uri uri) {
        try {
            return ctx.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Tracker.get().trackException("FileNotFoundException in ticketImportActivity/ImportAsyncTask", e, false);
            return null;
        }

    }


    public final static InputStream getDefaultHttpInputStreamForUri(Uri intent_uri) {
        try {
            return new BufferedInputStream(new URL(intent_uri.toString()).openStream(), 4096);
        } catch (IOException e) {
            Tracker.get().trackException("IOException in ticketImportActivity/ImportAsyncTask", e, false);
            return null;
        }
    }
}
