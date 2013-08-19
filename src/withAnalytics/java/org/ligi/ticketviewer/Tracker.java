package org.ligi.ticketviewer;

public class Tracker {

    private static AnalyticsTracker instance;

    public static TrackerInterface get() {
        if (instance==null) {
            instance=new AnalyticsTracker();
        }
        return instance;
    }
}
