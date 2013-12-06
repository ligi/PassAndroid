package org.ligi.passandroid.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ligi.passandroid.R;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class NavigationFragment extends Fragment {

    @OnClick(R.id.rate)
    void rateClick() {
        openURL("https://play.google.com/store/apps/details?id=org.ligi.passandroid");
    }

    @OnClick(R.id.community)
    void community() {
        openURL("https://plus.google.com/communities/116353894782342292067");
    }

    private void openURL(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnCheckedChanged(R.id.categoryRadioButton)
    void onSortCategoryCheckedChange() {

    }

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
        return view;
    }

}
