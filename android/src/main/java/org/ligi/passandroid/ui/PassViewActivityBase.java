package org.ligi.passandroid.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.PassUtil;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.ui.UnzipPassController.InputStreamUnzipControllerSpec;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.annotation.Nullable;

public class PassViewActivityBase extends PassAndroidActivity {

    public static final String EXTRA_KEY_UUID = "uuid";

    @Nullable
    public Pass optionalPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        final String uuid = getIntent().getStringExtra(EXTRA_KEY_UUID);

        if (uuid != null) {
            final Pass passbookForId = passStore.getPassbookForId(uuid);
            passStore.setCurrentPass(passbookForId);
        }
        optionalPass = passStore.getCurrentPass();

        if (optionalPass == null) {
            tracker.trackException("pass not present in " + this, false);
            finish();
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
        menu.findItem(R.id.menu_edit).setVisible(shouldAllowEdit());
        menu.findItem(R.id.menu_light).setVisible(!fullBrightnessSet);
        return res;
    }

    protected void showPassProblemDialog(final Pass pass, final String reason) {
        new AlertDialog.Builder(this).setMessage(getString(R.string.pass_problem))
                .setTitle(getString(R.string.problem))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNeutralButton(getString(R.string.send), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new ExportProblemPassToLigiAndFinishTask(PassViewActivityBase.this,
                                pass.getId(),
                                settings.getShareDir(),
                                "share",
                                reason).execute();
                    }
                })
                .show();
    }

    private boolean shouldAllowEdit() {
        if (optionalPass == null) {
            return false;
        }

        final String app = optionalPass.getApp();
        return (app != null && app.equals(PassUtil.APP));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (new PassMenuOptions(this, optionalPass).process(item)) {
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
        return pass != null
                && pass.getWebServiceURL() != null
                && pass.getPassIdent() != null
                && pass.getSerial() != null;
    }

    class UpdateAsync implements Runnable {

        private ProgressDialog dlg;

        @Override
        public void run() {
            final Pass pass = optionalPass;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dlg = new ProgressDialog(PassViewActivityBase.this);
                    dlg.setMessage(getString(R.string.downloading_new_pass_version));
                    dlg.show();
                }
            });
            if (!mightPassBeAbleToUpdate(pass)) {
                //TODO: might be removed if not on path of user anymore
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showPassProblemDialog(pass, "cannot update");
                    }
                });
                return;
            }

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
                        new UnzipPassController.SuccessCallback() {
                            @Override
                            public void call(final String uuid) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isFinishing()) {
                                            return;
                                        }
                                        dlg.dismiss();
                                        if (!optionalPass.getId().equals(uuid)) {
                                            passStore.deletePassWithId(optionalPass.getId());
                                        }
                                        passStore.deleteCacheForId(uuid);
                                        final Pass newPass = passStore.getPassbookForId(
                                                uuid);
                                        passStore.setCurrentPass(newPass);
                                        optionalPass = passStore.getCurrentPass();
                                        refresh();
                                        Toast.makeText(PassViewActivityBase.this,
                                                "Pass Updated",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        },
                        new UnzipPassController.FailCallback() {
                            @Override
                            public void fail(final String reason) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isFinishing()) {
                                            return;
                                        }
                                        dlg.dismiss();
                                        new AlertDialog.Builder(PassViewActivityBase.this)
                                                .setMessage("Could not update pass :( " +
                                                        reason +
                                                        ")")
                                                .setPositiveButton(android.R.string.ok,
                                                        null)
                                                .show();
                                    }
                                });

                            }
                        });
                spec.overwrite = true;
                UnzipPassController.processInputStream(spec);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean fullBrightnessSet = false;

    private void setToFullBrightness() {
        final Window win = getWindow();
        final WindowManager.LayoutParams params = win.getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        win.setAttributes(params);
        fullBrightnessSet = true;
        invalidateOptionsMenu();
    }
}
