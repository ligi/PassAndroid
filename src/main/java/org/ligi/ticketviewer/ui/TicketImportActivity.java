package org.ligi.ticketviewer.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import org.ligi.ticketviewer.R;

import java.io.InputStream;

public class TicketImportActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ImportAndShowAsyncTask(this, getIntent().getData()).execute();
    }

    class ImportAndShowAsyncTask extends ImportAsyncTask {

        public ImportAndShowAsyncTask(Activity ticketImportActivity, Uri intent_uri) {
            super(ticketImportActivity, intent_uri);
        }

        @Override
        protected void onPostExecute(InputStream result) {
            if (result != null) {

                UnzipPassDialog.show(result, ticketImportActivity, new UnzipPassDialog.FinishCallback() {
                    @Override
                    public Void call(String path) {

                        Intent i = new Intent(ticketImportActivity, TicketViewActivity.class);
                        i.putExtra("path", path);
                        TicketImportActivity.this.startActivity(i);
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
        getMenuInflater().inflate(R.menu.activity_ticket_view, menu);

        return true;
    }


}
