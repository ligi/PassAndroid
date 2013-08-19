package org.ligi.ticketviewer;

import android.app.Activity;
import android.content.Context;

public class NotTracker implements TrackerInterface {
    @Override
    public void init(Context ctx) {
        
    }

    @Override
    public void trackException(String s, Exception e, boolean fatal) {

    }

    @Override
    public void trackException(String s, boolean fatal) {

    }

    @Override
    public void trackEvent(String category, String action, String label, Long val) {

    }

    @Override
    public void activityStart(Activity activity) {

    }

    @Override
    public void activityStop(Activity activity) {

    }
}
