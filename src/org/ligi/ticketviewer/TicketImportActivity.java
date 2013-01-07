package org.ligi.ticketviewer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public class TicketImportActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_view);
		new ImportAsyncTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_ticket_view, menu);

		return true;
	}

	class ImportAsyncTask extends AsyncTask<Void, Void, InputStream> {

		@Override
		protected InputStream doInBackground(Void... params) {

			Uri intent_uri = getIntent().getData(); // extract the uri from

			// InputStream in = null;
			String uri_str = intent_uri.toString();

			if (uri_str.startsWith("content://")) {
				try {
					return getContentResolver().openInputStream(intent_uri);
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
				String path = TicketDefinitions.getTmpDir(TicketImportActivity.this);
				Intent i = new Intent(TicketImportActivity.this, TicketViewActivity.class);

				i.putExtra("path", path);
				(new File(path)).mkdirs();
				UnzipPasscodeDialog.show(result, path, TicketImportActivity.this, i);
			}
			super.onPostExecute(result);
		}

	}

}
