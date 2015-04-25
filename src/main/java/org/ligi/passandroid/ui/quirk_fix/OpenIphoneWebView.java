package org.ligi.passandroid.ui.quirk_fix;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import org.ligi.passandroid.ui.InputStreamProvider;

public class OpenIphoneWebView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WebView webView = new WebView(this);
        webView.getSettings().setUserAgentString(InputStreamProvider.IPHONE_USER_AGENT);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(getIntent().getData().toString());


        setContentView(webView);


    }
}
