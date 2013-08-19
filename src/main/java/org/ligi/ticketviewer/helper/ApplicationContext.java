package org.ligi.ticketviewer.helper;

import android.app.Application;

import org.ligi.ticketviewer.Tracker;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

public class ApplicationContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Tracker.get().init(this);

        TraceDroid.init(this);
        Log.setTAG("TicketViewer");
        Log.i("TicketViewer starting");
    }


}
