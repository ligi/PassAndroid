package org.ligi.ticketviewer.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ligi.axt.AXT;
import org.ligi.ticketviewer.App;
import org.ligi.ticketviewer.R;
import org.ligi.ticketviewer.TicketDefinitions;
import org.ligi.ticketviewer.helper.PassbookVisualisationHelper;
import org.ligi.ticketviewer.maps.PassbookMapsFacade;
import org.ligi.ticketviewer.model.PassbookParser;

import java.io.File;

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

        View contentView = getLayoutInflater().inflate(R.layout.activity_ticket_view, null);
        setContentView(contentView);

        ImageView barcode_img = (ImageView) findViewById(R.id.barcode_img);
        ImageView logo_img = (ImageView) findViewById(R.id.logo_img);
        ImageView thumbnail_img = (ImageView) findViewById(R.id.thumbnail_img);

        barcode_img.setImageBitmap(passbookParser.getBarcodeBitmap(AXT.at(getWindowManager()).getSmallestSide() / 3));
        logo_img.setImageBitmap(passbookParser.getLogoBitmap());
        thumbnail_img.setImageBitmap(passbookParser.getThumbnailImage());

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

        if (passbookParser.getType() != null) {
            TextView front_tv = getAQ().find(R.id.main_fields).getTextView();
            String front_str = "";
            front_str += PassbookVisualisationHelper.getFieldListAsString(passbookParser.getPrimaryFields());
            front_str += PassbookVisualisationHelper.getFieldListAsString(passbookParser.getSecondaryFields());
            front_str += PassbookVisualisationHelper.getFieldListAsString(passbookParser.getHeaderFields());
            front_str += PassbookVisualisationHelper.getFieldListAsString(passbookParser.getAuxiliaryFields());

            front_tv.setText(Html.fromHtml(front_str));
        }
        String back_str = "";

        if (App.isDeveloperMode()) {
            back_str += getPassDebugInfo(passbookParser);
        }

        back_str += PassbookVisualisationHelper.getFieldListAsString(passbookParser.getBackFields());

        TextView back_tv = getAQ().find(R.id.back_fields).getTextView();
        back_tv.setText(Html.fromHtml(back_str));

        Linkify.addLinks(back_tv, Linkify.ALL);
        PassbookVisualisationHelper.visualizePassbookData(passbookParser, contentView);
    }

    public String getPassDebugInfo(PassbookParser passbook) {

        String result = passbook.plainJsonString;

        for (File f : new File(passbookParser.getPath()).listFiles()) {
            result += f.getName() + "<br/>";
        }

        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean res = super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_map).setVisible((passbookParser.getLocations().size() > 0));
        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_item, menu);
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
            it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + zipPath + zipFileName));
            //it.setType("application/vnd.apple.pkpass");
            it.setType("plain/text");
            it.putExtra(android.content.Intent.EXTRA_TEXT, "");

            ctx.startActivity(Intent.createChooser(it, "How to send Pass?"));

            finish();

        }
    }
}
