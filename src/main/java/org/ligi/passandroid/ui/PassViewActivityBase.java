package org.ligi.passandroid.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassStore;

import java.io.IOException;

public class PassViewActivityBase extends ActionBarActivity {

    public Optional<Pass> optionalPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        optionalPass = App.getPassStore().getCurrentPass();

        if (!optionalPass.isPresent()) {
            Tracker.get().trackException("pass not present in " + this, false);
            finish();
            return;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (new PassMenuOptions(this, optionalPass.get()).process(item)) {
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

    class UpdateAsync implements Runnable {

        private ProgressDialog dlg;

        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dlg=new ProgressDialog(PassViewActivityBase.this);
                    dlg.setMessage("downloading new pass version");
                    dlg.show();

                }
            });

            final OkHttpClient client = new OkHttpClient();
            final Pass pass = optionalPass.get();
            final String url = pass.getWebServiceURL().get() + "/v1/passes/" + pass.getPassIdent().get() + "/" + pass.getSerial().get();
            final Request.Builder requestBuilder = new Request.Builder().url(url);
            requestBuilder.addHeader("Authorization", "ApplePass " + pass.getAuthToken().get());

            final Request request = requestBuilder.build();

            final Response response;
            try {
                response = client.newCall(request).execute();

                final InputStreamWithSource inputStreamWithSource = new InputStreamWithSource(url, response.body().byteStream());

                final UnzipPassController.InputStreamUnzipControllerSpec spec = new UnzipPassController.InputStreamUnzipControllerSpec(inputStreamWithSource, PassViewActivityBase.this, new UnzipPassController.SuccessCallback() {
                    @Override
                    public void call(final String id) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinishing()) {
                                    return;
                                }
                                dlg.dismiss();
                                final PassStore passStore = App.getPassStore();
                                if (!optionalPass.get().getId().equals(id)) {
                                    passStore.deletePassWithId(optionalPass.get().getId());
                                }
                                passStore.deleteCacheForId(id);
                                final Pass newPass = passStore.getPassbookForId(id);
                                passStore.setCurrentPass(newPass);
                                optionalPass = App.getPassStore().getCurrentPass();
                                refresh();
                                Toast.makeText(PassViewActivityBase.this, "Pass Updated", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }, new UnzipPassController.FailCallback() {
                    @Override
                    public void fail(final String reason) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinishing()) {
                                    return;
                                }
                                dlg.dismiss();
                                new AlertDialog.Builder(PassViewActivityBase.this).setMessage("Could not update pass :( " + reason + ")").setPositiveButton(android.R.string.ok, null).show();
                            }
                        });

                    }
                });
                spec.overwrite=true;
                UnzipPassController.processInputStream(spec);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setToFullBrightness() {
        final Window win = getWindow();
        final WindowManager.LayoutParams params = win.getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        win.setAttributes(params);
    }
}
