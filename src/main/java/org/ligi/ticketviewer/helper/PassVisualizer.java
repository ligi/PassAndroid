package org.ligi.ticketviewer.helper;

import android.graphics.Bitmap;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.ligi.ticketviewer.R;
import org.ligi.ticketviewer.model.PassbookParser;

import java.util.List;

import butterknife.ButterKnife;

public class PassVisualizer {
    public static void visualize(PassbookParser passbookParser, View res) {
        TextView tv = ButterKnife.findById(res, R.id.label);
        TextView more_tv = ButterKnife.findById(res, R.id.descr);
        TextView date_tv = ButterKnife.findById(res, R.id.date);
        View colorIndicator = ButterKnife.findById(res, R.id.colorIndicator);


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

        colorIndicator.setBackgroundColor(passbookParser.getBackGroundColor());
        tv.setText(passbookParser.getType());

        if (passbookParser.getRelevantDate() != null) {
            date_tv.setText(DateUtils.getRelativeDateTimeString(res.getContext(), new DateTime(passbookParser.getRelevantDate()).getMillis(), DateUtils.HOUR_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));
        }
        more_tv.setText(passbookParser.getDescription());


    }

    public static String getFieldListAsString(List<PassbookParser.Field> fieldList) {
        String result = "";
        for (PassbookParser.Field f : fieldList) {
            result += "<b>" + f.label + "</b>: " + f.value + "<br/>";
        }
        return result;
    }
}
