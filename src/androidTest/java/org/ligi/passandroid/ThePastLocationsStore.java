package org.ligi.passandroid;

import android.app.Activity;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.passandroid.model.PastLocationsStore;

import static org.assertj.core.api.Assertions.assertThat;

public class ThePastLocationsStore extends BaseIntegration<Activity> {

    public ThePastLocationsStore() {
        super(Activity.class);
    }

    @SmallTest
    public void testPastLocationsStoreShouldNeverContainMoreThanMaxElements() {
        PastLocationsStore tested = new PastLocationsStore(getInstrumentation().getContext());

        for (int i = 0; i < PastLocationsStore.MAX_ELEMENTS * 2; i++) {
            tested.putLocation("" + i);
        }

        assertThat(tested.getLocations().size()).isLessThan(PastLocationsStore.MAX_ELEMENTS);

    }

    @SmallTest
    public void testPastLocationsStoreShouldStoreOnlyOneOfAKind() {
        PastLocationsStore tested = new PastLocationsStore(getInstrumentation().getContext());

        for (int i = 0; i < 3; i++) {
            tested.putLocation("foo");
        }

        assertThat(tested.getLocations().size()).isLessThan(2);

    }

}
