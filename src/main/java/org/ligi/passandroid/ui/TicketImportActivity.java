package org.ligi.passandroid.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.model.PassStore;

public class TicketImportActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ImportAndShowAsyncTask(this, getIntent().getData()).execute();
    }

    class ImportAndShowAsyncTask extends ImportAsyncTask {

        public ImportAndShowAsyncTask(final Activity ticketImportActivity, final Uri intent_uri) {
            super(ticketImportActivity, intent_uri);
        }

        @Override
        protected void onPostExecute(InputStreamWithSource result) {
            if (result != null) {

                UnzipPassDialog.show(result, ticketImportActivity, new UnzipPassDialog.FinishCallback() {
                    @Override
                    public Void call(String path) {

                        // TODO this is kind of a hack - there should be a better way
                        final String id = AXT.at(path.split("/")).last();

                        final PassStore store = App.getPassStore();
                        store.setCurrentPass(store.getPassbookForId(id));

                        AXT.at(TicketImportActivity.this).startCommonIntent().activityFromClass(TicketViewActivity.class);

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
        getMenuInflater().inflate(R.menu.activity_ticket_view, menu);

        return true;
    }


}
