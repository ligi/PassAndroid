package org.ligi.passandroid.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

public class Settings {
    public static final String ORDER_KEY = "order";
    public final Context context;

    private final SharedPreferences sharedPreferences;

    public Settings(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setSortOrder(PassStore.SortOrder order) {
        sharedPreferences.edit().putInt(ORDER_KEY, order.getInt()).apply();
    }

    public PassStore.SortOrder getSortOrder() {
        int id = sharedPreferences.getInt(ORDER_KEY, 0);
        for (PassStore.SortOrder order : PassStore.SortOrder.values()) {
            if (order.getInt() == id) {
                return order;
            }
        }
        return PassStore.SortOrder.DATE;
    }

    public boolean doTraceDroidEmailSend() {
        // will be overridden in test-module
        return true;
    }

    public String getPassesDir() {
        return context.getFilesDir().getAbsolutePath() + "/passes";
    }

    public String getShareDir() {
        return Environment.getExternalStorageDirectory() + "/tmp/passbook_share_tmp/";
    }

}
