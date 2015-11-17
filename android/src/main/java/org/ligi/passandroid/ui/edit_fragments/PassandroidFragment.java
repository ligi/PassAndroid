package org.ligi.passandroid.ui.edit_fragments;

import android.support.v4.app.Fragment;

import com.squareup.otto.Bus;

import org.ligi.passandroid.App;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.model.PassStore;

import javax.inject.Inject;

public class PassandroidFragment extends Fragment {

    @Inject
    PassStore passStore;

    @Inject
    Bus bus;

    protected PassImpl getPass() {
        return (PassImpl) passStore.getCurrentPass();
    }

    public PassandroidFragment() {
        App.component().inject(this);
    }
}
