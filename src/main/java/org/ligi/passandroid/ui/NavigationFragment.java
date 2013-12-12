package org.ligi.passandroid.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.SortOrderChangeEvent;
import org.ligi.passandroid.model.PassStore;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NavigationFragment extends Fragment {

    private static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=org.ligi.passandroid";

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

    public NavigationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        return view;
    }

}
