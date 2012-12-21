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
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public class TicketImportActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_view);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_ticket_view, menu);

		Uri intent_uri = getIntent().getData(); // extract the uri from
		
		InputStream in=null;
		String uri_str=intent_uri.toString();
		
		if (uri_str.startsWith("content://")) {
			try {
				in = getContentResolver().openInputStream(intent_uri);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			try {
				in = new BufferedInputStream(new URL("" + intent_uri).openStream(),
						4096);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if (in!=null) {
			String path=TicketDefinitions.getTmpDir(this);
			Intent i=new Intent(this,TicketViewActivity.class);
			
			i.putExtra("path",path);
			(new File(path)).mkdirs();
			UnzipPasscodeDialog.show(in,path,this,i);
		}
			
		
		
		
		return true;
	}

}
