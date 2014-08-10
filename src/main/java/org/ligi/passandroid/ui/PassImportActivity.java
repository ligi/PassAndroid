package org.ligi.passandroid.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.model.PassStore;

public class PassImportActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ImportAndShowAsyncTask(this, getIntent().getData()).execute();
    }

    class ImportAndShowAsyncTask extends ImportAsyncTask {

        private final ProgressDialog progressDialog;

        public ImportAndShowAsyncTask(final Activity passImportActivity, final Uri intent_uri) {
            super(passImportActivity, intent_uri);
            progressDialog = new ProgressDialog(passImportActivity);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCancelable(false);
        }


        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(InputStreamWithSource result) {
            progressDialog.dismiss();
            if (result != null) {

                UnzipPassDialog.show(result, passImportActivity, new UnzipPassDialog.FinishCallback() {
                    @Override
                    public Void call(String path) {

                        // TODO this is kind of a hack - there should be a better way
                        final String id = AXT.at(path.split("/")).last();

                        final PassStore store = App.getPassStore();
                        store.setCurrentPass(store.getPassbookForId(id));

                        AXT.at(PassImportActivity.this).startCommonIntent().activityFromClass(PassViewActivity.class);
                        finish();
                        return null;
                    }
                });
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_pass_view, menu);

        return true;
    }


}
