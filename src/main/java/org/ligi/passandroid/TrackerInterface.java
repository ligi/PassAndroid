package org.ligi.passandroid;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

public interface TrackerInterface {

    void trackException(String s, Exception e, boolean fatal);

    void trackException(String s, boolean fatal);

    void trackEvent(String category, String action, String label, Long val);

    void activityStart(FragmentActivity activity);

    void activityStop(FragmentActivity activity);
}
