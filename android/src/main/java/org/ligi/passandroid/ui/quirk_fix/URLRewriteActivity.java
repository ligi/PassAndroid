package org.ligi.passandroid.ui.quirk_fix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.ligi.passandroid.ui.AlertFragment;
import org.ligi.passandroid.ui.PassAndroidActivity;
import org.ligi.passandroid.ui.PassImportActivity;

public class URLRewriteActivity extends PassAndroidActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Uri data = getIntent().getData();
        final String url = data != null ? new URLRewriteController(getTracker()).getUrlByUri(data) : null;

        if (url == null) {
            AlertFragment alert = new AlertFragment();
            getSupportFragmentManager().beginTransaction().add(alert, "AlertFrag").commit();

            return;
        }

        final Intent intent = new Intent(this, PassImportActivity.class);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        finish();
    }

}
