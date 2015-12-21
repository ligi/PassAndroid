package org.ligi.passandroid;

import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.ui.PassListActivity;

import javax.inject.Inject;

public class ThePassListSwiping extends BaseIntegration<PassListActivity> {

    public static final String CUSTOM_PROBE = "custom";
    @Inject
    PassStore passStore;

    public ThePassListSwiping() {
        super(PassListActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final TestComponent testComponent = DaggerTestComponent.builder().build();

        testComponent.inject(this);

        App.setComponent(testComponent);
        getActivity();
    }

    /*

    TODO figure out why this is flaky on some devices :-(

    @MediumTest
    public void testDialogOpensWhenSwipeRight() {
        onView(withId(R.id.pass_recyclerview)).perform(click());

        onView(withId(R.id.pass_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));

        onView(withText("description")).perform(swipeRight());
        RecyclerViewActions.actionOnItemAtPosition(0,swipeRight());

        onView(withText(R.string.move_to_new_topic)).check(matches(isDisplayed()));
    }

    @MediumTest
    public void testWeCanMoveToTrash() {
        onView(withId(R.id.pass_recyclerview)).perform(click());
        onView(withText("description")).perform(swipeRight());

        onView(withId(R.id.suggestion_button_trash)).perform(click());

        onView(withText(R.string.topic_trash)).check(matches(isDisplayed()));
        assertThat(passStore.getClassifier().getTopics()).containsExactly(getActivity().getString(R.string.topic_trash));
    }


    @MediumTest
    public void testWeCanMoveToArchive() {
        onView(withId(R.id.pass_recyclerview)).perform(click());
        onView(withText("description")).perform(swipeRight());

        onView(withId(R.id.suggestion_button_archive)).perform(click());

        onView(withText(R.string.topic_archive)).check(matches(isDisplayed()));
        assertThat(passStore.getClassifier().getTopics()).containsExactly(getActivity().getString(R.string.topic_archive));
    }


    @MediumTest
    public void testWeCanMoveToCustom() {
        onView(withId(R.id.pass_recyclerview)).perform(click());
        onView(withText("description")).perform(swipeLeft());

        onView(withId(R.id.new_topic_edit)).perform(typeText(CUSTOM_PROBE));

        onView(withText(android.R.string.ok)).perform(click());

        assertThat(passStore.getClassifier().getTopics()).containsExactly(CUSTOM_PROBE);
    }


    @MediumTest
    public void testDialogOpensWhenSwipeLeft() {
        onView(withId(R.id.pass_recyclerview)).perform(click());
        onView(withId(R.id.pass_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.pass_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeLeft()));

        onView(withText(R.string.move_to_new_topic)).check(matches(isDisplayed()));
    }



    public static ViewAction swipeRight() {
        return actionWithAssertions(new GeneralSwipeAction(Swipe.SLOW,
                translate(GeneralLocation.CENTER_LEFT, -0.1f, 0),
                GeneralLocation.CENTER_RIGHT, Press.FINGER));
    }

    static CoordinatesProvider translate(final CoordinatesProvider coords,
                                         final float dx, final float dy) {
        return new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                float xy[] = coords.calculateCoordinates(view);
                xy[0] += dx * view.getWidth();
                xy[1] += dy * view.getHeight();
                return xy;
            }
        };
    }
        */
}
