package org.ligi.passandroid.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.PassField;
import org.ligi.passandroid.model.PassFieldList;
import org.ligi.passandroid.model.ReducedPassInformation;
import org.ligi.passandroid.ui.NavigateToLocationsDialog;
import org.ligi.passandroid.ui.views.CategoryIndicatorView;

import static butterknife.ButterKnife.findById;

public class PassVisualizer {
    public static void visualize(final Activity activity, final ReducedPassInformation passbook, View res) {
        final TextView titleTextView = findById(res, R.id.title);
        final TextView dateTextView = findById(res, R.id.date);

        final CategoryIndicatorView categoryIndicator = findById(res, R.id.categoryView);

        if (passbook.hasLocation) {
            findById(res, R.id.navigateTo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigateToLocationsDialog.perform(activity, App.getPassStore().getPassbookForId(passbook.id), false);
                }
            });
        } else {
            findById(res, R.id.navigateTo).setVisibility(View.GONE);
        }

        final DateTime dateForIntent;

        if (passbook.relevantDate.isPresent()) {
            dateForIntent = passbook.relevantDate.get();
        } else if (passbook.expirationDate.isPresent()) {
            dateForIntent = passbook.expirationDate.get();
        } else {
            dateForIntent = null;
        }

        if (dateForIntent != null) {
            findById(res, R.id.addCalendar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra("beginTime", dateForIntent.getMillis());
                    intent.putExtra("endTime", dateForIntent.getMillis() + 60 * 60 * 1000);
                    intent.putExtra("title", passbook.name);
                    activity.startActivity(intent);
                }
            });
        } else {
            findById(res, R.id.addCalendar).setVisibility(View.GONE);
        }

        if (dateForIntent == null && !passbook.hasLocation) {
            findById(res, R.id.actionsContainer).setVisibility(View.GONE);
        }

        int size = (int) res.getResources().getDimension(R.dimen.pass_icon_size);
        final ImageView icon_img = findById(res, R.id.icon);

        icon_img.setBackgroundColor(passbook.backgroundColor);

        if (passbook.iconPath != null) {
            Bitmap ico = BitmapFactory.decodeFile(passbook.iconPath);

            if (ico != null) {
                icon_img.setImageBitmap(Bitmap.createScaledBitmap(ico, size, size, false));
            } else {
                icon_img.setImageResource(R.drawable.ic_launcher);
            }
        }

        if (passbook.type != null) {
            categoryIndicator.setImageByCategory(passbook.type);
            categoryIndicator.setExtraTextToCatShortString(passbook.type);
        }

        categoryIndicator.setTextBackgroundColor(passbook.backgroundColor);
        categoryIndicator.setTextColor(passbook.foregroundColor);

        titleTextView.setText(passbook.name);

        if (passbook.relevantDate.isPresent()) {
            final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(res.getContext(), passbook.relevantDate.get().getMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
            dateTextView.setText(relativeDateTimeString);
        } else if (passbook.expirationDate.isPresent()) {
            final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(res.getContext(), passbook.expirationDate.get().getMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
            dateTextView.setText("expires " + relativeDateTimeString);
        } else {
            dateTextView.setVisibility(View.GONE);
        }

    }

    public static String getFieldListAsString(PassFieldList fieldList) {
        String result = "";
        for (PassField f : fieldList) {
            result += "<b>" + f.label + "</b>: " + f.value + "<br/>";
        }
        return result;
    }
}
