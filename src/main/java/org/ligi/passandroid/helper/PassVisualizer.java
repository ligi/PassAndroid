package org.ligi.passandroid.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Optional;

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
    public static void visualize(final Activity activity, final Pass pass, final View container) {
        final TextView titleTextView = findById(container, R.id.title);
        final TextView dateTextView = findById(container, R.id.date);

        final CategoryIndicatorView categoryIndicator = findById(container, R.id.categoryView);

        if (pass.getLocations().size() > 0) {
            findById(container, R.id.navigateTo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigateToLocationsDialog.perform(activity, App.getPassStore().getPassbookForId(pass.getId()), false);
                }
            });
        } else {
            findById(container, R.id.navigateTo).setVisibility(View.GONE);
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
            findById(container, R.id.addCalendar).setOnClickListener(new View.OnClickListener() {
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
            findById(container, R.id.addCalendar).setVisibility(View.GONE);
        }

        if (dateForIntent == null && !(pass.getLocations().size() > 0)) {
            findById(container, R.id.actionsContainer).setVisibility(View.GONE);
        }

        final ImageView icon_img = findById(container, R.id.icon);

        icon_img.setBackgroundColor(pass.getBackGroundColor());

        final Optional<Bitmap> iconBitmap = pass.getIconBitmap();
        if (iconBitmap.isPresent()) {
            final int size = (int) container.getResources().getDimension(R.dimen.pass_icon_size);
            icon_img.setImageBitmap(Bitmap.createScaledBitmap(iconBitmap.get(), size, size, false));
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
            final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(container.getContext(), pass.getRelevantDate().get().getMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
            dateTextView.setText(relativeDateTimeString);
        } else if (pass.getExpirationDate().isPresent()) {
            final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(container.getContext(), pass.getExpirationDate().get().getMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);

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
