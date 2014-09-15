package org.ligi.passandroid.ui;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;

import org.ligi.axt.AXT;
import org.ligi.passandroid.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FullscreenBarcodeActivity extends PassViewActivityBase {

    @InjectView(R.id.fullscreen_barcode)
    ImageView barcodeImageView;

    @InjectView(R.id.alternativeBarcodeText)
    TextView alternativeBarcodeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (optionalPass.isPresent()) {
            setContentView(R.layout.fullscreen_image);

            ButterKnife.inject(this);

            final int smallestSize = AXT.at(getWindowManager()).getSmallestSide();

            setBestFittingOrientationForBarCode();

            barcodeImageView.setImageBitmap(optionalPass.get().getBarCode().get().getBitmap(smallestSize));

            if (optionalPass.get().getBarCode().get().getAlternativeText().isPresent()) {
                alternativeBarcodeText.setVisibility(View.VISIBLE);
                alternativeBarcodeText.setText(optionalPass.get().getBarCode().get().getAlternativeText().get());
            } else {
                alternativeBarcodeText.setVisibility(View.GONE);
            }
        } else {
            finish(); // this should never happen, but better safe than sorry
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * QR and AZTEC are best fit in Portrait
     * PDF417 is best viewed in Landscape
     * <p/>
     * main work is to avoid changing if we are already optimal
     * ( reverse orientation / sensor is the problem here ..)
     */
    private void setBestFittingOrientationForBarCode() {

        if (optionalPass.get().getBarCode().get().getFormat() == BarcodeFormat.PDF_417) {
            switch (getRequestedOrientation()) {

                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE:
                    return; // do nothing

                default:
                    AXT.at(this).lockOrientation(Configuration.ORIENTATION_LANDSCAPE);
            }

        } else { // QR and AZTEC are square -> best fit is portrait
            switch (getRequestedOrientation()) {

                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT:
                    return; // do nothing

                default:
                    AXT.at(this).lockOrientation(Configuration.ORIENTATION_PORTRAIT);
            }

        }
    }

    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

}
