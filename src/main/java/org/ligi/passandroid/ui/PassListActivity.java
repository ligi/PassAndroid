package org.ligi.passandroid.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidquery.service.MarketService;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.TrackerInterface;
import org.ligi.passandroid.events.NavigationOpenedEvent;
import org.ligi.passandroid.events.SortOrderChangeEvent;
import org.ligi.passandroid.events.TypeFocusEvent;
import org.ligi.passandroid.helper.PassUtil;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.PassStore;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

public class PassListActivity extends ActionBarActivity {

    private PassAdapter passAdapter;

    private ActionBarDrawerToggle drawerToggle;

    @InjectView(R.id.content_list)
    RecyclerView recyclerView;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;

    @InjectView(R.id.fab)
    FloatingActionButton fab;

    @OnClick(R.id.fab)
    void foo() {
        new MaterialDialog.Builder(this)
                .title("Pass Source")
                .items(R.array.items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        switch (i) {
                            case 0: // scan
                                final Intent intent = new Intent(PassListActivity.this, SearchPassesIntentService.class);
                                startService(intent);
                                break;

                            case 1: // demo-pass
                                AXT.at(PassListActivity.this).startCommonIntent().openUrl("http://ligi.de/passandroid_samples/index.html");
                                break;

                            case 2: // add
                                final FiledPass pass = PassUtil.createEmptyPass();
                                App.getPassStore().setCurrentPass(pass);
                                pass.save(App.getPassStore());
                                AXT.at(PassListActivity.this).startCommonIntent().activityFromClass(PassEditActivity.class);
                                break;

                        }
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    private NavigationFragment navigationFragment;

    @Subscribe
    public void sortOrderChange(SortOrderChangeEvent orderChangeEvent) {
        refreshPasses();
    }

    @Subscribe
    public void typeFocus(TypeFocusEvent typeFocusEvent) {
        scrollToType(typeFocusEvent.type);
        drawer.closeDrawers();
    }

    public void refreshPasses() {
        final PassStore passStore = App.getPassStore();
        passStore.preCachePassesList();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                passStore.refreshPassesList();
                passStore.sort(App.getSettings().getSortOrder());

                passAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.pass_list);
        ButterKnife.inject(this);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.scrollToPosition(0);
        recyclerView.setLayoutManager(llm);

        // don't want too many windows in worst case - so check for errors first
        if (TraceDroid.getStackTraceFiles().length > 0) {
            Tracker.get().trackEvent("ui_event", "send", "stacktraces", null);
            TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
        } else { // if no error - check if there is a new version of the app
            Tracker.get().trackEvent("ui_event", "processFile", "updatenotice", null);
            MarketService ms = new MarketService(this);
            ms.level(MarketService.MINOR).checkVersion();

            AppRate.with(this)
                    .retryPolicy(RetryPolicy.EXPONENTIAL)
                    .initialLaunchCount(5)
                    .checkAndShow();
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                App.getBus().post(new NavigationOpenedEvent());
            }
        };

        drawer.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    private void scrollToType(String type) {
        for (int i = 0; i < passAdapter.getItemCount(); i++) {
            if (App.getPassStore().getPassbookAt(i).getTypeNotNull().equals(type)) {
                recyclerView.scrollToPosition(i);
                return; // we are done
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fab.attachToRecyclerView(recyclerView);
        new InitAsyncTask().execute();

        App.getBus().register(this);
    }

    private class InitAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            App.getPassStore();
            return null;
        }

        @Override
        protected void onPreExecute() {
            updateUIRegardingToUIState();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isFinishing()) {
                return;
            }

            passAdapter = new PassAdapter(PassListActivity.this);
            recyclerView.setAdapter(passAdapter);

            Tracker.get().trackEvent("ui_event", "resume", "passes", (long) App.getPassStore().passCount());

            refreshPasses();

            if (navigationFragment == null) {
                navigationFragment = new NavigationFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.left_drawer, navigationFragment).commitAllowingStateLoss();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_pass_list_view, menu);
        return true;
    }

    public void updateUIRegardingToUIState() {
        supportInvalidateOptionsMenu();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        App.getBus().unregister(this);
        super.onPause();
    }

}