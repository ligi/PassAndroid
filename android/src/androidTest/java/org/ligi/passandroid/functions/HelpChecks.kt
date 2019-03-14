package org.ligi.passandroid.functions

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.ligi.passandroid.R

fun checkThatHelpIsThere() {
    onView(withId(R.id.help_text)).check(matches(isDisplayed()))
}
