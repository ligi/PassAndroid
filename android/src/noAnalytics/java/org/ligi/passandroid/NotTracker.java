package org.ligi.passandroid;

import javax.annotation.Nullable;
import timber.log.Timber;

public class NotTracker implements Tracker {

    @Override
    public void trackException(String s, Throwable e, boolean fatal) {
        if (fatal) {
            Timber.w(e, "Fatal Exception %s", s);
        } else {
            Timber.w(e, "Not Fatal Exception %s", s);
        }
    }

    @Override
    public void trackException(String s, boolean fatal) {
        if (fatal) {
            Timber.w("Fatal Exception %s", s);
        } else {
            Timber.w("Not Fatal Exception %s", s);
        }
    }

    @Override
    public void trackEvent(@Nullable String category, @Nullable String action,
                           @Nullable String label, @Nullable Long val) {

    }
}
