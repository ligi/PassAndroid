package org.ligi.passandroid;

import android.content.Context;

import java.lang.IllegalArgumentException;

public class Tracker {

    private static NotTracker instance;

    public static void init(Context ctx) {
        instance = new NotTracker();
    }

    public static TrackerInterface get() {
        if (instance==null) {
            throw new IllegalArgumentException("tracker not initialized but get() called");
        }
        return instance;
    }
}
