package org.ligi.passandroid.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Optional;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.PassVisualizer;
import org.ligi.passandroid.maps.PassbookMapsFacade;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassField;
import org.ligi.passandroid.model.PassFieldList;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PassViewActivity extends PassViewActivityBase {

    private View contentView;

    @OnClick(R.id.barcode_img)
    void onBarcodeClick() {
        AXT.at(this).startCommonIntent().activityFromClass(FullscreenBarcodeActivity.class);
    }

    @OnClick(R.id.moreTextView)
    void onMoreClick() {
        if (back_tv.getVisibility() == View.VISIBLE) {
            back_tv.setVisibility(View.GONE);
            moreTextView.setText(R.string.more);
        } else {
            back_tv.setVisibility(View.VISIBLE);
            moreTextView.setText(R.string.less);
        }
    }

    @InjectView(R.id.moreTextView)
    TextView moreTextView;

    @InjectView(R.id.barcode_img)
    ImageView barcode_img;

    @InjectView(R.id.logo_img)
    ImageView logo_img;

    @InjectView(R.id.thumbnail_img)
    ImageView thumbnail_img;

    @InjectView(R.id.strip_img)
    ImageView strip_img;

    @InjectView(R.id.back_fields)
    TextView back_tv;

    @InjectView(R.id.front_field_container)
    ViewGroup frontFieldsContainer;

    @InjectView(R.id.barcode_alt_text)
    TextView barcodeAlternatvieText;

    @Override
    protected void onResume() {

        if (!optionalPass.isPresent()) {
            return;
        }

        AXT.at(this).disableRotation();


        contentView = getLayoutInflater().inflate(R.layout.activity_pass_view, null);
        setContentView(contentView);

        final View passExtrasView = getLayoutInflater().inflate(R.layout.pass_view_extra_data, null);
        final ViewGroup extraViewContainer = (ViewGroup) contentView.findViewById(R.id.passExtrasContainer);
        extraViewContainer.addView(passExtrasView);

        ButterKnife.inject(this);

        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();

        final Pass pass = optionalPass.get();

        if (!pass.isValid()) { // don't deal with invalid passes
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
                            new ExportProblemPassToLigiAndFinishTask(PassViewActivity.this, pass.getId(), App.getShareDir(), "share.pkpass").execute();
                        }
                    })
                    .show();
            return;
        }




        if (pass.getBarCode().isPresent()) {
            final int smallestSide = AXT.at(getWindowManager()).getSmallestSide();
            final Bitmap bitmap = pass.getBarCode().get().getBitmap(smallestSide / 3);
            setBitmapSafe(barcode_img, Optional.fromNullable(bitmap));
            if (pass.getBarCode().get().getAlternativeText().isPresent()) {
                barcodeAlternatvieText.setText(pass.getBarCode().get().getAlternativeText().get());
                barcodeAlternatvieText.setVisibility(View.VISIBLE);
            } else {
                barcodeAlternatvieText.setVisibility(View.GONE);
            }
        } else {
            setBitmapSafe(barcode_img, Optional.<Bitmap>absent());
        }

        setBitmapSafe(logo_img, pass.getLogoBitmap());

        logo_img.setBackgroundColor(pass.getBackGroundColor());

        setBitmapSafe(thumbnail_img, pass.getThumbnailImage());
        setBitmapSafe(strip_img, pass.getStripImage());

        if (findViewById(R.id.map_container) != null) {
            if (!(pass.getLocations().size() > 0 && PassbookMapsFacade.init(this))) {
                findViewById(R.id.map_container).setVisibility(View.GONE);
            }
        }

        if (pass.getType() != null) {
            frontFieldsContainer.removeAllViews();
            addFrontFields(pass.getHeaderFields());
            addFrontFields(pass.getPrimaryFields());
            addFrontFields(pass.getSecondaryFields());
            addFrontFields(pass.getAuxiliaryFields());
        }

        String back_str = "";

        if (App.isDeveloperMode()) {
            back_str += getPassDebugInfo(pass);
        }


        if (pass.getBackFields().size() != 0) {
            back_str += PassVisualizer.getFieldListAsString(pass.getBackFields());
            back_tv.setText(Html.fromHtml(back_str));
            moreTextView.setVisibility(View.VISIBLE);
        } else {
            moreTextView.setVisibility(View.GONE);
        }

        Linkify.addLinks(back_tv, Linkify.ALL);
        PassVisualizer.visualize(this, pass, getWindow().getDecorView());
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AXT.at(this).disableRotation();

        setContentView(R.layout.activity_pass_view);

        final View passExtrasView = getLayoutInflater().inflate(R.layout.pass_view_extra_data, null);
        final ViewGroup extraViewContainer = (ViewGroup) findViewById(R.id.passExtrasContainer);
        extraViewContainer.addView(passExtrasView);

        ButterKnife.inject(this);

    }

    private void addFrontFields(PassFieldList passFields) {
        for (PassField field : passFields) {
            View v = getLayoutInflater().inflate(R.layout.main_field_item, null);
            TextView key = (TextView) v.findViewById(R.id.key);
            key.setText(field.label);
            TextView value = (TextView) v.findViewById(R.id.value);
            value.setText(field.value);
            frontFieldsContainer.addView(v);
        }
    }

    private static void setBitmapSafe(ImageView imageView, Optional<Bitmap> bitmapOptional) {

        if (bitmapOptional.isPresent()) {
            imageView.setImageBitmap(bitmapOptional.get());
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

    }

    private String getPassDebugInfo(Pass pass) {

        String result = ""; // TODO bring back sth like passbook.getPlainJsonString();

        for (File f : new File(pass.getId()).listFiles()) {
            result += f.getName() + "<br/>";
        }

        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean res = super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_map).setVisible((optionalPass.isPresent() && optionalPass.get().isValid() && optionalPass.get().getLocations().size() > 0));
        menu.findItem(R.id.menu_update).setVisible((optionalPass.isPresent() && optionalPass.get().isValid() &&
                optionalPass.get().getAuthToken().isPresent() && optionalPass.get().getSerial().isPresent()
        ));//&& optionalPass.get().getPassIdent().isPresent()));
        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_item, menu);
        getMenuInflater().inflate(R.menu.update, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            final Intent upIntent = NavUtils.getParentActivityIntent(this);
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


    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

}
