package org.ligi.passandroid.ui;

import android.content.Intent;
import android.net.Uri;
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

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.NavigationOpenedEvent;
import org.ligi.passandroid.events.SortOrderChangeEvent;
import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.ui.views.CategoryIndicatorView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NavigationFragment extends Fragment {

    private static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=org.ligi.passandroid";
    private int lastDisplayedStoreCount = -1;

    @OnClick(R.id.rate)
    void rateClick() {
        openURL(PLAY_STORE_URL);
    }

    @OnClick(R.id.community)
    void community() {
        openURL("https://plus.google.com/communities/116353894782342292067");
    }

    @OnClick(R.id.share)
    void share() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, PLAY_STORE_URL);
        intent.setType("text/plain");
        startActivity(intent);
    }

    private void openURL(String url) {
        new Intent();
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @InjectView(R.id.radioGroup)
    RadioGroup radioGroup;

    @InjectView(R.id.categoryRadioButton)
    RadioButton categoryRadioButton;

    @InjectView(R.id.dateRadioButton)
    RadioButton dateRadioButton;

    @InjectView(R.id.navCategoriesInner)
    ViewGroup categoriesContainer;

    @InjectView(R.id.navCategoriesOuter)
    ViewGroup categoriesContainerOuter;

    public NavigationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        ButterKnife.inject(this, view);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (dateRadioButton.isChecked()) {
                    App.getSettings().setSortOrder(PassStore.SortOrder.DATE);
                } else if (categoryRadioButton.isChecked()) {
                    App.getSettings().setSortOrder(PassStore.SortOrder.TYPE);
                }
                App.getBus().post(new SortOrderChangeEvent());
            }
        });

        switch (App.getSettings().getSortOrder()) {
            case DATE:
                dateRadioButton.setChecked(true);
                break;
            case TYPE:
                categoryRadioButton.setChecked(true);
                break;
        }

        createCategoryJumpMarks(inflater);

        App.getBus().register(this);

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

    private void createCategoryJumpMarks(LayoutInflater inflater) {

        List<PassStore.CountedType> countedTypes = App.getPassStore().getCountedTypes();

        if (lastDisplayedStoreCount == App.getPassStore().passCount()) {
            return; // nothing new - all displayed
        }

        if (countedTypes.size() > 1) {
            categoriesContainerOuter.setVisibility(View.VISIBLE);
        } else {
            categoriesContainerOuter.setVisibility(View.GONE);
            return;
        }

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
                    Intent i = new Intent(getActivity(), TicketListActivity.class);
                    i.putExtra("typeFocus", type);
                    startActivity(i);
                }
            });

            categoriesContainer.addView(item);
        }
    }

}
