package org.ligi.passandroid.reporting;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.android.apps.common.testing.ui.espresso.FailureHandler;
import com.google.android.apps.common.testing.ui.espresso.base.DefaultFailureHandler;
import com.squareup.spoon.Spoon;

import org.hamcrest.Matcher;

public class SpooningFailureHandler implements FailureHandler {

    private final FailureHandler delegate;
    private final Context context;

    public SpooningFailureHandler(Context targetContext) {
        delegate = new DefaultFailureHandler(targetContext);
        context = targetContext;
    }

    @Override
    public void handle(Throwable error, Matcher<View> viewMatcher) {
        delegate.handle(error, viewMatcher);
        if (context instanceof Activity) {
            Spoon.screenshot((Activity) context, "error");
        }

    }
}
