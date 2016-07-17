package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.support.test.filters.MediumTest;
import com.squareup.spoon.Spoon;
import java.util.ArrayList;
import javax.inject.Inject;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.PassField;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.ui.PassEditActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;

@TargetApi(14)
public class TheFieldListEditFragment extends BaseIntegration<PassEditActivity> {

    @Inject
    PassStore passStore;

    private PassField field;

    public TheFieldListEditFragment() {
        super(PassEditActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final TestComponent build = DaggerTestComponent.create();
        build.inject(this);
        App.setComponent(build);

        PassImpl currentPass = (PassImpl) (passStore.getCurrentPass());

        final ArrayList<PassField> fieldList = new ArrayList<>();
        field = new PassField(null, "labelfieldcontent", "valuefieldcontent", true);
        fieldList.add(field);
        currentPass.setFields(fieldList);
    }

    @MediumTest
    public void testFieldDetailsArePreFilled() {

        getActivity();

        Spoon.screenshot(getActivity(), "one_field");

        onView(withId(R.id.label_field_edit)).perform(scrollTo());
        onView(withId(R.id.label_field_edit)).check(matches(isDisplayed()));
        onView(withId(R.id.label_field_edit)).check(matches(withText("labelfieldcontent")));

        onView(withId(R.id.value_field_edit)).check(matches(isDisplayed()));
        onView(withId(R.id.value_field_edit)).check(matches(withText("valuefieldcontent")));

    }


    @MediumTest
    public void testThatChangingLabelWorks() {

        getActivity();

        onView(withId(R.id.label_field_edit)).perform(scrollTo());
        onView(withId(R.id.label_field_edit)).perform(click(),replaceText("newlabel"));
        assertThat(field.getLabel()).isEqualTo("newlabel");
    }

    @MediumTest
    public void testThatChangingValueWorks() {

        getActivity();

        onView(withId(R.id.value_field_edit)).perform(scrollTo());
        onView(withId(R.id.value_field_edit)).perform(click(),replaceText("newvalue"));
        assertThat(field.getValue()).isEqualTo("newvalue");
    }

    /* TODO add tests for delete and add */

}
