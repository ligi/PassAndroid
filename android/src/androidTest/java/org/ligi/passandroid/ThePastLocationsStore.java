package org.ligi.passandroid;

import android.app.Activity;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.ligi.passandroid.model.PastLocationsStore;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class ThePastLocationsStore extends BaseIntegration<Activity> {

    @Inject
    Tracker tracker;

    public ThePastLocationsStore() {
        super(Activity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ((TestComponent)App.component()).inject(this);
    }
    
    @SmallTest
    public void testPastLocationsStoreShouldNeverContainMoreThanMaxElements() {
        PastLocationsStore tested = new PastLocationsStore(getInstrumentation().getContext(), tracker);

        for (int i = 0; i < PastLocationsStore.MAX_ELEMENTS * 2; i++) {
            tested.putLocation("" + i);
        }

        assertThat(tested.getLocations().size()).isEqualTo(PastLocationsStore.MAX_ELEMENTS);

    }

    @SmallTest
    public void testPastLocationsStoreShouldStoreOnlyOneOfAKind() {
        PastLocationsStore tested = new PastLocationsStore(getInstrumentation().getContext(), tracker);

        for (int i = 0; i < 3; i++) {
            tested.putLocation("foo");
        }

        assertThat(tested.getLocations().size()).isLessThan(2);

    }

}
