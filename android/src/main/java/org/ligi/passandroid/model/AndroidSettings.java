package org.ligi.passandroid.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.ligi.passandroid.model.comparator.PassSortOrder;

import java.io.File;

public class AndroidSettings implements Settings {
    public static final String ORDER_KEY = "order";
    public final Context context;

    final SharedPreferences sharedPreferences;

    public AndroidSettings(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void setSortOrder(PassSortOrder order) {
        sharedPreferences.edit().putInt(ORDER_KEY, order.getInt()).apply();
    }

    @Override
    public PassSortOrder getSortOrder() {
        int id = sharedPreferences.getInt(ORDER_KEY, 0);
        for (PassSortOrder order : PassSortOrder.values()) {
            if (order.getInt() == id) {
                return order;
            }
        }
        return PassSortOrder.DATE;
    }

    @Override
    public boolean doTraceDroidEmailSend() {
        // will be overridden in test-module
        return true;
    }

    @Override
    public String getPassesDir() {
        return context.getFilesDir().getAbsolutePath() + "/passes";
    }

    @Override
    public File getStateDir() {
        return new File(context.getFilesDir(), "state");
    }


    @Override
    public String getShareDir() {
        return Environment.getExternalStorageDirectory() + "/tmp/passbook_share_tmp/";
    }

}
