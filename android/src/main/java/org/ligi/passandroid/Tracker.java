package org.ligi.passandroid;


import androidx.annotation.Nullable;

public interface Tracker {
    void trackException(String s, Throwable e, boolean fatal);

    void trackException(String s, boolean fatal);

    void trackEvent(@Nullable String category, @Nullable String action, @Nullable String label, @Nullable Long val);
}
