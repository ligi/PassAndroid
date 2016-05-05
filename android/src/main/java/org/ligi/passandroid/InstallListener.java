package org.ligi.passandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import org.ligi.passandroid.ui.PassImportActivity;

public class InstallListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String rawReferrerString = intent.getStringExtra("referrer");
        if (rawReferrerString != null) {

            final Intent newIntent = new Intent(context, PassImportActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.setData(Uri.parse(rawReferrerString));

            context.startActivity(newIntent);
        }
    }

}
