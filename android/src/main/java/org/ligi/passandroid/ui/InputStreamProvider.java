package org.ligi.passandroid.ui;

import android.content.Context;
import android.net.Uri;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.ligi.passandroid.App;
import org.ligi.passandroid.model.InputStreamWithSource;

public class InputStreamProvider {

    public static final String IPHONE_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X; en-us) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53";

    public static InputStreamWithSource fromURI(final Context context, final Uri uri) {
        App.component().tracker().trackEvent("protocol", "to_inputstream", uri.getScheme(), null);
        switch (uri.getScheme()) {
            case "content":

                return InputStreamProvider.fromContent(context, uri);

            case "http":
            case "https":
                // TODO check if SPDY should be here
                return InputStreamProvider.fromOKHttp(uri);

            default:
                App.component().tracker().trackException("unknown scheme in ImportAsyncTask" + uri.getScheme(), false);
            case "file":
                return InputStreamProvider.getDefaultInputStreamForUri(uri);
        }

    }

    public static InputStreamWithSource fromOKHttp(final Uri uri) {
        try {
            final OkHttpClient client = new OkHttpClient();
            final URL url = new URL(uri.toString());
            final Request.Builder requestBuilder = new Request.Builder().url(url);

            // fake to be an iPhone in some cases when the server decides to send no passbook
            // to android phones - but only do it then - we are proud to be Android ;-)
            final Set<Map.Entry<String, String>> iPhoneFakeMap = new HashMap<String, String>() {{
                put("air_canada", "//m.aircanada.ca/ebp/");
                put("air_canada2", "//services.aircanada.com/ebp/");
                put("icelandair","//checkin.si.amadeus.net");
                put("mbk","//mbk.thy.com/");
                put("heathrow","//passbook.heathrow.com/");
                put("eventbrite","//www.eventbrite.com/passes/order");
            }}.entrySet();

            for (Map.Entry<String, String> fakeConfig : iPhoneFakeMap) {
                if (uri.toString().contains(fakeConfig.getValue())) {
                    App.component().tracker().trackEvent("quirk_fix", "ua_fake", fakeConfig.getKey(), null);
                    requestBuilder.header("User-Agent", IPHONE_USER_AGENT);
                }
            }

            final Request request = requestBuilder.build();

            final Response response = client.newCall(request).execute();

            return new InputStreamWithSource(uri.toString(), response.body().byteStream());
        } catch (MalformedURLException e) {
            App.component().tracker().trackException("MalformedURLException in ImportAsyncTask", e, false);
        } catch (IOException e) {
            App.component().tracker().trackException("IOException in ImportAsyncTask", e, false);
        }
        return null;
    }

    public static InputStreamWithSource fromContent(final Context ctx, final Uri uri) {
        try {
            return new InputStreamWithSource(uri.toString(), ctx.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            App.component().tracker().trackException("FileNotFoundException in passImportActivity/ImportAsyncTask", e, false);
            return null;
        }

    }


    public static InputStreamWithSource getDefaultInputStreamForUri(final Uri uri) {
        try {
            return new InputStreamWithSource(uri.toString(), new BufferedInputStream(new URL(uri.toString()).openStream(), 4096));
        } catch (IOException e) {
            App.component().tracker().trackException("IOException in passImportActivity/ImportAsyncTask", e, false);
            return null;
        }
    }
}
