package org.ligi.ticketviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;

public class TicketImportActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_view);
        new ImportAndShowAsyncTask(this, getIntent().getData()).execute();
    }

    class ImportAndShowAsyncTask extends ImportAsyncTask {

        public ImportAndShowAsyncTask(Activity ticketImportActivity, Uri intent_uri) {
            super(ticketImportActivity, intent_uri);
        }

        @Override
        protected void onPostExecute(InputStream result) {
            if (result != null) {
                String path = TicketDefinitions.getTmpDir(ticketImportActivity);
                Intent i = new Intent(ticketImportActivity, TicketViewActivity.class);

                i.putExtra("path", path);
                (new File(path)).mkdirs();
                UnzipPasscodeDialog.show(result, path, ticketImportActivity, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.activity_ticket_view, menu);

        return true;
    }


}
