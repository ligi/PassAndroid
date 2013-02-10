package org.ligi.ticketviewer;

import android.os.Bundle;
import org.ligi.ticketviewer.TicketViewActivityBase;

import java.lang.Override;

/**
 * User: ligi
 * Date: 2/10/13
 * Time: 10:25 PM
 */
public class FullscreenMapActivity extends TicketViewActivityBase{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_map);
    }
}
