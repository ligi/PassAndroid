package org.ligi.passandroid.ui.quirk_fix;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import org.ligi.axt.AXT;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.ui.PassImportActivity;

import java.net.URLEncoder;

public class URLRewriteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = null;

        if (getIntent().getData().getHost().endsWith(".virginaustralia.com")) { // mobile. or checkin.
            url = getVirginAustraliaURL();
        } else if (getIntent().getData().getHost().equals("www.cathaypacific.com")) {
            url = getCathay();
        } else if (getIntent().getData().getHost().equals("mbp.swiss.com")) {
            url = getSwiss();
        }

        if (url == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Workaround failed")
                    .setMessage("The URL PassAndroid tried to work around failed :-( some companies just send PassBooks to Apple Devices - this was an attempt to workaround this." +
                            "Unfortunately it failed - perhaps there where changes on the serverside - you can open the site with your browser now - to see it in PassAndroid in future again it would help if you can send me the pass")
                    .setPositiveButton("Browser", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Tracker.get().trackException("URLRewrite with invalid activity", false);
                            AXT.at(URLRewriteActivity.this).rethrowIntentExcludingSelf();
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
                            }
                    )
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

    private String getVirginAustraliaURL() {


        final Uri data = getIntent().getData();

        final String passId;
        if (data.toString().contains("CheckInApiIntegration")) {
            passId = data.getQueryParameter("key");
            Tracker.get().trackEvent("quirk_fix", "redirect_attempt", "virgin_australia2", null);
        } else {
            Tracker.get().trackEvent("quirk_fix", "redirect_attempt", "virgin_australia1", null);
            passId = data.getQueryParameter("c");
        }

        if (passId == null) {
            return null;
        }

        Tracker.get().trackEvent("quirk_fix", "redirect", "virgin_australia", null);

        return "https://mobile.virginaustralia.com/boarding/pass.pkpass?key=" + URLEncoder.encode(passId);
    }

    private String getCathay() {
        final String passId = getIntent().getData().getQueryParameter("v");

        Tracker.get().trackEvent("quirk_fix", "redirect_attempt", "cathay", null);

        if (passId == null) {
            return null;
        }

        Tracker.get().trackEvent("quirk_fix", "redirect", "cathay", null);

        return "https://www.cathaypacific.com/icheckin2/PassbookServlet?v=" + URLEncoder.encode(passId);
    }

    public String getSwiss() {
        String url = getIntent().getData().toString();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);

        }

        final String[] split = url.split("/");

        if (split.length < 6) {
            return null;
        }

        return "http://prod.wap.ncrwebhost.mobi/mobiqa/wap/" + split[split.length - 2] + "/" + split[split.length - 1] + "/passbook";
    }
}
