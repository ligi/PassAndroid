package org.ligi.ticketviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import org.ligi.ticketviewer.helper.PassbookVisualisationHelper;

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

        if (getAQ().find(R.id.map_container).isExist()) {

            if (passbookParser.getLocations().size() > 0) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                LocationsMapFragment locationsMapFragment = new LocationsMapFragment();
                locationsMapFragment.click_to_fullscreen = true;
                ft.replace(R.id.map_container, locationsMapFragment);
                ft.commit();
            } else {
                getAQ().find(R.id.map_container).getView().setVisibility(View.GONE);
            }
        }

        String back_str = "";
        for (PassbookParser.Field f : passbookParser.getAuxiliaryFields())
            back_str += "<b>" + f.label + "</b>: " + f.value + "<br/>";
        for (PassbookParser.Field f : passbookParser.getBackFields())
            back_str += "<b>" + f.label + "</b>: " + f.value + "<br/>";

        TextView back_tv = getAQ().find(R.id.back_fields).getTextView();
        back_tv.setText(Html.fromHtml(back_str));

        Linkify.addLinks(back_tv, Linkify.ALL);
        PassbookVisualisationHelper.visualizePassbookData(passbookParser, v);

        getAQ().find(R.id.colorable_top).getView().setBackgroundColor(passbookParser.getBgcolor());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean res = super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_map).setVisible((passbookParser.getLocations().size() > 0));
        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.map_item, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
