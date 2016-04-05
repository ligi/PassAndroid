package org.ligi.passandroid.ui.edit_fragments;

import android.support.v4.app.Fragment;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.ligi.passandroid.App;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.PassImpl;

public class PassandroidFragment extends Fragment {

    @Inject
    PassStore passStore;

    @Inject
    EventBus bus;

    protected PassImpl getPass() {
        return (PassImpl) passStore.getCurrentPass();
    }

    public PassandroidFragment() {
        App.component().inject(this);
    }
}
