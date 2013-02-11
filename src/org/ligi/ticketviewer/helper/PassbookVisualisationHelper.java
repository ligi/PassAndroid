package org.ligi.ticketviewer.helper;

import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ligi.ticketviewer.PassbookParser;
import org.ligi.ticketviewer.R;

import java.io.File;

/**
 * User: ligi
 * Date: 2/11/13
 * Time: 7:02 PM
 */
public class PassbookVisualisationHelper {
    public static void visualizePassbookData(PassbookParser passbookParser, View res) {
        TextView tv = (TextView) res.findViewById(R.id.label);
        TextView more_tv = (TextView) res.findViewById(R.id.descr);

        try {
            JSONObject pass_json = new JSONObject(FileHelper.file2String(new File(passbookParser.getPath() + "/pass.json")));

            int size = (int) res.getResources().getDimension(R.dimen.pass_icon_size);
            ImageView icon_img = (ImageView) res.findViewById(R.id.icon);

            if (passbookParser.getPath() != null) {
                Bitmap ico = passbookParser.getIconBitmap();

                if (ico != null)
                    icon_img.setImageBitmap(Bitmap.createScaledBitmap(ico, size, size, false));
                else
                    icon_img.setImageBitmap(Bitmap.createScaledBitmap(ico, size, size, false));
            }

            icon_img.setBackgroundColor(passbookParser.getBgcolor());


            tv.setText(pass_json.getString("description"));
            String more_str = "";

            String ticket_kind = null;

            String[] types = {"coupon", "eventTicket", "boardingPass", "generic", "storeCard"};

            for (String type : types) {
                if (pass_json.has(type))
                    ticket_kind = type;

            }

            if (ticket_kind != null) {
                JSONObject eventTicket = pass_json.getJSONObject(ticket_kind);

                if (eventTicket.has("primaryFields")) {
                    JSONArray pri_arr = eventTicket.getJSONArray("primaryFields");
                    for (int i = 0; i < pri_arr.length(); i++) {
                        JSONObject sec_obj = pri_arr.getJSONObject(i);
                        more_str += "<b>" + sec_obj.getString("label") + "</b>:" + sec_obj.getString("value") + "<br/>";
                    }
                }
                if (eventTicket.has("secondaryFields")) {
                    JSONArray sec_arr = eventTicket.getJSONArray("secondaryFields");
                    for (int i = 0; i < sec_arr.length(); i++) {
                        JSONObject sec_obj = sec_arr.getJSONObject(i);
                        more_str += "<b>" + sec_obj.getString("label") + "</b>: " + sec_obj.getString("value") + "<br/>";
                    }
                }
            }

            more_tv.setText(Html.fromHtml(more_str));


        } catch (Exception e) {

        }
    }
}
