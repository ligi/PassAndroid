package org.ligi.passandroid.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import javax.inject.Inject;
import org.ligi.passandroid.App;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.model.PassStore;

public class PassImportActivity extends AppCompatActivity {

    @Inject
    PassStore passStore;

    @Inject
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.component().inject(this);
        if (getIntent().getData() == null || getIntent().getData().getScheme() == null) {
            tracker.trackException("invalid_import_uri", false);
            finish();
        } else {
            new ImportAndShowAsyncTask(this, getIntent().getData()).execute();
        }
    }

}
