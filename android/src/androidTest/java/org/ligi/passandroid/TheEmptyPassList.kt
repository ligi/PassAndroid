package org.ligi.passandroid

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.R.id.emptyView
import org.ligi.passandroid.functions.checkThatHelpIsThere
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.ui.PassListActivity
import org.ligi.trulesk.TruleskIntentRule
import java.util.*


class TheEmptyPassList {

    @get:Rule
    var rule = TruleskIntentRule(PassListActivity::class.java, false)

    @Before
    fun setUp() {
        App.setComponent(DaggerTestComponent.builder().testModule(TestModule(ArrayList<Pass>())).build())
        rule.launchActivity(null)
    }

    @Test
    fun testEmptyViewIsThereWhenThereAreNoPasses() {
        rule.screenShot("empty_view")
        onView(withId(emptyView)).check(matches(isDisplayed()))
    }

    @Test
    fun testHelpGoesToHelp() {
        onView(withId(R.id.menu_help)).perform(click())

        checkThatHelpIsThere()
    }

}
