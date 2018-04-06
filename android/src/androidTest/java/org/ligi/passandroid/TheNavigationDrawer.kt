package org.ligi.passandroid

import android.annotation.TargetApi
import android.app.Activity.RESULT_CANCELED
import android.app.Instrumentation.ActivityResult
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_VIEW
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerActions.open
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.ligi.passandroid.R.string.*
import org.ligi.passandroid.ui.PassListActivity
import org.ligi.passandroid.ui.PreferenceActivity
import org.ligi.trulesk.TruleskIntentRule

@TargetApi(14)
class TheNavigationDrawer {

    @get:Rule
    var rule = TruleskIntentRule(PassListActivity::class.java)

    @Test
    fun testNavigationDrawerIsUsuallyNotShown() {
        onView(withId(R.id.navigationView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testThatNavigationDrawerOpens() {
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigationView)).check(matches(isDisplayed()))
    }

    @Test
    fun testThatNavigationDrawerClosesOnBackPress() {
        testThatNavigationDrawerOpens()

        pressBack()

        onView(withId(R.id.navigationView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testBetatestClick() {
        testThatNavigationDrawerOpens()
        rule.screenShot("open_drawer")

        intending(hasAction(ACTION_VIEW)).respondWith(ActivityResult(RESULT_CANCELED, null))

        onView(withText(nav_betatest_opt_in_out)).perform(click())

        intended(allOf(hasAction(ACTION_VIEW), hasData("https://play.google.com/apps/testing/org.ligi.passandroid")))
    }


    @Test
    fun testCommunityClick() {
        testThatNavigationDrawerOpens()
        rule.screenShot("open_drawer")

        intending(hasAction(ACTION_VIEW)).respondWith(ActivityResult(RESULT_CANCELED, null))

        onView(withText(nav_community_on_google)).perform(click())

        intended(allOf(hasAction(ACTION_VIEW), hasData("https://plus.google.com/communities/116353894782342292067")))
    }

    @Test
    fun testGitHubClick() {
        testThatNavigationDrawerOpens()
        rule.screenShot("open_drawer")

        intending(hasAction(ACTION_VIEW)).respondWith(ActivityResult(RESULT_CANCELED, null))

        onView(withText(nav_github)).perform(click())

        intended(allOf(hasAction(ACTION_VIEW), hasData("https://github.com/ligi/PassAndroid")))
    }

    @Test
    fun testImproveTranslationsClick() {
        testThatNavigationDrawerOpens()
        rule.screenShot("open_drawer")

        intending(hasAction(ACTION_VIEW)).respondWith(ActivityResult(RESULT_CANCELED, null))

        onView(withText(nav_improve_translation)).perform(click())

        intended(allOf(hasAction(ACTION_VIEW), hasData("https://transifex.com/projects/p/passandroid")))
    }

    @Test
    fun testShareClick() {
        testThatNavigationDrawerOpens()
        rule.screenShot("open_drawer")

        intending(hasAction(ACTION_SEND)).respondWith(ActivityResult(RESULT_CANCELED, null))

        onView(withText(nav_share)).perform(click())

        intended(allOf(hasAction(ACTION_SEND), hasType("text/plain")))
    }

    @Test
    fun testSettings() {
        testThatNavigationDrawerOpens()
        rule.screenShot("open_drawer")

        onView(withText(nav_settings)).perform(click())

        intended(hasComponent(PreferenceActivity::class.java.name))
    }
}
