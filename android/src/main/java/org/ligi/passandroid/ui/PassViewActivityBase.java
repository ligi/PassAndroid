package org.ligi.passandroid.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.model.Settings;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.ui.UnzipPassController.InputStreamUnzipControllerSpec;

public class PassViewActivityBase extends PassAndroidActivity {

    public static final String EXTRA_KEY_UUID = "uuid";

    public Pass currentPass;
    private boolean fullBrightnessSet = false;

    @Inject
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // a little hack because I strongly disagree with the style guide here
        // ;-)
        // not having the Actionbar overflow menu also with devices with hardware
        // key really helps discoverability
        // http://stackoverflow.com/questions/9286822/how-to-force-use-of-overflow-menu-on-devices-with-menu-button
        try {
            final ViewConfiguration config = ViewConfiguration.get(this);
            final Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore - but at least we tried ;-)
        }

        App.component().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final String uuid = getIntent().getStringExtra(EXTRA_KEY_UUID);

        if (uuid != null) {
            final Pass passbookForId = passStore.getPassbookForId(uuid);
            passStore.setCurrentPass(passbookForId);
        }
        currentPass = passStore.getCurrentPass();

        if (currentPass == null) {
            tracker.trackException("pass not present in " + this, false);
            finish();
        }

        configureActionBar();

        if (settings.isAutomaticLightEnabled()) {
            setToFullBrightness();
        }
    }

    protected void configureActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void refresh() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pass_view, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final boolean res = super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_light).setVisible(!fullBrightnessSet);
        menu.findItem(R.id.menu_print).setVisible(Build.VERSION.SDK_INT >= 19);
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (new PassMenuOptions(this, currentPass).process(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_light:
                setToFullBrightness();
                return true;

            case R.id.menu_update:
                new Thread(new UpdateAsync()).start();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean mightPassBeAbleToUpdate(Pass pass) {
        return pass != null && pass.getWebServiceURL() != null && pass.getPassIdent() != null && pass.getSerial() != null;
    }

    private class UpdateAsync implements Runnable {

        private ProgressDialog dlg;

        @Override
        public void run() {
            final Pass pass = currentPass;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dlg = new ProgressDialog(PassViewActivityBase.this);
                    dlg.setMessage(getString(R.string.downloading_new_pass_version));
                    dlg.show();
                }
            });

            final OkHttpClient client = new OkHttpClient();

            final String url = pass.getWebServiceURL() + "/v1/passes/" + pass.getPassIdent() + "/" + pass.getSerial();
            final Request.Builder requestBuilder = new Request.Builder().url(url);
            requestBuilder.addHeader("Authorization", "ApplePass " + pass.getAuthToken());

            final Request request = requestBuilder.build();

            final Response response;
            try {
                response = client.newCall(request).execute();
                final InputStreamWithSource inputStreamWithSource = new InputStreamWithSource(url, response.body().byteStream());
                final InputStreamUnzipControllerSpec spec = new InputStreamUnzipControllerSpec(inputStreamWithSource,
                                                                                               PassViewActivityBase.this,
                                                                                               passStore,
                                                                                               new MyUnzipSuccessCallback(dlg),
                                                                                               new MyUnzipFailCallback(dlg));
                spec.setOverwrite(true);
                UnzipPassController.INSTANCE.processInputStream(spec);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyUnzipFailCallback implements UnzipPassController.FailCallback {
        private final Dialog dlg;

        private MyUnzipFailCallback(final Dialog dlg) {
            this.dlg = dlg;
        }

        @Override
        public void fail(@NonNull final String reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        dlg.dismiss();
                        new AlertDialog.Builder(PassViewActivityBase.this).setMessage("Could not update pass :( " + reason + ")")
                                                                          .setPositiveButton(android.R.string.ok, null)
                                                                          .show();
                    }
                }
            });

        }
    }

    private class MyUnzipSuccessCallback implements UnzipPassController.SuccessCallback {

        private final Dialog dlg;

        private MyUnzipSuccessCallback(final Dialog dlg) {
            this.dlg = dlg;
        }

        @Override
        public void call(@NonNull final String uuid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing()) {
                        return;
                    }
                    dlg.dismiss();
                    if (!currentPass.getId().equals(uuid)) {
                        passStore.deletePassWithId(currentPass.getId());
                    }
                    final Pass newPass = passStore.getPassbookForId(uuid);
                    passStore.setCurrentPass(newPass);
                    currentPass = passStore.getCurrentPass();
                    refresh();

                    Snackbar.make(getWindow().getDecorView(), R.string.pass_updated, Snackbar.LENGTH_LONG).show();
                }
            });

        }

    }

    private void setToFullBrightness() {
        final Window win = getWindow();
        final WindowManager.LayoutParams params = win.getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        win.setAttributes(params);
        fullBrightnessSet = true;
        supportInvalidateOptionsMenu();
    }
}
