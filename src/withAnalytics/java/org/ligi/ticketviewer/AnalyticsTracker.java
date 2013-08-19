package org.ligi.ticketviewer;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;


public class AnalyticsTracker implements TrackerInterface {
    @Override
    public void init(Context ctx) {
        EasyTracker.getInstance().setContext(ctx);
    }

    @Override
    public void trackException(String s, Exception e, boolean fatal) {
        EasyTracker.getTracker().trackException(s,e,fatal);
    }

    @Override
    public void trackException(String s, boolean fatal) {
        EasyTracker.getTracker().trackException(s,fatal);
    }

    @Override
    public void trackEvent(String category, String action, String label, Long val) {
        EasyTracker.getTracker().trackEvent(category,action,label, val);
    }

    @Override
    public void activityStart(FragmentActivity activity) {
        EasyTracker.getInstance().activityStart(activity);
    }

    @Override
    public void activityStop(FragmentActivity activity) {
        EasyTracker.getInstance().activityStop(activity);
    }
}
