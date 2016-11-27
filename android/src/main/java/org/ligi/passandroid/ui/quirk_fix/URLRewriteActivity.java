package org.ligi.passandroid.ui.quirk_fix;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import org.ligi.passandroid.ui.PassAndroidActivity;
import org.ligi.passandroid.ui.PassImportActivity;

public class URLRewriteActivity extends PassAndroidActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String url = new URLRewriteController(getTracker()).getUrlByUri(getIntent().getData());

        if (url == null) {
            new AlertDialog.Builder(this).setTitle("Workaround failed")
                    .setMessage(
                            "The URL PassAndroid tried to work around failed :-( some companies just send PassBooks to Apple Devices - this was an attempt to workaround this." +
                                    "Unfortunately it failed - perhaps there where changes on the serverside - you can open the site with your browser now - to see it in PassAndroid in future again it would help if you can send me the pass")
                    .setPositiveButton("Browser", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getTracker().trackException("URLRewrite with invalid activity", false);
                            final Intent intent = new Intent(URLRewriteActivity.this, OpenIphoneWebView.class);
                            intent.setData(getIntent().getData());
                            startActivity(intent);

                        }
                    })
                    .setNeutralButton("send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "PassAndroid: URLRewrite Problem");
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ligi@ligi.de"});
                            intent.putExtra(Intent.EXTRA_TEXT, getIntent().getData().toString());
                            intent.setType("text/plain");

                            startActivity(Intent.createChooser(intent, "How to send Link?"));
                            finish();
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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
