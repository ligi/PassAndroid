package org.ligi.passandroid;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import org.ligi.passandroid.reporting.SpooningFailureHandler;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.setFailureHandler;


public abstract class BaseIntegration<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    public BaseIntegration(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setFailureHandler(new SpooningFailureHandler(getInstrumentation().getTargetContext()));
    }

}
