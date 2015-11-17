package org.ligi.passandroid.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.otto.Bus;

import org.ligi.passandroid.App;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;

import javax.inject.Inject;

public class PassAndroidActivity extends AppCompatActivity {

    @Inject
    PassStore passStore;

    @Inject
    Settings settings;

    @Inject
    Bus bus;

    @Inject
    protected Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.component().inject(this);
    }

}