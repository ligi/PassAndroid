package org.ligi.passandroid;

import org.ligi.tracedroid.logging.Log;

public class NotTracker implements Tracker {

    @Override
    public void trackException(String s, Throwable e, boolean fatal) {
        if (fatal) {
            Log.w("Fatal Exception " + s + " " + e);
        } else {
            Log.w("Not Fatal Exception " + s + " " + e);
        }
    }

    @Override
    public void trackException(String s, boolean fatal) {
        if (fatal) {
            Log.w("Fatal Exception " + s);
        } else {
            Log.w("Not Fatal Exception " + s);
        }
    }

    @Override
    public void trackEvent(String category, String action, String label, Long val) {

    }
}
