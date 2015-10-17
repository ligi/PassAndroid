package org.ligi.passandroid.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.ui.quirk_fix.OpenIphoneWebView;

public class ExtractURLAsIphoneActivity extends Activity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        Tracker.get().trackEvent("quirk_fix", "unpack_attempt", getIntent().getData().getHost(), null);

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
                final Pattern p = Pattern.compile("href=\"(.*\\.pkpass.*?)\"");
                final Matcher m = p.matcher(bodyString);

                if (!m.find()) {
                    return null;
                }
                final String url = m.group(1); // this variable should contain the link URL

                if (!url.startsWith("http")) {
                    return getIntent().getData().getScheme() + "://" + getIntent().getData().getHost() + "/" + url;
                }

                return url;
            } catch (IOException | URISyntaxException e) {
                Tracker.get().trackException("ExtractURLAsIphoneActivity", e, false);
            }

            return null;
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

            Tracker.get().trackEvent("quirk_fix", "unpack_success", getIntent().getData().getHost(), null);

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
