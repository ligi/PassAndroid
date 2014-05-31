package org.ligi.passandroid.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.TicketDefinitions;
import org.ligi.passandroid.helper.PassVisualizer;
import org.ligi.passandroid.maps.PassbookMapsFacade;
import org.ligi.passandroid.model.Passbook;
import org.ligi.passandroid.model.ReducedPassInformation;

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

        if (!passbook.isValid()) { // don't deal with invalid passes
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
                            new ExportProblemPassToLigiAndFinishTask(TicketViewActivity.this, passbook.getPath(), TicketDefinitions.getShareDir(), "share.pkpass").execute();
                        }
                    })
                    .show();
            return;
        }

        View contentView = getLayoutInflater().inflate(R.layout.activity_ticket_view, null);
        setContentView(contentView);

        ButterKnife.inject(this);

        barcode_img.setImageBitmap(passbook.getBarcodeBitmap(AXT.at(getWindowManager()).getSmallestSide() / 3));
        logo_img.setImageBitmap(passbook.getLogoBitmap());

        logo_img.setBackgroundColor(passbook.getBackGroundColor());
        thumbnail_img.setImageBitmap(passbook.getThumbnailImage());

        if (findViewById(R.id.map_container) != null) {
            if (!(passbook.getLocations().size() > 0 && PassbookMapsFacade.init(this))) {
                findViewById(R.id.map_container).setVisibility(View.GONE);
            }
        }

        if (passbook.getType() != null) {
            String front_str = "";
            front_str += PassVisualizer.getFieldListAsString(passbook.getPrimaryFields());
            front_str += PassVisualizer.getFieldListAsString(passbook.getSecondaryFields());
            front_str += PassVisualizer.getFieldListAsString(passbook.getHeaderFields());
            front_str += PassVisualizer.getFieldListAsString(passbook.getAuxiliaryFields());

            front_tv.setText(Html.fromHtml(front_str));
        }
        String back_str = "";

        if (App.isDeveloperMode()) {
            back_str += getPassDebugInfo(passbook);
        }

        back_str += PassVisualizer.getFieldListAsString(passbook.getBackFields());

        back_tv.setText(Html.fromHtml(back_str));

        Linkify.addLinks(back_tv, Linkify.ALL);
        PassVisualizer.visualize(this, new ReducedPassInformation(passbook), contentView);
    }

    public String getPassDebugInfo(Passbook passbook) {

        String result = passbook.plainJsonString;

        for (File f : new File(this.passbook.getPath()).listFiles()) {
            result += f.getName() + "<br/>";
        }

        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean res = super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_map).setVisible((passbook.getLocations().size() > 0));
        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
                finish();
            } else {
                NavUtils.navigateUpTo(this, upIntent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
