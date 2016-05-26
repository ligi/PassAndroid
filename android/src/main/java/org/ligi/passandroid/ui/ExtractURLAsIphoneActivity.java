package org.ligi.passandroid.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.ligi.passandroid.ui.quirk_fix.OpenIphoneWebView;

public class ExtractURLAsIphoneActivity extends PassAndroidActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        tracker.trackEvent("quirk_fix", "unpack_attempt", getIntent().getData().getHost(), null);

        new DownloadExtractAndStartImportTask().execute();
    }

    private class DownloadExtractAndStartImportTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            final OkHttpClient client = new OkHttpClient();
            try {
                final Request.Builder requestBuilder = new Request.Builder().url(new URI(getIntent().getData().toString()).toURL());
                requestBuilder.header("User-Agent", InputStreamProvider.IPHONE_USER_AGENT);

                final ResponseBody body = client.newCall(requestBuilder.build()).execute().body();
                final String bodyString = body.string();
                body.close();

                final String url = extractURL(bodyString);

                if (url == null) {
                    return null;
                }

                if (!url.startsWith("http")) {
                    return getIntent().getData().getScheme() + "://" + getIntent().getData().getHost() + "/" + url;
                }

                return url;
            } catch (IOException | URISyntaxException e) {
                tracker.trackException("ExtractURLAsIphoneActivity", e, false);
            }

            return null;
        }

        @Nullable
        private String extractURL(String body) {
            final String[] patterns = new String[] { "href=\"(.*\\.pkpass.*?)\"" , "window.location = \'(.*\\.pkpass.*?)\'"};

            for (final String pattern : patterns) {
                final Pattern p = Pattern.compile(pattern);
                final Matcher m = p.matcher(body);

                if (m.find()) {
                    return m.group(1);
                }
            }

            return null ;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                final Intent intent = new Intent(ExtractURLAsIphoneActivity.this, OpenIphoneWebView.class);
                intent.setData(getIntent().getData());
                startActivity(intent);
                tearDown();
                return;
            }

            tracker.trackEvent("quirk_fix", "unpack_success", getIntent().getData().getHost(), null);

            final Intent intent = new Intent(ExtractURLAsIphoneActivity.this, PassImportActivity.class);
            intent.setData(Uri.parse(s));

            startActivity(intent);
            tearDown();
        }
    }

    public void tearDown() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        finish();
    }
}
