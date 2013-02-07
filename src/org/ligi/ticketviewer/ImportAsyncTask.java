package org.ligi.ticketviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: ligi
 * Date: 2/7/13
 * Time: 2:27 AM
 */
class ImportAsyncTask extends AsyncTask<Void, Void, InputStream> {

    private Uri intent_uri;
    private Activity ticketImportActivity;

    public ImportAsyncTask(Activity ticketImportActivity, Uri intent_uri) {
        this.ticketImportActivity = ticketImportActivity;
        this.intent_uri = intent_uri;
    }

    @Override
    protected InputStream doInBackground(Void... params) {


        if (intent_uri.toString().startsWith("content://")) {
            try {
                return ticketImportActivity.getContentResolver().openInputStream(intent_uri);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else
            try {
                return new BufferedInputStream(new URL("" + intent_uri).openStream(), 4096);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        return null;
    }

    @Override
    protected void onPostExecute(InputStream result) {
        if (result != null) {
            String path = TicketDefinitions.getTmpDir(ticketImportActivity);
            Intent i = new Intent(ticketImportActivity, TicketViewActivity.class);

            i.putExtra("path", path);
            (new File(path)).mkdirs();
            UnzipPasscodeDialog.show(result, path, ticketImportActivity, i);
        }
        super.onPostExecute(result);
    }

}
