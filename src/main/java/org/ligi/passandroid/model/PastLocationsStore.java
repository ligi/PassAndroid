package org.ligi.passandroid.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.ligi.passandroid.Tracker;

import java.util.HashSet;
import java.util.Set;

public class PastLocationsStore {
    public static final String KEY_PAST_LOCATIONS = "past_locations";
    public static final int MAX_ELEMENTS = 5;
    private final Context context;

    public PastLocationsStore(Context context) {
        this.context = context;
    }

    @TargetApi(11)
    public void putLocation(final String path) {
        if (Build.VERSION.SDK_INT < 11) {
            // feature not available for these versions
            return;
        }
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final Set<String> pastLocations = prefs.getStringSet(KEY_PAST_LOCATIONS, new HashSet<String>());

        if (pastLocations.size() > MAX_ELEMENTS) {
            deleteOneElementFromSet(pastLocations);
        }

        if (!pastLocations.contains(path)) {
            pastLocations.add(path);
        }

        Tracker.get().trackEvent("scan", "put location", "count", (long) pastLocations.size());
        prefs.edit().putStringSet(KEY_PAST_LOCATIONS, pastLocations).commit();
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
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(KEY_PAST_LOCATIONS, new HashSet<String>());
    }
}
