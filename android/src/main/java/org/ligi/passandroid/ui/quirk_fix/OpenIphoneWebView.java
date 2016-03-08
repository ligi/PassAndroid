package org.ligi.passandroid.ui.quirk_fix;

import android.app.Activity;
import android.os.Bundle;
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

        final LoadToast loadToast = new LoadToast(this)
                .setText(getString(R.string.spinner_loading))
                .setBackgroundColor(getResources().getColor(R.color.dividing_color)).show();



        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
                loadToast.success();
            }

            @Override
            public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                loadToast.error();
            }
        });

    }
}
