package org.ligi.passandroid.ui.pass_view_holder;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.ligi.passandroid.R;
import org.ligi.passandroid.actions.AddToCalendar;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassField;
import org.ligi.passandroid.ui.NavigateToLocationsDialog;
import org.ligi.passandroid.ui.Visibility;
import org.ligi.passandroid.ui.views.CategoryIndicatorView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public abstract class PassViewHolder extends RecyclerView.ViewHolder {

    public final CardView root;

    @Bind(R.id.icon)
    ImageView icon;

    @Bind(R.id.date)
    TextView dateOrExtraText;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.categoryView)
    CategoryIndicatorView category;

    @Bind(R.id.actions_separator)
    View actionsSeparator;

    @Bind(R.id.navigateTo)
    TextView navigateTo;

    @OnClick(R.id.navigateTo)
    void navigateTo() {
        NavigateToLocationsDialog.perform(activity, pass, false);
    }

    @Bind(R.id.addCalendar)
    TextView addCalendar;

    @OnClick(R.id.addCalendar)
    void onCalendarClick() {
        AddToCalendar.tryAddDateToCalendar(pass, activity, getDateOrExtraText());
    }

    public Pass pass;
    protected Activity activity;


    public PassViewHolder(View view) {
        super(view);
        root = (CardView) view;
        ButterKnife.bind(this, view);
    }

    public void apply(final Pass pass, final Activity activity) {
        this.pass = pass;
        this.activity = activity;

        final boolean noButtons = getDateOrExtraText() == null && !(pass.getLocations().size() > 0);

        actionsSeparator.setVisibility(getVisibilityForGlobalAndLocal(noButtons, true));
        navigateTo.setVisibility(getVisibilityForGlobalAndLocal(noButtons, (pass.getLocations().size() > 0)));
        addCalendar.setVisibility(getVisibilityForGlobalAndLocal(noButtons, getDateOrExtraText() != null));

        final Bitmap iconBitmap = pass.getBitmap(Pass.BITMAP_ICON);

        if (iconBitmap != null) {
            final int size = (int) root.getResources().getDimension(R.dimen.pass_icon_size);
            icon.setImageBitmap(Bitmap.createScaledBitmap(iconBitmap, size, size, false));
        } else {
            final ColorDrawable colorDrawable = new ColorDrawable(pass.getBackgroundColor());
            icon.setImageDrawable(colorDrawable);
        }

        if (pass.getType() != null) {
            category.setImageByCategory(pass.getType());
            category.setExtraTextToCatShortString(pass.getType());
        }

        category.setTextBackgroundColor(pass.getBackgroundColor());
        category.setTextColor(pass.getForegroundColor());

        title.setText(pass.getDescription());


    }

    @Nullable
    protected String getExtraString() {
        if (pass.getHeaderFields().size() > 0) {
            return getExtraStringForField(pass.getHeaderFields().get(0));
        }

        if (pass.getPrimaryFields().size() > 0) {
            return getExtraStringForField(pass.getPrimaryFields().get(0));
        }

        if (pass.getSecondaryFields().size() > 0) {
            return getExtraStringForField(pass.getSecondaryFields().get(0));
        }

        return null;
    }

    private String getExtraStringForField(final PassField passField) {
        return (passField.label + ": " + passField.value);
    }
    @NonNull
    private String setDateTextFromDateAndPrefix(String prefix, @NonNull final DateTime relevantDate) {
        final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(activity,
                relevantDate.getMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                0);

        return prefix + relativeDateTimeString;
    }

    @Nullable
    protected String getTimeInfoString() {
        if (pass.getRelevantDate() != null) {
            return setDateTextFromDateAndPrefix("", pass.getRelevantDate());
        } else if (pass.getExpirationDate() != null) {
            return setDateTextFromDateAndPrefix(pass.getExpirationDate().isAfterNow() ? "expires " : " expired ", pass.getExpirationDate());
        } else {
            return null;
        }
    }

    @Nullable
    private DateTime getDateOrExtraText() {
        if (pass.getRelevantDate() != null) {
            return pass.getRelevantDate();
        }
        return pass.getExpirationDate();
    }

    @Visibility
    private int getVisibilityForGlobalAndLocal(final boolean global, final boolean local) {
        if (global) {
            return GONE;
        }

        return local ? VISIBLE : INVISIBLE;
    }
}
