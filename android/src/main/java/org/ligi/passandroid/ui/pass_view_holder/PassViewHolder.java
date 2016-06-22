package org.ligi.passandroid.ui.pass_view_holder;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.ligi.passandroid.R;
import org.ligi.passandroid.actions.AddToCalendar;
import org.ligi.passandroid.model.PassBitmapDefinitions;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.model.pass.PassField;
import org.ligi.passandroid.ui.NavigateToLocationsDialog;
import org.ligi.passandroid.ui.Visibility;
import org.ligi.passandroid.ui.views.CategoryIndicatorViewWithIcon;
import org.threeten.bp.ZonedDateTime;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public abstract class PassViewHolder extends RecyclerView.ViewHolder {

    public final CardView root;

    @BindView(R.id.date)
    TextView dateOrExtraText;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.categoryView)
    CategoryIndicatorViewWithIcon category;

    @BindView(R.id.actions_separator)
    View actionsSeparator;

    @BindView(R.id.navigateTo)
    TextView navigateTo;

    @OnClick(R.id.navigateTo)
    void navigateTo() {
        NavigateToLocationsDialog.perform(activity, pass, false);
    }

    @BindView(R.id.addCalendar)
    TextView addCalendar;

    @OnClick(R.id.addCalendar)
    void onCalendarClick() {
        AddToCalendar.tryAddDateToCalendar(pass, activity, getDateOrExtraText());
    }

    public Pass pass;
    protected Activity activity;
    protected PassStore passStore;


    public PassViewHolder(View view) {
        super(view);
        root = (CardView) view;
        ButterKnife.bind(this, view);
    }

    public void apply(final Pass pass, final PassStore passStore, final Activity activity) {
        this.pass = pass;
        this.activity = activity;
        this.passStore = passStore;

        final boolean noButtons = getDateOrExtraText() == null && !(pass.getLocations().size() > 0);

        actionsSeparator.setVisibility(getVisibilityForGlobalAndLocal(noButtons, true));
        navigateTo.setVisibility(getVisibilityForGlobalAndLocal(noButtons, pass.getLocations().size() > 0));
        addCalendar.setVisibility(getVisibilityForGlobalAndLocal(noButtons, getDateOrExtraText() != null));

        final Bitmap iconBitmap = pass.getBitmap(passStore, PassBitmapDefinitions.BITMAP_ICON);

        if (iconBitmap != null) {
            category.setIcon(iconBitmap);
        }

        category.setImageByCategory(pass.getType());

        category.setAccentColor(pass.getAccentColor());

        title.setText(pass.getDescription());


    }

    @Nullable
    protected String getExtraString() {
        if (pass.getFields().size() > 0) {
            return getExtraStringForField(pass.getFields().get(0));
        }

        return null;
    }

    private String getExtraStringForField(final PassField passField) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (passField.getLabel()!=null) {
            stringBuilder.append(passField.getLabel());

            if (passField.getValue()!=null) {
                stringBuilder.append(": ");
            }
        }

        if (passField.getValue()!=null) {
            stringBuilder.append(passField.getValue());
        }

        return stringBuilder.toString();
    }

    @NonNull
    private String setDateTextFromDateAndPrefix(String prefix, @NonNull final ZonedDateTime relevantDate) {
        final CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(activity,
                                                                                        relevantDate.toEpochSecond() * 1000,
                                                                                        DateUtils.MINUTE_IN_MILLIS,
                                                                                        DateUtils.WEEK_IN_MILLIS,
                                                                                        0);

        return prefix + relativeDateTimeString;
    }

    @Nullable
    protected String getTimeInfoString() {
        if (pass.getCalendarTimespan() != null && pass.getCalendarTimespan().getFrom() != null) {
            return setDateTextFromDateAndPrefix("", pass.getCalendarTimespan().getFrom());
        } else if (pass.getValidTimespans() != null && pass.getValidTimespans().size() > 0 && pass.getValidTimespans().get(0).getTo() != null) {
            final ZonedDateTime to = pass.getValidTimespans().get(0).getTo();
            return setDateTextFromDateAndPrefix(to.isAfter(ZonedDateTime.now()) ? "expires " : " expired ", to);
        } else {
            return null;
        }
    }

    @Nullable
    private ZonedDateTime getDateOrExtraText() {
        if (pass.getCalendarTimespan() != null && pass.getCalendarTimespan().getFrom() != null) {
            return pass.getCalendarTimespan().getFrom();
        }
        if (pass.getValidTimespans() != null && pass.getValidTimespans().size() > 0) {
            return pass.getValidTimespans().get(0).getTo();
        }
        return null;
    }

    @Visibility
    protected int getVisibilityForGlobalAndLocal(final boolean global, final boolean local) {
        if (global) {
            return GONE;
        }

        return local ? VISIBLE : INVISIBLE;
    }
}
