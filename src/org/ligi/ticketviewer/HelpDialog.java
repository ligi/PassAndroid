package org.ligi.ticketviewer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * User: ligi
 * Date: 2/11/13
 * Time: 10:25 PM
 */
public class HelpDialog {

    public static void show(Context c) {

        EasyTracker.getTracker().trackEvent("ui_action", "help", "open", null);

        TextView tv = new TextView(c);
        tv.setPadding(10, 10, 10, 10);
        tv.setText(Html.fromHtml(c.getString(R.string.help_content)));

        Linkify.addLinks(tv, Linkify.ALL);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        new AlertDialog.Builder(c).setTitle(R.string.help_label).setView(tv)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }
}
