package org.ligi.passandroid.steps;

import android.support.test.espresso.web.assertion.WebViewAssertions;

import static android.support.test.espresso.web.matcher.DomMatchers.containingTextInBody;
import static android.support.test.espresso.web.sugar.Web.onWebView;

public class HelpSteps {
    public static void checkThatHelpIsThere() {
        onWebView().check(WebViewAssertions.webContent(containingTextInBody("Example passes")));
    }

}
