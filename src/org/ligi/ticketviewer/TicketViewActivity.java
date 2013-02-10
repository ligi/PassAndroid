package org.ligi.ticketviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TicketViewActivity extends TicketViewActivityBase {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = getLayoutInflater().inflate(R.layout.activity_ticket_view, null);
        setContentView(v);

        ImageView barcode_img = (ImageView) findViewById(R.id.barcode_img);

        barcode_img.setImageBitmap(passbookParser.getBarcodeBitmap());

        // when clicking on the barcode we want to go to the activity showing the barcode fullscreen
        barcode_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(TicketViewActivity.this, FullscreenImageActivity.class);
                i.putExtra("path", path);
                TicketViewActivity.this.startActivity(i);
            }


        });

        // for the header_data
        TicketListActivity.visualizePassbookData(passbookParser, v);
    }


}
