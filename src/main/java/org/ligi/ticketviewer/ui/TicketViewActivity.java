package org.ligi.ticketviewer.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import org.ligi.ticketviewer.helper.PassVisualizer;
import org.ligi.ticketviewer.maps.PassbookMapsFacade;
import org.ligi.ticketviewer.model.PassbookParser;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TicketViewActivity extends TicketViewActivityBase {


    @OnClick(R.id.barcode_img)
    void onBarcodeClick() {
        Intent i = new Intent(TicketViewActivity.this, FullscreenImageActivity.class);
        i.putExtra("path", path);
        TicketViewActivity.this.startActivity(i);
    }

    @InjectView(R.id.barcode_img)
    ImageView barcode_img;

    @InjectView(R.id.logo_img)
    ImageView logo_img;

    @InjectView(R.id.thumbnail_img)
    ImageView thumbnail_img;

    @InjectView(R.id.back_fields)
    TextView back_tv;

    @InjectView(R.id.main_fields)
    TextView front_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!passbookParser.isValid()) { // don't deal with invalid passes
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.pass_problem))
                    .setTitle(getString(R.string.problem))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNeutralButton(getString(R.string.send), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ExportProblemPassToLigiAndFinishTask(TicketViewActivity.this, passbookParser.getPath(), TicketDefinitions.getShareDir(TicketViewActivity.this), "share.pkpass").execute();
                        }
                    })
                    .show();
            return;
        }

        View contentView = getLayoutInflater().inflate(R.layout.activity_ticket_view, null);
        setContentView(contentView);

        ButterKnife.inject(this);

        barcode_img.setImageBitmap(passbookParser.getBarcodeBitmap(AXT.at(getWindowManager()).getSmallestSide() / 3));
        logo_img.setImageBitmap(passbookParser.getLogoBitmap());
        thumbnail_img.setImageBitmap(passbookParser.getThumbnailImage());

        if (findViewById(R.id.map_container) != null) {
            if (!(passbookParser.getLocations().size() > 0 && PassbookMapsFacade.init(this))) {
                findViewById(R.id.map_container).setVisibility(View.GONE);
            }
        }

        if (passbookParser.getType() != null) {
            String front_str = "";
            front_str += PassVisualizer.getFieldListAsString(passbookParser.getPrimaryFields());
            front_str += PassVisualizer.getFieldListAsString(passbookParser.getSecondaryFields());
            front_str += PassVisualizer.getFieldListAsString(passbookParser.getHeaderFields());
            front_str += PassVisualizer.getFieldListAsString(passbookParser.getAuxiliaryFields());

            front_tv.setText(Html.fromHtml(front_str));
        }
        String back_str = "";

        if (App.isDeveloperMode()) {
            back_str += getPassDebugInfo(passbookParser);
        }

        back_str += PassVisualizer.getFieldListAsString(passbookParser.getBackFields());

        back_tv.setText(Html.fromHtml(back_str));

        Linkify.addLinks(back_tv, Linkify.ALL);
        PassVisualizer.visualize(passbookParser, contentView);
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

}
