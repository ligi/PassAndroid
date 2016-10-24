package org.ligi.gobandroid_hd.base

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.setFailureHandler
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.view.WindowManager
import org.ligi.passandroid.reporting.SpooningFailureHandler
import org.ligi.tracedroid.TraceDroid

class PassandroidTestRule<T : Activity>(activityClass: Class<T>, autoLaunch: Boolean = true) : IntentsTestRule<T>(activityClass, true, autoLaunch) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        TraceDroid.deleteStacktraceFiles()
        setFailureHandler(SpooningFailureHandler(InstrumentationRegistry.getInstrumentation()))
    }

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        activity.runOnUiThread { activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD) }
    }

}