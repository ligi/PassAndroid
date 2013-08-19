package org.ligi.ticketviewer;

public class Tracker {

    private static NotTracker instance;

    public static TrackerInterface get() {
        if (instance==null) {
            instance=new NotTracker();
        }
        return instance;
    }
}
