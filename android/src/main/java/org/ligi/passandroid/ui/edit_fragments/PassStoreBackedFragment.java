package org.ligi.passandroid.ui.edit_fragments;

import android.support.v4.app.Fragment;

import org.ligi.passandroid.App;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.model.PassStore;

import javax.inject.Inject;

public class PassStoreBackedFragment extends Fragment {

    @Inject
    PassStore passStore;


    protected PassImpl getPass() {
        return (PassImpl) passStore.getCurrentPass();
    }

    public PassStoreBackedFragment() {
        App.component().inject(this);
    }
}
