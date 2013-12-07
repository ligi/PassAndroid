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
        TextView titleTextView = ButterKnife.findById(res, R.id.title);
        TextView dateTextView = ButterKnife.findById(res, R.id.date);
        TextView colorIndicator = ButterKnife.findById(res, R.id.colorIndicator);
        ImageView categoryIndicator = ButterKnife.findById(res,R.id.categoryImage);

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

        if (passbook.type!=null) {
            String typeLowerCase = passbook.type.toLowerCase();
            if (typeLowerCase.contains("oarding")) {
                categoryIndicator.setImageResource(R.drawable.cat_boarding_top);
                colorIndicator.setText("BP");
            } else if (typeLowerCase.contains("event")) {
                categoryIndicator.setImageResource(R.drawable.cat_event_crop);
                colorIndicator.setText("T");
            } else if (typeLowerCase.contains("coupon")) {
                colorIndicator.setText("%");
                categoryIndicator.setImageResource(R.drawable.cat_coupon_crop);
            }else if (typeLowerCase.contains("generic")) {
                colorIndicator.setText("G");
                categoryIndicator.setImageResource(R.drawable.cat_generic_crop);
            }else if (typeLowerCase.contains("store")) {
                colorIndicator.setText("SC");
                categoryIndicator.setImageResource(R.drawable.cat_store_crop);
            }
        }

        colorIndicator.setBackgroundColor(passbook.backgroundColor);
        colorIndicator.setTextColor(passbook.foregroundColor);
        titleTextView.setText(passbook.name);

        if (passbook.relevantDate != null) {
            dateTextView.setText(DateUtils.getRelativeDateTimeString(res.getContext(), passbook.relevantDate.getMillis(), DateUtils.HOUR_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));
        }

    }

    public static String getFieldListAsString(List<Passbook.Field> fieldList) {
        String result = "";
        for (Passbook.Field f : fieldList) {
            result += "<b>" + f.label + "</b>: " + f.value + "<br/>";
        }
        return result;
    }
}
