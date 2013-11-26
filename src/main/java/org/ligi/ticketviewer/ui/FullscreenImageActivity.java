package org.ligi.ticketviewer.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;

import org.ligi.axt.AXT;
import org.ligi.ticketviewer.R;

public class FullscreenImageActivity extends TicketViewActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen_image);
        ImageView iv = (ImageView) findViewById(R.id.fullscreen_image);

        int smallestSize = AXT.at(getWindowManager()).getSmallestSide();

        setBestFittingOrientationForBarCode();

        iv.setImageBitmap(passbookParser.getBarcodeBitmap(smallestSize));
    }

    private void setBestFittingOrientationForBarCode() {
        if (passbookParser.getBarcodeFormat() == BarcodeFormat.PDF_417) {
            AXT.at(this).lockOrientation(Configuration.ORIENTATION_LANDSCAPE);
        } else { // QR and AZTEC are square -> best fit is portrait
            AXT.at(this).lockOrientation(Configuration.ORIENTATION_PORTRAIT);
        }
    }

}
