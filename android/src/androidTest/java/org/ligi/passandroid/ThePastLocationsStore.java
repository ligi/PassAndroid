package org.ligi.passandroid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.passandroid.model.PastLocationsStore;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class ThePastLocationsStore extends BaseIntegration<Activity> {

    @Inject
    Tracker tracker;

    private SharedPreferences sharedPreferences;

    public ThePastLocationsStore() {
        super(Activity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        App.setComponent(DaggerTestComponent.builder().testModule(new TestModule()).build());
        sharedPreferences = getInstrumentation().getContext().getSharedPreferences("" + (System.currentTimeMillis() / 100000), Context.MODE_PRIVATE);
        ((TestComponent) App.component()).inject(this);
    }

    @Override
    protected void tearDown() throws Exception {
        sharedPreferences.edit().clear().apply();
        super.tearDown();
    }

    @SmallTest
    public void testPastLocationsStoreShouldNeverContainMoreThanMaxElements() {
        PastLocationsStore tested = new PastLocationsStore(sharedPreferences, tracker);

        for (int i = 0; i < PastLocationsStore.MAX_ELEMENTS * 2; i++) {
            tested.putLocation("" + i);
        }

        assertThat(tested.getLocations().size()).isEqualTo(PastLocationsStore.MAX_ELEMENTS);

    }

    @SmallTest
    public void testPastLocationsStoreShouldStoreOnlyOneOfAKind() {
        PastLocationsStore tested = new PastLocationsStore(sharedPreferences, tracker);

        for (int i = 0; i < 3; i++) {
            tested.putLocation("foo");
        }

        assertThat(tested.getLocations()).containsOnly("foo");

    }

}
