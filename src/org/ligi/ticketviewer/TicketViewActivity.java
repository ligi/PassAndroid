package org.ligi.ticketviewer;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.ticketviewer.helper.FileHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TicketViewActivity extends SherlockActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_view);
		
		ImageView logo_img=(ImageView)findViewById(R.id.logo_img);
		ImageView barcode_img=(ImageView)findViewById(R.id.barcode_img);
		String path=getIntent().getStringExtra("path");
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Display display = getWindowManager().getDefaultDisplay();
		Log.i("TicketView","is temp" + TicketDefinitions.getTmpDir(this) + " " + path);
		if (path.equals(TicketDefinitions.getTmpDir(this))) {
			Log.i("TicketView","is temp");
			JSONObject manifest_json=null;
			try {
				manifest_json=new JSONObject(FileHelper.file2String(new File(path+"/manifest.json")));
			} catch (Exception e) {
				DisplayError("Invalid Passbook","Problem with manifest.json: " + e);
				//return false;
				return;
			}
			
			try {
				String rename_str=TicketDefinitions.getPassesDir(this)+"/"+manifest_json.getString("pass.json");
				File rename_file=new File(rename_str);
				Log.i("TicketView","Renaming to " + rename_str + " " + rename_file);
				
				if (rename_file.exists())
					FileHelper.DeleteRecursive(rename_file);
				
				new File(path+"/").renameTo(rename_file);
				path=rename_str;
			} catch (JSONException e) {
				DisplayError("Invalid Passbook","Problem with pass.json: " + e);
				return;
			}
		}
		JSONObject pass_json;
		
		try {
			pass_json = new JSONObject(FileHelper.file2String(new File(path+"/pass.json")));
			JSONObject barcode_json=pass_json.getJSONObject("barcode");
			com.google.zxing.BarcodeFormat format=com.google.zxing.BarcodeFormat.QR_CODE;
			if (barcode_json.getString("format").contains("417"))
				format=com.google.zxing.BarcodeFormat.PDF_417;
			
			Bitmap barcode_image =BarcodeHelper.generateBarCode(barcode_json.getString("message"),format);
			barcode_img.setImageBitmap(barcode_image);
		} catch (Exception e) {
			DisplayError("Invalid Passbook","Problem with pass.json: " + e);
			//return false;
			return;
		}
			
		int size=(int)(2.0f*Math.min((int)display
				.getHeight(),(int)display.getWidth())/3.0f);
		
		if (path!=null)
			logo_img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path+"/icon.png"),size,size,false));
		
		
		
		
		Log.i("","loading " + path);
	}

	public void DisplayError(String title,String err) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(err)
		.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
			
		}).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_ticket_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			Intent intent=new Intent(TicketViewActivity.this,TicketListActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
