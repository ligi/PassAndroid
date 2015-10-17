package org.ligi.passandroid.ui.quirk_fix;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.ui.PassImportActivity;

public class USAirwaysLoadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String url;
        if (getIntent().getData().toString().endsWith("/")) {
            url = getIntent().getData().toString().substring(0, getIntent().getData().toString().length() - 1);
        } else {
            url = getIntent().getData().toString();
        }

        final String[] split = url.split("/");

        final String passId = split[split.length - 2] + "/" + split[split.length - 1];

        final String redirectUrl = "http://prod.wap.ncrwebhost.mobi/mobiqa/wap/" + passId + "/passbook";

        Tracker.get().trackEvent("quirk_fix", "redirect", "usairways", null);
        final Intent intent = new Intent(this, PassImportActivity.class);
        intent.setData(Uri.parse(redirectUrl));
        startActivity(intent);
        finish();
    }
}
