package org.ligi.passandroid

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.helper.checkThatHelpIsThere
import org.ligi.passandroid.ui.HelpActivity
import org.ligi.trulesk.TruleskIntentRule

class TheHelpActivity {

    @get:Rule
    val rule = TruleskIntentRule(HelpActivity::class.java)

    @Test
    fun testHelpIsThere() {
        checkThatHelpIsThere()
        rule.screenShot("help")
    }

    @Test
    fun test_that_help_finishes_on_home() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        assertThat(rule.activity.isFinishing).isTrue()
    }

    @Test
    fun test_that_version_is_shown() {
        onView(withText("v" + BuildConfig.VERSION_NAME)).check(matches(isDisplayed()))
    }
}
