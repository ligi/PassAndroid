package org.ligi.passandroid;

import android.app.Application;

import com.squareup.otto.Bus;

import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

public class App extends Application {

    private static Bus bus;
    private static Settings settings;
    private static PassStore passStore;

    @Override
    public void onCreate() {
        super.onCreate();

        Tracker.get().init(this);
        initTraceDroid();

        bus = new Bus();
        settings = new Settings(this);
        passStore = new PassStore(this);
    }

    private void initTraceDroid() {
        TraceDroid.init(this);
        Log.setTAG("TicketViewer");
    }

    public static boolean isDeveloperMode() {
        return false;
    }

    public static Bus getBus() {
        return bus;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static PassStore getPassStore() {
        return passStore;
    }
}
