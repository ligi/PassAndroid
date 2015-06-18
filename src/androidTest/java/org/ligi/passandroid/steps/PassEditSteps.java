package org.ligi.passandroid.steps;

import org.ligi.passandroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class PassEditSteps {
    public static void goToMetaData() {
        onView(withId(R.id.passEditPager)).perform(swipeLeft());
    }

    public static void goToImages() {
        goToMetaData();
        onView(withId(R.id.passEditPager)).perform(swipeLeft());

    }

    public static void goToColor() {
        goToImages();
        onView(withId(R.id.passEditPager)).perform(swipeLeft());
    }


    public static void goToBarCode() {
        goToColor();
        onView(withId(R.id.passEditPager)).perform(swipeLeft());
        onView(withId(R.id.barcodeTypeRadioGroup)).check(matches(isDisplayed()));
    }
}
