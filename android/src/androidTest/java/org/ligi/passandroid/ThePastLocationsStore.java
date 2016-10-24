package org.ligi.passandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.passandroid.model.PastLocationsStore;
import org.ligi.passandroid.ui.PassViewActivity;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.assertj.core.api.Assertions.assertThat;

public class ThePastLocationsStore {

    @Rule
    public ActivityTestRule<PassViewActivity> rule = new ActivityTestRule<>(PassViewActivity.class);

    @Inject
    Tracker tracker;

    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        App.setComponent(DaggerTestComponent.builder().testModule(new TestModule()).build());
        sharedPreferences = getInstrumentation().getContext().getSharedPreferences("" + (System.currentTimeMillis() / 100000), Context.MODE_PRIVATE);
        TestApp.component().inject(this);
    }

    @After
    public void tearDown() {
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void testPastLocationsStoreShouldNeverContainMoreThanMaxElements() {
        PastLocationsStore tested = new PastLocationsStore(sharedPreferences, tracker);

        for (int i = 0; i < PastLocationsStore.MAX_ELEMENTS * 2; i++) {
            tested.putLocation("" + i);
        }

        assertThat(tested.getLocations().size()).isEqualTo(PastLocationsStore.MAX_ELEMENTS);

    }

    @Test
    public void testPastLocationsStoreShouldStoreOnlyOneOfAKind() {
        PastLocationsStore tested = new PastLocationsStore(sharedPreferences, tracker);

        for (int i = 0; i < 3; i++) {
            tested.putLocation("foo");
        }

        assertThat(tested.getLocations()).containsOnly("foo");

    }

}
