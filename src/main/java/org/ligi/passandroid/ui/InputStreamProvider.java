package org.ligi.passandroid.ui;

import android.content.Context;
import android.net.Uri;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.model.InputStreamWithSource;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class InputStreamProvider {
    public final static InputStreamWithSource fromOKHttp(final Uri uri) {
        try {
            final OkHttpClient client = new OkHttpClient();
            final URL url = new URL(uri.toString());
            Request request = new Request.Builder().url(url).build();
            final Response response = client.newCall(request).execute();

            return new InputStreamWithSource(uri.toString(), response.body().byteStream());
        } catch (MalformedURLException e) {
            Tracker.get().trackException("MalformedURLException in ImportAsyncTask", e, false);
        } catch (IOException e) {
            Tracker.get().trackException("IOException in ImportAsyncTask", e, false);
        }
        return null;
    }

    public final static InputStreamWithSource fromContent(final Context ctx, final Uri uri) {
        try {
            return new InputStreamWithSource(uri.toString(), ctx.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            Tracker.get().trackException("FileNotFoundException in ticketImportActivity/ImportAsyncTask", e, false);
            return null;
        }

    }


    public final static InputStreamWithSource getDefaultHttpInputStreamForUri(final Uri uri) {
        try {
            return new InputStreamWithSource(uri.toString(), new BufferedInputStream(new URL(uri.toString()).openStream(), 4096));
        } catch (IOException e) {
            Tracker.get().trackException("IOException in ticketImportActivity/ImportAsyncTask", e, false);
            return null;
        }
    }
}
