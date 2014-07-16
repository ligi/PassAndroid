package org.ligi.passandroid;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.squareup.otto.Bus;

import org.ligi.passandroid.model.AndroidFileSystemPassStore;
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

        Tracker.init(this);
        initTraceDroid();

        bus = new Bus();
        settings = new Settings(this);
        passStore = new AndroidFileSystemPassStore(this);
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

    public static void replacePassStore(PassStore newPassStore) {
        passStore = newPassStore;
    }

    public static String getPassesDir(final Context ctx) {
        return ctx.getFilesDir().getAbsolutePath() + "/passes";
    }

    public static String getShareDir() {
        return Environment.getExternalStorageDirectory() + "/tmp/passbook_share_tmp/";
    }
}
