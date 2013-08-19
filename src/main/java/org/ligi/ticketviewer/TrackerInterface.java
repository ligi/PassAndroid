package org.ligi.ticketviewer;

import android.app.Activity;
import android.content.Context;

public interface TrackerInterface {

    public void init(Context ctx);

    void trackException(String s, Exception e, boolean fatal);

    void trackException(String s, boolean fatal);

    void trackEvent(String category, String action, String label, Long val);

    void activityStart(Activity activity);

    void activityStop(Activity activity);
}
