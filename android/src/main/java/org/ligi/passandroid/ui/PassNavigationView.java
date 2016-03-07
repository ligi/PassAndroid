package org.ligi.passandroid.ui;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.PassClassifier;
import org.ligi.passandroid.model.PassStore;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PassNavigationView extends NavigationView implements PassClassifier.OnClassificationChangeListener {

    @Bind(R.id.pass_count_header)
    TextView passCountTextView;

    @Bind(R.id.topic_count_header)
    TextView topicCountTextView;

    @Inject
    PassStore passStore;

    public PassNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_plus:
                        AXT.at(getContext()).startCommonIntent().openUrl("https://plus.google.com/communities/116353894782342292067");
                        return true;

                    case R.id.menu_github:
                        AXT.at(getContext()).startCommonIntent().openUrl("https://github.com/ligi/PassAndroid");
                        return true;

                    case R.id.menu_share:
                        AXT.at(getContext()).startCommonIntent().shareUrl(getMarketUrl());
                        return true;

                    case R.id.menu_beta:
                        AXT.at(getContext()).startCommonIntent().openUrl("https://play.google.com/apps/testing/org.ligi.passandroid");
                        return true;

                    case R.id.menu_settings:
                        AXT.at(getContext()).startCommonIntent().activityFromClass(PreferenceActivity.class);
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    public View inflateHeaderView(@LayoutRes int res) {
        final View view = super.inflateHeaderView(res);

        ButterKnife.bind(this,view);

        App.component().inject(this);

        passStore.getClassifier().onClassificationChangeListeners.add(this);
        OnClassificationChange();
        return view;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        passStore.getClassifier().onClassificationChangeListeners.remove(this);
    }

    private String getMarketUrl() {
        return getContext().getString(R.string.market_url, getContext().getPackageName());
    }

    @Override
    public void OnClassificationChange() {
        final int passCount = passStore.getPassList().size();
        passCountTextView.setText(getContext().getString(R.string.passes_nav, passCount));

        final int topicCount = passStore.getClassifier().getTopics().size();
        topicCountTextView.setText(getContext().getString(R.string.categories_nav, topicCount));
    }
}
