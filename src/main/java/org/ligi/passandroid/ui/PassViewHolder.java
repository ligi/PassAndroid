package org.ligi.passandroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.provider.CalendarContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.joda.time.DateTime;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.ui.views.CategoryIndicatorView;

public class PassViewHolder extends RecyclerView.ViewHolder {

    final CardView root;

    @InjectView(R.id.icon)
    ImageView icon;

    @InjectView(R.id.date)
    TextView date;

    @InjectView(R.id.title)
    TextView title;

    @InjectView(R.id.categoryView)
    CategoryIndicatorView category;

    @InjectView(R.id.actions_separator)
    View actionsSeparator;

    @InjectView(R.id.navigateTo)
    TextView navigateTo;

    @InjectView(R.id.addCalendar)
    TextView addCalendar;

    public PassViewHolder(View view) {
        super(view);
        root = (CardView) view;
        ButterKnife.inject(this, view);
    }

    public void apply(final Pass pass, final Activity activity) {

        final DateTime dateForIntent;

        if (pass.getRelevantDate() != null) {

            dateForIntent = pass.getRelevantDate();
        } else if (pass.getExpirationDate() != null) {
            dateForIntent = pass.getExpirationDate();
        } else {
            dateForIntent = null;
        }

        final boolean noButtons = dateForIntent == null && !(pass.getLocations().size() > 0);
        if (noButtons) {
            actionsSeparator.setVisibility(View.GONE);
        }

        if (pass.getLocations().size() > 0) {
            navigateTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigateToLocationsDialog.perform(activity, App.getPassStore().getPassbookForId(pass.getId()), false);
                }
            });
            navigateTo.setVisibility(View.VISIBLE);
        } else {
            navigateTo.setVisibility(View.INVISIBLE);
        }

        if (dateForIntent != null) {
            addCalendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateForIntent.getMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dateForIntent.getMillis() + 60 * 60 * 1000);
                    intent.putExtra("title", pass.getDescription());
                    activity.startActivity(intent);
                }
            });
            addCalendar.setVisibility(View.VISIBLE);
        } else {
            addCalendar.setVisibility(View.INVISIBLE);
        }

        final Bitmap iconBitmap = pass.getIconBitmap();

        if (iconBitmap != null) {
            final int size = (int) root.getResources().getDimension(R.dimen.pass_icon_size);
            icon.setImageBitmap(Bitmap.createScaledBitmap(iconBitmap, size, size, false));
        } else {
            final ColorDrawable colorDrawable = new ColorDrawable(pass.getBackGroundColor());
            icon.setImageDrawable(colorDrawable);
        }

        if (pass.getType() != null) {
            category.setImageByCategory(pass.getType());
            category.setExtraTextToCatShortString(pass.getType());
        }

        category.setTextBackgroundColor(pass.getBackGroundColor());
        category.setTextColor(pass.getForegroundColor());

        title.setText(pass.getDescription());

        if (pass.getRelevantDate()!=null) {
            final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(root.getContext(),
                                                                                            pass.getRelevantDate().getMillis(),
                                                                                            DateUtils.MINUTE_IN_MILLIS,
                                                                                            DateUtils.WEEK_IN_MILLIS,
                                                                                            0);
            date.setText(relativeDateTimeString);
            date.setVisibility(View.VISIBLE);
        } else if (pass.getExpirationDate()!=null) {
            date.setVisibility(View.VISIBLE);
            final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(root.getContext(),
                                                                                            pass.getExpirationDate().getMillis(),
                                                                                            DateUtils.MINUTE_IN_MILLIS,
                                                                                            DateUtils.WEEK_IN_MILLIS,
                                                                                            0);

            if (pass.getExpirationDate().isAfterNow()) {
                date.setText("expires " + relativeDateTimeString);
            } else {
                date.setText("expired " + relativeDateTimeString);
            }
        } else {
            date.setVisibility(View.GONE);
        }


    }
}
