package org.ligi.passandroid.steps;

import org.ligi.passandroid.R;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class PassEditSteps {
    public static void goToMetaData() {
        onView(withId(R.id.passEditPager)).perform(swipeLeft());
        sleep();
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
        sleep();
    }

    private static void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
