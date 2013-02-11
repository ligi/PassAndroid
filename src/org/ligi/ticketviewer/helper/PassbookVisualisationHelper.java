package org.ligi.ticketviewer.helper;

import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.ligi.ticketviewer.PassbookParser;
import org.ligi.ticketviewer.R;

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
            //JSONObject pass_json = new JSONObject(FileHelper.file2String(new File(passbookParser.getPath() + "/pass.json")));

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


            tv.setText(passbookParser.getDescription());

            String more_str = "";


            if (passbookParser.getType() != null) {
                for (PassbookParser.Field f : passbookParser.getPrimaryFields())
                    more_str += "<b>" + f.label + "</b>: " + f.value + "<br/>";
                for (PassbookParser.Field f : passbookParser.getSecondaryFields())
                    more_str += "<b>" + f.label + "</b>: " + f.value + "<br/>";
            }

            more_tv.setText(Html.fromHtml(more_str));


        } catch (Exception e) {

        }
    }
}
