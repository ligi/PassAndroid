package org.ligi.passandroid.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.ligi.passandroid.App;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;
import org.ligi.passandroid.model.State;

public class PassAndroidActivity extends AppCompatActivity {

    @Inject
    PassStore passStore;

    @Inject
    Settings settings;

    @Inject
    EventBus bus;

    @Inject
    State state;

    @Inject
    protected Tracker tracker;

    private Integer lastSet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.component().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (lastSet != null && lastSet != settings.getNightMode()) {
            recreateActivity();
        }
        lastSet = settings.getNightMode();
    }

    @TargetApi(11)
    public void recreateActivity() {
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        }
    }
}