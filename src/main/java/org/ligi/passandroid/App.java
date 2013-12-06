package org.ligi.passandroid;

import android.app.Application;

import com.squareup.otto.Bus;

import org.ligi.passandroid.model.Settings;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

public class App extends Application {

    private static Bus bus;
    private static Settings settings;

    @Override
    public void onCreate() {
        super.onCreate();

        bus = new Bus();
        settings = new Settings(this);

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

    public static Bus getBus() {
        return bus;
    }

    public static Settings getSettings() {
        return settings;
    }
}
