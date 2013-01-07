package org.ligi.ticketviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TicketViewActivity extends TicketViewActivityBase {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_view);
		
		ImageView logo_img=(ImageView)findViewById(R.id.logo_img);
		ImageView barcode_img=(ImageView)findViewById(R.id.barcode_img);
		
		logo_img.setImageBitmap(icon_bitmap);
		barcode_img.setImageBitmap(barcode_bitmap);
		barcode_img.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i=new Intent(TicketViewActivity.this,FullscreenImageActivity.class);
				i.putExtra("path",path);
				TicketViewActivity.this.startActivity(i);
			}

			
		});
		
	}
		
	

}
