package org.ligi.ticketviewer.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;

import org.ligi.ticketviewer.R;
import org.ligi.ticketviewer.TicketDefinitions;
import org.ligi.ticketviewer.helper.PassbookVisualisationHelper;
import org.ligi.ticketviewer.maps.PassbookMapsFacade;
import org.ligi.ticketviewer.model.PassbookParser;

public class TicketViewActivity extends TicketViewActivityBase {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!passbookParser.isValid()) { // don't deal with invalid passes
            new AlertDialog.Builder(this)
                    .setMessage("Sorry, but there was a problem processing this Passbook. If you want you can send me this passbook so I can check what the problem is and improve the software.")
                    .setTitle("Problem")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNeutralButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new PassExportToLigi(TicketViewActivity.this, passbookParser.getPath(), TicketDefinitions.getShareDir(TicketViewActivity.this), "share.pkpass").execute();
                        }
                    })
                    .show();
            return;
        }

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
            if (!(passbookParser.getLocations().size() > 0 && PassbookMapsFacade.init(this))) {
                findViewById(R.id.map_container).setVisibility(View.GONE);
            }
        }

        String back_str = "";
        for (PassbookParser.Field f : passbookParser.getBackFields()) {
            back_str += "<b>" + f.label + "</b>: " + f.value + "<br/>";
        }

        TextView back_tv = getAQ().find(R.id.back_fields).getTextView();
        back_tv.setText(Html.fromHtml(back_str));

        Linkify.addLinks(back_tv, Linkify.ALL);
        PassbookVisualisationHelper.visualizePassbookData(passbookParser, v, true);

        getAQ().find(R.id.colorable_top).getView().setBackgroundColor(passbookParser.getBackGroundColor());
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

    class PassExportToLigi extends PassExportTask {

        public PassExportToLigi(Context ctx, String path, String zip_path, String zip_fname) {
            super(ctx, path, zip_path, zip_fname, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_SUBJECT, "a Passbook with a problem");
            it.putExtra(Intent.EXTRA_EMAIL, new String[]{"ligi@ligi.de"});
            it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + zip_path + zip_fname));
            //it.setType("application/vnd.apple.pkpass");
            it.setType("plain/text");
            it.putExtra(android.content.Intent.EXTRA_TEXT, "");

            ctx.startActivity(Intent.createChooser(it, "How to send Pass?"));

            finish();

        }
    }
}
