package org.ligi.passandroid.ui;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.zxing.BarcodeFormat;
import org.ligi.axt.AXT;
import org.ligi.passandroid.R;
import org.ligi.tracedroid.logging.Log;

public class FullscreenBarcodeActivity extends PassViewActivityBase {

    @Bind(R.id.fullscreen_barcode)
    ImageView barcodeImageView;

    @Bind(R.id.alternativeBarcodeText)
    TextView alternativeBarcodeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_image);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final int smallestSize = AXT.at(getWindowManager()).getSmallestSide();
        if (optionalPass == null || optionalPass.getBarCode() == null) {
            Log.w("FullscreenBarcodeActivity in bad state");
            finish(); // this should never happen, but better safe than sorry
            return;
        }
        setBestFittingOrientationForBarCode();

        barcodeImageView.setImageBitmap(optionalPass.getBarCode().getBitmap(smallestSize));

        if (optionalPass.getBarCode().getAlternativeText() != null) {
            alternativeBarcodeText.setVisibility(View.VISIBLE);
            alternativeBarcodeText.setText(optionalPass.getBarCode().getAlternativeText());
        } else {
            alternativeBarcodeText.setVisibility(View.GONE);
        }

    }

    /**
     * QR and AZTEC are best fit in Portrait
     * PDF417 is best viewed in Landscape
     * <p/>
     * main work is to avoid changing if we are already optimal
     * ( reverse orientation / sensor is the problem here ..)
     */
    private void setBestFittingOrientationForBarCode() {

        if (optionalPass.getBarCode().getFormat() == BarcodeFormat.PDF_417) {
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
