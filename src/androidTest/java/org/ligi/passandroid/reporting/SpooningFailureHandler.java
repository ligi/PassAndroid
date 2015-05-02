package org.ligi.passandroid.reporting;

import android.app.Activity;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.InstrumentationTestCase;
import android.view.View;

import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.base.DefaultFailureHandler;
import com.squareup.spoon.Spoon;

import org.hamcrest.Matcher;

import java.util.Collection;


public class SpooningFailureHandler implements FailureHandler {

    private final FailureHandler delegate;
    private final InstrumentationTestCase instrumentation;

    public SpooningFailureHandler(InstrumentationTestCase instrumentation) {
        delegate = new DefaultFailureHandler(instrumentation.getInstrumentation().getTargetContext());
        this.instrumentation = instrumentation;
    }

    @Override
    public void handle(Throwable error, Matcher<View> viewMatcher) {
        try {
            Spoon.screenshot(getCurrentActivity(), "error");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        delegate.handle(error, viewMatcher);

    }


    private Activity getCurrentActivity() throws Throwable {
        instrumentation.getInstrumentation().waitForIdleSync();
        final Activity[] activity = new Activity[1];
        instrumentation.runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                activity[0] = com.google.common.collect.Iterables.getOnlyElement(activities);
            }
        });
        return activity[0];
    }

}

