package org.ligi.passandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.ligi.axt.AXT;
import org.ligi.axt.listeners.RepeatedOnClicksListener;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.NavigationOpenedEvent;
import org.ligi.passandroid.events.SortOrderChangeEvent;
import org.ligi.passandroid.events.TypeFocusEvent;
import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.ui.views.CategoryIndicatorView;
import org.ligi.tracedroid.logging.Log;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NavigationFragment extends Fragment {

    private int lastDisplayedStoreCount = -1;

    @OnClick(R.id.community)
    void community() {
        AXT.at(getActivity()).startCommonIntent().openUrl("https://plus.google.com/communities/116353894782342292067");
    }

    @OnClick(R.id.share)
    void share() {
        AXT.at(getActivity()).startCommonIntent().shareUrl(getMarketUrl());
    }

    @InjectView(R.id.radioGroup)
    RadioGroup radioGroup;

    @InjectView(R.id.categoryRadioButton)
    RadioButton categoryRadioButton;

    @InjectView(R.id.dateAscRadioButton)
    RadioButton dateAscRadioButton;

    @InjectView(R.id.dateDescRadioButton)
    RadioButton dateDescRadioButton;

    @InjectView(R.id.navCategoriesInner)
    ViewGroup categoriesContainer;

    @InjectView(R.id.navCategoriesOuter)
    ViewGroup categoriesContainerOuter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        ButterKnife.inject(this, view);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (dateAscRadioButton.isChecked()) {
                    App.getSettings().setSortOrder(PassStore.SortOrder.DATE_ASC);
                } else if (dateDescRadioButton.isChecked()) {
                    App.getSettings().setSortOrder(PassStore.SortOrder.DATE_DESC);
                } else if (categoryRadioButton.isChecked()) {
                    App.getSettings().setSortOrder(PassStore.SortOrder.TYPE);
                }
                setCategoryNavVisibilityByCurrentConditions();
                App.getBus().post(new SortOrderChangeEvent());
            }
        });

        switch (App.getSettings().getSortOrder()) {
            case DATE_ASC:
                dateAscRadioButton.setChecked(true);
                break;
            case DATE_DESC:
                dateDescRadioButton.setChecked(true);
                break;
            case TYPE:
                categoryRadioButton.setChecked(true);
                break;
        }

        createCategoryJumpMarks(inflater);

        App.getBus().register(this);

        dateAscRadioButton.setOnClickListener(new RepeatedOnClicksListener(7, new View.OnClickListener() {
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
        App.getBus().unregister(this);
    }

    @Subscribe
    public void onNavigationOpened(NavigationOpenedEvent event) {
        createCategoryJumpMarks(LayoutInflater.from(getActivity()));
    }

    private boolean shouldDisplaCategoryNav() {
        if (App.getPassStore().getCountedTypes().size() < 2) { // navigation only makes sense with >=2 Elements
            return false;
        }

        if (App.getSettings().getSortOrder() != PassStore.SortOrder.TYPE) { // nav only makes sense if we order by category
            return false;
        }

        return true; // we are good to give this user this feature
    }

    private void setCategoryNavVisibilityByCurrentConditions() {
        if (shouldDisplaCategoryNav()) {
            categoriesContainerOuter.setVisibility(View.VISIBLE);
        } else {
            categoriesContainerOuter.setVisibility(View.GONE);
        }
    }

    private void createCategoryJumpMarks(LayoutInflater inflater) {

        List<PassStore.CountedType> countedTypes = App.getPassStore().getCountedTypes();

        if (lastDisplayedStoreCount == App.getPassStore().passCount()) {
            return; // nothing new - all displayed
        }

        setCategoryNavVisibilityByCurrentConditions();

        categoriesContainer.removeAllViews();

        for (PassStore.CountedType countedType : countedTypes) {
            final String type = countedType.type;

            View item = inflater.inflate(R.layout.item_nav_pass_category, null);

            // gather views
            CategoryIndicatorView categoryIndicatorView = ButterKnife.findById(item, R.id.categoryView);
            TextView labelText = ButterKnife.findById(item, R.id.navCategoryLabel);

            categoryIndicatorView.setExtraText(String.valueOf(countedType.count));
            categoryIndicatorView.setTextBackgroundColor(CategoryHelper.getCategoryDefaultBG(type));
            categoryIndicatorView.setTextColor(CategoryHelper.getCategoryDefaultFG(type));
            categoryIndicatorView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

            categoryIndicatorView.setImageByCategory(type);

            labelText.setText(CategoryHelper.getHumanCategoryString(type));

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.getBus().post(new TypeFocusEvent(type));
                }
            });

            categoriesContainer.addView(item);
        }
    }

    private String getMarketUrl() {
        return getString(R.string.market_url, getActivity().getPackageName());
    }

}
