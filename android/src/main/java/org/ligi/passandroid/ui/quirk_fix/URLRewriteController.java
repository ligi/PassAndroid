package org.ligi.passandroid.ui.quirk_fix;

import android.net.Uri;
import java.net.URLEncoder;
import org.ligi.passandroid.Tracker;

public class URLRewriteController {

    private final Tracker tracker;

    public URLRewriteController(Tracker tracker) {
        this.tracker = tracker;
    }

    public String getUrlByUri(final Uri uri) {

        if (uri.getScheme().equals("pass2u") && uri.getAuthority().equals("import")) {
            return uri.toString().substring("pass2u://import/".length());
        }

        if (uri.getHost().endsWith(".virginaustralia.com")) { // mobile. or checkin.
            return getVirginAustraliaURL(uri);
        }

        switch (uri.getHost()) {
            case "m.aircanada.ca":
            case "services.aircanada.com":
                return getAirCanada(uri);
            case "www.cathaypacific.com":
                return getCathay(uri);
            case "mbp.swiss.com":
            case "prod.wap.ncrwebhost.mobi":
                return getNrcWebHost(uri);
        }

        return null;
    }

    private String getAirCanada(Uri uri) {
        return uri.toString()+ "?appDetection=false";
    }

    private String getVirginAustraliaURL(final Uri uri) {

        final String passId;
        if (uri.toString().contains("CheckInApiIntegration")) {
            passId = uri.getQueryParameter("key");
            tracker.trackEvent("quirk_fix", "redirect_attempt", "virgin_australia2", null);
        } else {
            tracker.trackEvent("quirk_fix", "redirect_attempt", "virgin_australia1", null);
            passId = uri.getQueryParameter("c");
        }

        if (passId == null) {
            return null;
        }

        tracker.trackEvent("quirk_fix", "redirect", "virgin_australia", null);

        return "https://mobile.virginaustralia.com/boarding/pass.pkpass?key=" + URLEncoder.encode(passId);
    }

    private String getCathay(Uri uri) {
        final String passId = uri.getQueryParameter("v");

        tracker.trackEvent("quirk_fix", "redirect_attempt", "cathay", null);

        if (passId == null) {
            return null;
        }

        tracker.trackEvent("quirk_fix", "redirect", "cathay", null);

        return "https://www.cathaypacific.com/icheckin2/PassbookServlet?v=" + URLEncoder.encode(passId);
    }

    private static String getNrcWebHost(Uri uri) {
        String url = uri.toString();
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
