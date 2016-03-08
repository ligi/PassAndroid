package org.ligi.passandroid.ui.quirk_fix;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import org.ligi.passandroid.ui.PassAndroidActivity;
import org.ligi.passandroid.ui.PassImportActivity;

import org.ligi.passandroid.R;

public class URLRewriteActivity extends PassAndroidActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String url = new URLRewriteController(tracker).getUrlByUri(getIntent().getData());

        if (url == null) {
            new AlertDialog.Builder(this).setTitle(getString(R.string.dialog_workaround_title))
                    .setMessage(getString(R.string.dialog_workaround_description))
                    .setPositiveButton(getString(R.string.intent_browser), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tracker.trackException("URLRewrite with invalid activity", false);
                            final Intent intent = new Intent(URLRewriteActivity.this, OpenIphoneWebView.class);
                            intent.setData(getIntent().getData());
                            startActivity(intent);

                        }
                    })
                    .setNeutralButton(getString(R.string.action_send), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "PassAndroid: URLRewrite Problem");
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ligi@ligi.de"});
                            intent.putExtra(Intent.EXTRA_TEXT, getIntent().getData().toString());
                            intent.setType("text/plain");

                            startActivity(Intent.createChooser(intent, getString(R.string.intent_open_link)));
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            URLRewriteActivity.this.finish();
                        }
                    })
                    .show();

            return;
        }

        final Intent intent = new Intent(this, PassImportActivity.class);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        finish();
    }

}
