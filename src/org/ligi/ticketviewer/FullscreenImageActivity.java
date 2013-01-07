package org.ligi.ticketviewer;

import android.os.Bundle;
import android.widget.ImageView;

public class FullscreenImageActivity extends TicketViewActivityBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.fullscreen_image);
		ImageView iv=(ImageView)findViewById(R.id.fullscreen_image);
		
		
		iv.setImageBitmap(barcode_bitmap);
		
	}

	
}
