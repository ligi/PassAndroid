package org.ligi.ticketviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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

        if (passbookParser.getLocations().size() > 0) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            LocationsMapFragment locationsMapFragment = new LocationsMapFragment();
            locationsMapFragment.click_to_fullscreen = true;
            ft.replace(R.id.map_container, locationsMapFragment);
            ft.commit();
        } else {
            getAQ().find(R.id.map_container).getView().setVisibility(View.GONE);
        }

        TicketListActivity.visualizePassbookData(passbookParser, v);
    }


}
