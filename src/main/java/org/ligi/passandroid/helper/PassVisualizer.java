package org.ligi.passandroid.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassField;
import org.ligi.passandroid.model.PassFieldList;
import org.ligi.passandroid.ui.NavigateToLocationsDialog;
import org.ligi.passandroid.ui.views.CategoryIndicatorView;

import static butterknife.ButterKnife.findById;

public class PassVisualizer {
    public static void visualize(final Activity activity, final Pass pass, View res) {
        final TextView titleTextView = findById(res, R.id.title);
        final TextView dateTextView = findById(res, R.id.date);

        final CategoryIndicatorView categoryIndicator = findById(res, R.id.categoryView);

        if (pass.getLocations().size() > 0) {
            findById(res, R.id.navigateTo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String lang = activity.getResources().getConfiguration().locale.getLanguage();
                    NavigateToLocationsDialog.perform(activity, App.getPassStore().getPassbookForId(pass.getId(), lang), false);
                }
            });
        } else {
            findById(res, R.id.navigateTo).setVisibility(View.GONE);
        }

        final DateTime dateForIntent;

        if (pass.getRelevantDate().isPresent()) {

            dateForIntent = pass.getRelevantDate().get();
        } else if (pass.getExpirationDate().isPresent()) {
            dateForIntent = pass.getExpirationDate().get();
        } else {
            dateForIntent = null;
        }

        if (dateForIntent != null) {
            findById(res, R.id.addCalendar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateForIntent.getMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dateForIntent.getMillis() + 60 * 60 * 1000);
                    intent.putExtra("title", pass.getDescription());
                    activity.startActivity(intent);
                }
            });
        } else {
            findById(res, R.id.addCalendar).setVisibility(View.GONE);
        }

        if (dateForIntent == null && !(pass.getLocations().size() > 0)) {
            findById(res, R.id.actionsContainer).setVisibility(View.GONE);
        }

        int size = (int) res.getResources().getDimension(R.dimen.pass_icon_size);
        final ImageView icon_img = findById(res, R.id.icon);

        icon_img.setBackgroundColor(pass.getBackGroundColor());

        if (pass.getIconBitmap().isPresent()) {
            icon_img.setImageBitmap(Bitmap.createScaledBitmap(pass.getIconBitmap().get(), size, size, false));
        } else {
            icon_img.setImageResource(R.drawable.ic_launcher);
        }


        if (pass.getType() != null) {
            categoryIndicator.setImageByCategory(pass.getType());
            categoryIndicator.setExtraTextToCatShortString(pass.getType());
        }

        categoryIndicator.setTextBackgroundColor(pass.getBackGroundColor());
        categoryIndicator.setTextColor(pass.getForegroundColor());

        titleTextView.setText(pass.getDescription());

        if (pass.getRelevantDate().isPresent()) {
            final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(res.getContext(), pass.getRelevantDate().get().getMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
            dateTextView.setText(relativeDateTimeString);
        } else if (pass.getExpirationDate().isPresent()) {
            final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(res.getContext(), pass.getExpirationDate().get().getMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);

            if (pass.getExpirationDate().get().isAfterNow()) {
                dateTextView.setText("expires " + relativeDateTimeString);
            } else {
                dateTextView.setText("expired " + relativeDateTimeString);
            }
        } else {
            dateTextView.setVisibility(View.GONE);
        }

    }

    public static String getFieldListAsString(PassFieldList fieldList) {
        final StringBuilder result = new StringBuilder();
        for (PassField f : fieldList) {
            result.append("<b>" + f.label + "</b>: " + f.value + "<br/>");
        }
        return result.toString();
    }
}
