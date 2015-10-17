package org.ligi.passandroid;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import org.ligi.tracedroid.logging.Log;

public class NotTracker implements TrackerInterface {

    @Override
    public void trackException(String s, Exception e, boolean fatal) {
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
