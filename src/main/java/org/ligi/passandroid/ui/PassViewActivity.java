package org.ligi.passandroid.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import org.ligi.axt.AXT;
import org.ligi.passandroid.R;
import org.ligi.passandroid.maps.PassbookMapsFacade;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassField;
import org.ligi.passandroid.model.PassFieldList;

public class PassViewActivity extends PassViewActivityBase {

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

    @InjectView(R.id.footer_img)
    ImageView footer_img;

    @InjectView(R.id.thumbnail_img)
    ImageView thumbnail_img;

    @InjectView(R.id.strip_img)
    ImageView strip_img;

    @InjectView(R.id.back_fields)
    TextView back_tv;

    @InjectView(R.id.front_field_container)
    ViewGroup frontFieldsContainer;

    @InjectView(R.id.barcode_alt_text)
    TextView barcodeAlternativeText;

    @Override
    protected void onResume() {
        super.onResume();

        if (optionalPass == null) {
            return;
        }

        AXT.at(this).disableRotation();

        final View contentView = getLayoutInflater().inflate(R.layout.activity_pass_view, null);
        setContentView(contentView);

        final ViewGroup extraViewContainer = (ViewGroup) contentView.findViewById(R.id.passExtrasContainer);
        final View passExtrasView = getLayoutInflater().inflate(R.layout.pass_view_extra_data, extraViewContainer, false);
        extraViewContainer.addView(passExtrasView);

        ButterKnife.inject(this);

        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();

        final Pass pass = optionalPass;

        if (!pass.isValid()) { // don't deal with invalid passes
            showPassProblemDialog(pass, "invalid");
            return;
        }

        if (pass.getBarCode() != null) {
            final int smallestSide = AXT.at(getWindowManager()).getSmallestSide();
            final Bitmap bitmap = pass.getBarCode().getBitmap(smallestSide / 3);
            setBitmapSafe(barcode_img, bitmap);
            if (pass.getBarCode().getAlternativeText() != null) {
                barcodeAlternativeText.setText(pass.getBarCode().getAlternativeText());
                barcodeAlternativeText.setVisibility(View.VISIBLE);
            } else {
                barcodeAlternativeText.setVisibility(View.GONE);
            }
        } else {
            setBitmapSafe(barcode_img, null);
        }

        setBitmapSafe(logo_img, pass.getBitmap(Pass.BITMAP_LOGO));
        setBitmapSafe(footer_img, pass.getBitmap(Pass.BITMAP_FOOTER));

        setBitmapSafe(thumbnail_img, pass.getBitmap(Pass.BITMAP_THUMBNAIL));
        setBitmapSafe(strip_img, pass.getBitmap(Pass.BITMAP_STRIP));

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

        final StringBuilder back_str = new StringBuilder();

        if (pass.getBackFields().size() != 0) {
            back_str.append(pass.getBackFields().toHTMLString());
            back_tv.setText(Html.fromHtml(back_str.toString()));
            moreTextView.setVisibility(View.VISIBLE);
        } else {
            moreTextView.setVisibility(View.GONE);
        }

        Linkify.addLinks(back_tv, Linkify.ALL);

        new PassViewHolder(findViewById(R.id.pass_card)).apply(pass, this);
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AXT.at(this).disableRotation();

        setContentView(R.layout.activity_pass_view);

        final ViewGroup extraViewContainer = (ViewGroup) findViewById(R.id.passExtrasContainer);
        getLayoutInflater().inflate(R.layout.pass_view_extra_data, extraViewContainer);

        ButterKnife.inject(this);

    }

    private void addFrontFields(PassFieldList passFields) {
        for (PassField field : passFields) {

            final View v = getLayoutInflater().inflate(R.layout.main_field_item, frontFieldsContainer,false);
            final TextView key = (TextView) v.findViewById(R.id.key);
            key.setText(field.label);
            final TextView value = (TextView) v.findViewById(R.id.value);
            value.setText(field.value);

            frontFieldsContainer.addView(v);
        }
    }

    private static void setBitmapSafe(ImageView imageView, Bitmap bitmap) {

        if (bitmap != null) {
            imageView.setLayoutParams(getLayoutParamsSoThatWeHaveAtLeasHalfAFinger(imageView, bitmap));

            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            imageView.requestLayout();
        } else {
            imageView.setVisibility(View.GONE);
        }

    }

    @NonNull
    private static ViewGroup.LayoutParams getLayoutParamsSoThatWeHaveAtLeasHalfAFinger(final ImageView imageView, final Bitmap bitmap) {
        final Context context = imageView.getContext();
        final int halfAFingerInPixels = context.getResources().getDimensionPixelSize(R.dimen.finger)/2 ;
        final ViewGroup.LayoutParams params=imageView.getLayoutParams();
        if (bitmap.getHeight()< halfAFingerInPixels) {
            params.height= halfAFingerInPixels;

        } else {
            params.height= LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        return params;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean res = super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_map).setVisible((optionalPass != null && optionalPass.isValid() && optionalPass.getLocations().size() > 0));
        menu.findItem(R.id.menu_update).setVisible((optionalPass != null &&
                                                    optionalPass.isValid() &&
                                                    optionalPass.getAuthToken() != null &&
                                                    optionalPass.getSerial() != null));//&& optionalPass.get().getPassIdent().isPresent()));
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
                TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
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
