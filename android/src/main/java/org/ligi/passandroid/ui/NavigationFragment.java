package org.ligi.passandroid.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.ligi.axt.AXT;
import org.ligi.axt.listeners.RepeatedOnClicksListener;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.NavigationOpenedEvent;
import org.ligi.passandroid.events.SortOrderChangeEvent;
import org.ligi.passandroid.events.TypeFocusEvent;
import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.CountedType;
import org.ligi.passandroid.model.PassSortOrder;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;
import org.ligi.passandroid.ui.views.CategoryIndicatorView;
import org.ligi.tracedroid.logging.Log;

import java.util.Set;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class NavigationFragment extends Fragment {

    @Inject
    PassStore passStore;

    @Inject
    Settings settings;

    @Inject
    Bus bus;

    @OnClick(R.id.community)
    void community() {
        AXT.at(getActivity()).startCommonIntent().openUrl("https://plus.google.com/communities/116353894782342292067");
    }

    @OnClick(R.id.share)
    void share() {
        AXT.at(getActivity()).startCommonIntent().shareUrl(getMarketUrl());
    }

    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;

    @Bind(R.id.categoryRadioButton)
    RadioButton categoryRadioButton;

    @Bind(R.id.dateRadioButton)
    RadioButton dateRadioButton;

    @Bind(R.id.navCategoriesInner)
    ViewGroup categoriesContainer;

    @Bind(R.id.navCategoriesOuter)
    View categoriesContainerOuter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        ButterKnife.bind(this, view);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (dateRadioButton.isChecked()) {
                    settings.setSortOrder(PassSortOrder.DATE);
                } else if (categoryRadioButton.isChecked()) {
                    settings.setSortOrder(PassSortOrder.TYPE);
                }
                setCategoryNavVisibilityByCurrentConditions();
                bus.post(new SortOrderChangeEvent());
            }
        });

        switch (settings.getSortOrder()) {
            case DATE:
                dateRadioButton.setChecked(true);
                break;
            case TYPE:
                categoryRadioButton.setChecked(true);
                break;
        }

        createCategoryJumpMarks(inflater);

        bus.register(this);

        dateRadioButton.setOnClickListener(new RepeatedOnClicksListener(7, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Log.getCachedLog());
                intent.setType("text/plain");
                startActivity(intent);
            }
        }));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bus.unregister(this);
    }

    @Subscribe
    public void onNavigationOpened(NavigationOpenedEvent event) {
        createCategoryJumpMarks(LayoutInflater.from(getActivity()));
    }

    private boolean shouldDisplayCategoryNav() {
        return shouldDisplaySort() && settings.getSortOrder() == PassSortOrder.TYPE;
    }

    private boolean shouldDisplaySort() {
        return passStore.getCountedTypes().size() >= 2;
    }

    private void setCategoryNavVisibilityByCurrentConditions() {
        categoriesContainerOuter.setVisibility(shouldDisplayCategoryNav() ? VISIBLE : GONE);
        categoriesContainer.setVisibility(shouldDisplayCategoryNav() ? VISIBLE : GONE);

        radioGroup.setVisibility(shouldDisplaySort() ? VISIBLE : GONE);
    }

    private void createCategoryJumpMarks(LayoutInflater inflater) {

        final Set<CountedType> countedTypes = passStore.getCountedTypes();

        setCategoryNavVisibilityByCurrentConditions();

        categoriesContainer.removeAllViews();

        for (CountedType countedType : countedTypes) {
            final String type = countedType.type;

            final View item = inflater.inflate(R.layout.item_nav_pass_category, categoriesContainer, false);

            // gather views
            final CategoryIndicatorView categoryIndicatorView = ButterKnife.findById(item, R.id.categoryView);
            TextView labelText = ButterKnife.findById(item, R.id.navCategoryLabel);

            categoryIndicatorView.setExtraText(String.valueOf(countedType.count));
            categoryIndicatorView.setTextBackgroundColor(0xAA343434);
            categoryIndicatorView.setTextColor(Color.WHITE);
            categoryIndicatorView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

            item.setBackgroundResource(R.drawable.clickable_bg);
            categoryIndicatorView.setImageByCategory(type);

            labelText.setText(CategoryHelper.getHumanCategoryString(type));
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bus.post(new TypeFocusEvent(type));
                }
            });

            categoriesContainer.addView(item);
        }
    }

    private String getMarketUrl() {
        return getString(R.string.market_url, getActivity().getPackageName());
    }

}
