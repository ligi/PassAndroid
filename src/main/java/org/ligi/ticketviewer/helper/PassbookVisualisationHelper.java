package org.ligi.ticketviewer.helper;

import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ligi.ticketviewer.R;
import org.ligi.ticketviewer.model.PassbookParser;

import java.util.List;

public class PassbookVisualisationHelper {
    public static void visualizePassbookData(PassbookParser passbookParser, View res, boolean verbose) {
        TextView tv = (TextView) res.findViewById(R.id.label);
        TextView more_tv = (TextView) res.findViewById(R.id.descr);

        try {
            //JSONObject pass_json = new JSONObject(FileHelper.file2String(new File(passbookParser.getPath() + "/pass.json")));

            int size = (int) res.getResources().getDimension(R.dimen.pass_icon_size);
            ImageView icon_img = (ImageView) res.findViewById(R.id.icon);

            if (passbookParser.getPath() != null) {
                Bitmap ico = passbookParser.getIconBitmap();

                if (ico != null) {
                    icon_img.setImageBitmap(Bitmap.createScaledBitmap(ico, size, size, false));
                } else {
                    icon_img.setImageBitmap(Bitmap.createScaledBitmap(ico, size, size, false));
                }
            }

            icon_img.setBackgroundColor(passbookParser.getBgcolor());

            tv.setTextColor(passbookParser.getFGcolor());
            more_tv.setTextColor(passbookParser.getFGcolor());

            tv.setText(passbookParser.getDescription());

            String more_str = "";

            if (passbookParser.getType() != null) {
                more_str += getFieldListAsString(passbookParser.getPrimaryFields());
                more_str += getFieldListAsString(passbookParser.getSecondaryFields());
                more_str += getFieldListAsString(passbookParser.getHeaderFields());

                if (verbose) {
                    more_str += getFieldListAsString(passbookParser.getAuxiliaryFields());
                }
            }

            more_tv.setText(Html.fromHtml(more_str));

        } catch (Exception e) {
        }
    }

    public static String getFieldListAsString(List<PassbookParser.Field> fieldList) {
        String result = "";
        for (PassbookParser.Field f : fieldList) {
            result += "<b>" + f.label + "</b>: " + f.value + "<br/>";
        }
        return result;
    }
}
