package org.ligi.passandroid;

import android.content.Context;

public class Tracker {

    private static AnalyticsTracker instance;


    public static void init(Context context) {
        instance = new AnalyticsTracker(context);
    }

    public static TrackerInterface get() {
        if (instance == null) {
            throw new IllegalArgumentException("Tracker not initialized but get() called");
        }
        return instance;
    }

}
