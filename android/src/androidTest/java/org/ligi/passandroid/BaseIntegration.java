package org.ligi.passandroid;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.WindowManager;

import org.ligi.passandroid.reporting.SpooningFailureHandler;

import static android.support.test.espresso.Espresso.setFailureHandler;


public abstract class BaseIntegration<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    public BaseIntegration(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setFailureHandler(new SpooningFailureHandler(this));

    }


    @Override
    public T getActivity() {
        final T activity = super.getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
        });

        return activity;
    }
}
