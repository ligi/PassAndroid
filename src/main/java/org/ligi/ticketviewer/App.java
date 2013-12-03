package org.ligi.ticketviewer;

import android.app.Application;

import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Tracker.get().init(this);

        initTraceDroid();
    }

    private void initTraceDroid() {
        TraceDroid.init(this);
        Log.setTAG("TicketViewer");
    }

    public static boolean isDeveloperMode() {
        return true;
    }

}
