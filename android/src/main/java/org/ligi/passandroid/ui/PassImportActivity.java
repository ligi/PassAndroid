package org.ligi.passandroid.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import javax.inject.Inject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassStore;

public class PassImportActivity extends AppCompatActivity {

    @Inject
    PassStore passStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.component().inject(this);

        new ImportAndShowAsyncTask(this, getIntent().getData()).execute();
    }

    class ImportAndShowAsyncTask extends AsyncTask<Void, Void, InputStreamWithSource> {

        private final ProgressDialog progressDialog;
        final Activity passImportActivity;
        final Uri intent_uri;

        public ImportAndShowAsyncTask(final Activity passImportActivity, final Uri intent_uri) {
            progressDialog = new ProgressDialog(passImportActivity);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            this.intent_uri = intent_uri;
            this.passImportActivity = passImportActivity;
        }


        @Override
        protected InputStreamWithSource doInBackground(Void... params) {
            return InputStreamProvider.fromURI(passImportActivity, intent_uri);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(InputStreamWithSource result) {
            super.onPostExecute(result);

            if (result == null) {

                if (!passImportActivity.isFinishing() && progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception ignored) {

                    }
                }
                finish();
                //TODO show some error here?!
                return; // no result -> no work here
            }

            if (isFinishing()) { // finish with no UI/Dialogs
                // let's do it silently TODO check if we need to jump to a service here as the activity is dying
                UnzipPassController.processInputStream(new UnzipPassController.InputStreamUnzipControllerSpec(result, getApplication(), null, null));
                return;
            }


            UnzipPassDialog.show(result, PassImportActivity.this, new UnzipPassDialog.FinishCallback() {
                @Override
                public Void call(String path) {

                    // TODO this is kind of a hack - there should be a better way
                    final String id = AXT.at(path.split("/")).last();

                    final Pass passbookForId = passStore.getPassbookForId(id);
                    passStore.setCurrentPass(passbookForId);

                    passStore.getClassifier().moveToTopic(passbookForId,getString(R.string.topic_new));

                    AXT.at(PassImportActivity.this).startCommonIntent().activityFromClass(PassViewActivity.class);
                    finish();
                    return null;
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_pass_view, menu);

        return true;
    }


}
