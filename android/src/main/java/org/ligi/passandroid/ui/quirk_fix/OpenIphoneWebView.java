package org.ligi.passandroid.ui.quirk_fix;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import net.steamcrafted.loadtoast.LoadToast;
import org.ligi.passandroid.R;
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

        final int backgroundColor = ContextCompat.getColor(this, R.color.dividing_color);
        final LoadToast loadToast = new LoadToast(this).setText("Loading").setBackgroundColor(backgroundColor).show();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
                loadToast.success();
            }

            @Override
            public void onReceivedError(final WebView view, final WebResourceRequest request, final WebResourceError error) {
                super.onReceivedError(view, request, error);
                loadToast.error();
            }
        });

    }
}
