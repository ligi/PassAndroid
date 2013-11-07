package org.ligi.ticketviewer.ui;

import android.os.Bundle;
import android.widget.ImageView;

import org.ligi.ticketviewer.R;

public class FullscreenImageActivity extends TicketViewActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen_image);
        ImageView iv = (ImageView) findViewById(R.id.fullscreen_image);

        iv.setImageBitmap(passbookParser.getBarcodeBitmap());
    }

}
