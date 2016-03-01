package org.ligi.passandroid;

import android.support.test.InstrumentationRegistry;
import org.ligi.passandroid.reporting.SpooningFailureHandler;
import static android.support.test.espresso.Espresso.setFailureHandler;


public abstract class BaseUnitTest {

    public void setUp()  {
        setFailureHandler(new SpooningFailureHandler(InstrumentationRegistry.getInstrumentation()));
    }

}
