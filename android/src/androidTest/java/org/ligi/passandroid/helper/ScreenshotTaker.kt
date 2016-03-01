package org.ligi.passandroid.helper

import android.app.Activity
import com.jraska.falcon.FalconSpoon

/*
Facade for screenshot's
 */
object ScreenshotTaker {

    fun takeScreenshot(activity: Activity, message: String) {
        FalconSpoon.screenshot(activity, message)
    }
}