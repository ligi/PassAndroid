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

    public static final String IPHONE_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X; en-us) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53";

    public final static InputStreamWithSource fromOKHttp(final Uri uri) {
        try {
            final OkHttpClient client = new OkHttpClient();
            final URL url = new URL(uri.toString());
            final Request.Builder requestBuilder = new Request.Builder().url(url);

            // fake to be an iPhone in some cases when the server decides to send no passbook
            // to android phones - but only do it then - we are proud to be Android ;-)
            if (uri.toString().contains("//m.aircanada.ca/ebp/")) {
                Tracker.get().trackEvent("quirk_fix", "ua_fake", "air_canada", null);
                requestBuilder.header("User-Agent", IPHONE_USER_AGENT);
            } else if (uri.toString().contains("//checkin.si.amadeus.net")) {
                Tracker.get().trackEvent("quirk_fix", "ua_fake", "icelandair", null);
                requestBuilder.header("User-Agent", IPHONE_USER_AGENT);
            } else if (uri.toString().contains("//mbk.thy.com/")) {
                Tracker.get().trackEvent("quirk_fix", "ua_fake", "mbk", null);
                requestBuilder.header("User-Agent", IPHONE_USER_AGENT);
            }

            final Request request = requestBuilder.build();

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
            Tracker.get().trackException("FileNotFoundException in passImportActivity/ImportAsyncTask", e, false);
            return null;
        }

    }


    public final static InputStreamWithSource getDefaultHttpInputStreamForUri(final Uri uri) {
        try {
            return new InputStreamWithSource(uri.toString(), new BufferedInputStream(new URL(uri.toString()).openStream(), 4096));
        } catch (IOException e) {
            Tracker.get().trackException("IOException in passImportActivity/ImportAsyncTask", e, false);
            return null;
        }
    }
}
