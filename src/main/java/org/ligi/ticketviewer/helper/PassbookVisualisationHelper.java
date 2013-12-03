package org.ligi.ticketviewer.helper;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ligi.ticketviewer.R;
import org.ligi.ticketviewer.model.PassbookParser;

import java.util.List;

public class PassbookVisualisationHelper {
    public static void visualizePassbookData(PassbookParser passbookParser, View res) {
        TextView tv = (TextView) res.findViewById(R.id.label);
        TextView more_tv = (TextView) res.findViewById(R.id.descr);
        View colorIndicator = res.findViewById(R.id.colorIndicator);

        try {
            //JSONObject pass_json = new JSONObject(FileHelper.file2String(new File(passbookParser.getPath() + "/pass.json")));

            int size = (int) res.getResources().getDimension(R.dimen.pass_icon_size);
            ImageView icon_img = (ImageView) res.findViewById(R.id.icon);

            if (passbookParser.getPath() != null) {
                Bitmap ico = passbookParser.getIconBitmap();

                if (ico != null) {
                    icon_img.setImageBitmap(Bitmap.createScaledBitmap(ico, size, size, false));
                } else {
                    icon_img.setImageResource(R.drawable.ic_launcher);
                }
            }

            //res.setBackgroundResource(R.drawable.pkbox);
            //icon_img.setBackgroundColor(passbookParser.getBackGroundColor());

            //tv.setTextColor(passbookParser.getForegroundColor());
            //more_tv.setTextColor(passbookParser.getForegroundColor());

            colorIndicator.setBackgroundColor(passbookParser.getBackGroundColor());
            tv.setText(passbookParser.getType());

            String more_str = "";

            /*

*/

            more_tv.setText(passbookParser.getDescription());


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
