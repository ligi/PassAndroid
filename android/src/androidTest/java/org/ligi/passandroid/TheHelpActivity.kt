package org.ligi.passandroid

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import junit.framework.Assert
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.helper.ScreenshotTaker
import org.ligi.passandroid.steps.HelpSteps.checkThatHelpIsThere
import org.ligi.passandroid.ui.HelpActivity

class TheHelpActivity {

    @get:Rule
    val rule: ActivityTestRule<HelpActivity> = ActivityTestRule(HelpActivity::class.java)

    @Test
    fun testHelpIsThere() {
        checkThatHelpIsThere()
        ScreenshotTaker.takeScreenshot(rule.activity, "help")
    }

    @Test
    fun test_that_help_finishes_on_home() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        Assert.assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun test_that_version_is_shown() {
        onView(withText("v" + BuildConfig.VERSION_NAME)).check(matches(isDisplayed()))
    }
}
