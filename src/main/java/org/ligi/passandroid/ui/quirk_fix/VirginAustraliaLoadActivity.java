package org.ligi.passandroid.ui.quirk_fix;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.ui.PassImportActivity;

import java.net.URLEncoder;

public class VirginAustraliaLoadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String passId = getIntent().getData().getQueryParameter("c");
        final String url = "https://mobile.virginaustralia.com/boarding/pass.pkpass?key=" + URLEncoder.encode(passId);

        Tracker.get().trackEvent("quirk_fix", "redirect", "virgin_australia", null);
        final Intent intent = new Intent(this, PassImportActivity.class);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        finish();
    }
}
