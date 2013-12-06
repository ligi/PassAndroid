package org.ligi.passandroid.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ligi.passandroid.R;
import org.ligi.passandroid.model.Passbook;
import org.ligi.passandroid.model.ReducedPassInformation;

import java.util.List;

import butterknife.ButterKnife;

public class PassVisualizer {
    public static void visualize(ReducedPassInformation passbook, View res) {
        TextView tv = ButterKnife.findById(res, R.id.label);
        TextView more_tv = ButterKnife.findById(res, R.id.descr);
        TextView date_tv = ButterKnife.findById(res, R.id.date);
        View colorIndicator = ButterKnife.findById(res, R.id.colorIndicator);


        int size = (int) res.getResources().getDimension(R.dimen.pass_icon_size);
        ImageView icon_img = (ImageView) res.findViewById(R.id.icon);

        if (passbook.iconPath != null) {
            Bitmap ico = BitmapFactory.decodeFile(passbook.iconPath);

            if (ico != null) {
                icon_img.setImageBitmap(Bitmap.createScaledBitmap(ico, size, size, false));
            } else {
                icon_img.setImageResource(R.drawable.ic_launcher);
            }
        }

        colorIndicator.setBackgroundColor(passbook.backgroundColor);
        tv.setText(passbook.type);

        if (passbook.relevantDate != null) {
            date_tv.setText(DateUtils.getRelativeDateTimeString(res.getContext(), passbook.relevantDate.getMillis(), DateUtils.HOUR_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));
            //date_tv.setText(passbook.relevantDate.to);
        }
        more_tv.setText(passbook.name);

    }

    public static String getFieldListAsString(List<Passbook.Field> fieldList) {
        String result = "";
        for (Passbook.Field f : fieldList) {
            result += "<b>" + f.label + "</b>: " + f.value + "<br/>";
        }
        return result;
    }
}
