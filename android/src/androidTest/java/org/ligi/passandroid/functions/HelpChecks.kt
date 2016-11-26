package org.ligi.passandroid.functions

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import org.ligi.passandroid.R


fun checkThatHelpIsThere() {
    onView(withId(R.id.help_text)).check(matches(isDisplayed()))
}
