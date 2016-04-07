package org.ligi.passandroid;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.MediumTest;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ligi.passandroid.steps.PassEditSteps.goToFields;

@TargetApi(14)
public class TheFieldListEditFragment extends BaseIntegration<PassEditActivity> {

    @Inject
    PassStore passStore;

    PassImpl currentPass;
    private PassField field;
    private ArrayList<PassField> fieldList;

    public TheFieldListEditFragment() {
        super(PassEditActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final TestComponent build = DaggerTestComponent.create();
        build.inject(this);
        App.setComponent(build);

        currentPass = (PassImpl) (passStore.getCurrentPass());

        fieldList = new ArrayList<>();
        field = new PassField(null, "labelfieldcontent", "valuefieldcontent", true);
        fieldList.add(field);
        currentPass.setFields(fieldList);
    }

    @MediumTest
    public void testFieldDetailsArePreFilled() {

        getActivity();
        goToFields();

        Spoon.screenshot(getActivity(), "one_field");

        onView(withId(R.id.label_field_edit)).check(matches(isDisplayed()));
        onView(withId(R.id.label_field_edit)).check(matches(withText("labelfieldcontent")));

        onView(withId(R.id.value_field_edit)).check(matches(isDisplayed()));
        onView(withId(R.id.value_field_edit)).check(matches(withText("valuefieldcontent")));

        onView(withId(R.id.hide_switch)).check(matches(isDisplayed()));
        onView(withId(R.id.hide_switch)).check(matches(isChecked()));
    }

    @MediumTest
    public void testThatUnCheckedHideWorks() {

        field.setHide(false);
        getActivity();
        goToFields();

        Spoon.screenshot(getActivity(), "no_hide");

        onView(withId(R.id.hide_switch)).check(matches(isDisplayed()));
        onView(withId(R.id.hide_switch)).check(matches(isNotChecked()));
    }

    @MediumTest
    public void testThatChangingLabelWorks() {

        getActivity();
        goToFields();

        onView(withId(R.id.label_field_edit)).perform(click(),replaceText("newlabel"));
        assertThat(field.getLabel()).isEqualTo("newlabel");
    }

    @MediumTest
    public void testThatChangingValueWorks() {

        getActivity();
        goToFields();

        onView(withId(R.id.value_field_edit)).perform(click(),replaceText("newvalue"));
        assertThat(field.getValue()).isEqualTo("newvalue");
    }

    /* TODO add tests for delete and add */

}
