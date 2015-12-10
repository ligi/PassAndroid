package org.ligi.passandroid.model;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.ligi.passandroid.Tracker;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public class PastLocationsStore {
    public static final String KEY_PAST_LOCATIONS = "past_locations";
    public static final int MAX_ELEMENTS = 5;

    private final SharedPreferences sharedPreferences;
    private final Tracker tracker;

    @Inject
    public PastLocationsStore(SharedPreferences sharedPreferences, Tracker tracker) {
        this.sharedPreferences = sharedPreferences;
        this.tracker = tracker;
    }

    @TargetApi(11)
    public void putLocation(final String path) {
        if (Build.VERSION.SDK_INT < 11) {
            // feature not available for these versions
            return;
        }
        final Set<String> pastLocations = sharedPreferences.getStringSet(KEY_PAST_LOCATIONS, new HashSet<String>());

        if (pastLocations.size() >= MAX_ELEMENTS) {
            deleteOneElementFromSet(pastLocations);
        }

        if (!pastLocations.contains(path)) {
            pastLocations.add(path);
        }

        tracker.trackEvent("scan", "put location", "count", (long) pastLocations.size());
        sharedPreferences.edit().putStringSet(KEY_PAST_LOCATIONS, pastLocations).apply();
    }

    private void deleteOneElementFromSet(Set<String> pastLocations) {
        final int deleteAtPosition = (int) (Math.random() * MAX_ELEMENTS);
        int pos = 0;
        for (String location : pastLocations) {
            if (pos == deleteAtPosition) {
                pastLocations.remove(location);
                return;
            }
            pos++;
        }
    }

    @TargetApi(11)
    public Set<String> getLocations() {
        if (Build.VERSION.SDK_INT < 11) {
            // feature not available for these versions
            return new HashSet<>();
        }
        return sharedPreferences.getStringSet(KEY_PAST_LOCATIONS, new HashSet<String>());
    }
}
