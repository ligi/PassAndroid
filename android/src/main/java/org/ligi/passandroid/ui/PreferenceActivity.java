package org.ligi.passandroid.ui;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.MenuItem;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;

public class PreferenceActivity extends AppCompatActivity {

    public static class GoPrefsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.preference_key_nightmode))) {
                AppCompatDelegate.setDefaultNightMode(App.component().settings().getNightMode());
                recreateActivity();
            }
        }

        @Override
        public void onCreatePreferences(final Bundle bundle, final String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }

        @TargetApi(11)
        public void recreateActivity() {
            if (Build.VERSION.SDK_INT >= 11) {
                getActivity().recreate();
            }
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_container);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new GoPrefsFragment()).commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
