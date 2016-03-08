package org.ligi.passandroid.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
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

import org.ligi.axt.AXT;
import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.BarcodeHelper;
import org.ligi.passandroid.maps.PassbookMapsFacade;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassField;
import org.ligi.passandroid.model.PassFieldList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassViewActivity extends PassViewActivityBase {

    @OnClick(R.id.barcode_img)
    void onBarcodeClick() {
        AXT.at(this).startCommonIntent().activityFromClass(FullscreenBarcodeActivity.class);
    }

    @OnClick(R.id.moreTextView)
    void onMoreClick() {
        if (back_tv.getVisibility() == View.VISIBLE) {
            back_tv.setVisibility(View.GONE);
            moreTextView.setText(R.string.nav_main_more);
        } else {
            back_tv.setVisibility(View.VISIBLE);
            moreTextView.setText(R.string.nav_main_less);
        }
    }

    @Bind(R.id.moreTextView)
    TextView moreTextView;

    @Bind(R.id.barcode_img)
    ImageView barcode_img;

    @Bind(R.id.logo_img)
    ImageView logo_img;

    @Bind(R.id.footer_img)
    ImageView footer_img;

    @Bind(R.id.thumbnail_img)
    ImageView thumbnail_img;

    @Bind(R.id.strip_img)
    ImageView strip_img;

    @Bind(R.id.back_fields)
    TextView back_tv;

    @Bind(R.id.front_field_container)
    ViewGroup frontFieldsContainer;

    @Bind(R.id.barcode_alt_text)
    TextView barcodeAlternativeText;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @OnClick(R.id.zoomIn)
    void zoomIn() {
        setBarCodeSize(currentBarcodeWidth + getFingerSize());
    }

    @OnClick(R.id.zoomOut)
    void zooOut() {
        setBarCodeSize(currentBarcodeWidth - getFingerSize());
    }

    @Bind(R.id.zoomOut)
    View zoomOut;

    @Bind(R.id.zoomIn)
    View zoomIn;

    private void setBarCodeSize(int width) {
        if (width < getFingerSize() * 2) {
            zoomOut.setVisibility(View.INVISIBLE);
        } else {
            zoomOut.setVisibility(View.VISIBLE);
        }

        if (width > getWindowWidth() - getFingerSize() * 2) {
            zoomIn.setVisibility(View.INVISIBLE);
        } else {
            zoomIn.setVisibility(View.VISIBLE);
        }

        currentBarcodeWidth = width;
        assert (optionalPass.getBarCode() != null); // we will not setBarCodeSize otherwise
        final boolean quadratic = BarcodeHelper.isBarcodeFormatQuadratic(optionalPass.getBarCode().getFormat());
        barcode_img.setLayoutParams(new LinearLayout.LayoutParams(width, quadratic ? width : ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @SuppressWarnings("deprecation")
    private int getWindowWidth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final Point point = new Point();
            getWindowManager().getDefaultDisplay().getSize(point);
            return point.x;
        } else {
            return getWindowManager().getDefaultDisplay().getWidth();
        }
    }

    int currentBarcodeWidth;

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

            final BitmapDrawable bitmapDrawable = pass.getBarCode().getBitmap(getResources());

            if (bitmapDrawable != null) {
                barcode_img.setImageDrawable(bitmapDrawable);
            } else {
                barcode_img.setVisibility(View.GONE);
            }

            if (pass.getBarCode().getAlternativeText() != null) {
                barcodeAlternativeText.setText(pass.getBarCode().getAlternativeText());
                barcodeAlternativeText.setVisibility(View.VISIBLE);
            } else {
                barcodeAlternativeText.setVisibility(View.GONE);
            }

            setBarCodeSize(smallestSide / 2);
        } else {
            setBitmapSafe(barcode_img, null);
            zoomIn.setVisibility(View.GONE);
            zoomOut.setVisibility(View.GONE);
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
    }

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

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        configureActionBar();
        refresh();
    }


    private void addFrontFields(PassFieldList passFields) {
        for (PassField field : passFields) {

            final View v = getLayoutInflater().inflate(R.layout.main_field_item, frontFieldsContainer, false);
            final TextView key = (TextView) v.findViewById(R.id.key);
            key.setText(field.label);
            final TextView value = (TextView) v.findViewById(R.id.value);
            value.setText(field.value);

            frontFieldsContainer.addView(v);
        }
    }

    private void setBitmapSafe(ImageView imageView, Bitmap bitmap) {

        if (bitmap != null) {
            imageView.setLayoutParams(getLayoutParamsSoThatWeHaveMinimumAFingerInHeight(imageView, bitmap));

            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            imageView.requestLayout();
        } else {
            imageView.setVisibility(View.GONE);
        }

    }

    @NonNull
    private ViewGroup.LayoutParams getLayoutParamsSoThatWeHaveMinimumAFingerInHeight(final ImageView imageView, final Bitmap bitmap) {
        final int halfAFingerInPixels = getFingerSize();
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (bitmap.getHeight() < halfAFingerInPixels) {
            params.height = halfAFingerInPixels;
        } else {
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        return params;
    }

    private int getFingerSize() {
        return getResources().getDimensionPixelSize(R.dimen.finger);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final boolean res = super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_map).setVisible((optionalPass != null && optionalPass.isValid() && optionalPass.getLocations().size() > 0));
        menu.findItem(R.id.menu_update).setVisible(mightPassBeAbleToUpdate(optionalPass));
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
