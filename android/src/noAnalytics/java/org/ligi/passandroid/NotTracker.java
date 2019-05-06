package org.ligi.passandroid;

import org.ligi.tracedroid.logging.Log;

import javax.annotation.Nullable;

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
    public void trackEvent(@Nullable String category, @Nullable String action,
                           @Nullable String label, @Nullable Long val) {

    }
}
