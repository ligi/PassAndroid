package org.ligi.passandroid

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.setFailureHandler
import android.view.WindowManager
import org.ligi.passandroid.reporting.SpooningFailureHandler


abstract class BaseUnitTest {

    fun setUp(activity: Activity) {
        setFailureHandler(SpooningFailureHandler(InstrumentationRegistry.getInstrumentation()))

        activity.runOnUiThread { activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD) }
    }

}
