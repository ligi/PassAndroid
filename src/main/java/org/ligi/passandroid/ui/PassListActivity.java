package org.ligi.passandroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.service.MarketService;
import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.TrackerInterface;
import org.ligi.passandroid.events.NavigationOpenedEvent;
import org.ligi.passandroid.events.SortOrderChangeEvent;
import org.ligi.passandroid.events.TypeFocusEvent;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.PastLocationsStore;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

import static org.ligi.passandroid.ui.UnzipPassController.InputStreamUnzipControllerSpec;
import static org.ligi.passandroid.ui.UnzipPassController.SilentFail;
import static org.ligi.passandroid.ui.UnzipPassController.processInputStream;

public class PassListActivity extends ActionBarActivity {

    private PassAdapter passAdapter;

    private ScanForPassesTask scanTask = null;
    private ActionBarDrawerToggle drawerToggle;

    @InjectView(R.id.content_list)
    RecyclerView listView;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;

    @InjectView(R.id.emptyView)
    TextView emptyView;

    @InjectView(R.id.list_swiperefresh_layout)
    SwipeRefreshLayout listSwipeRefreshLayout;

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

    final PassListUIState uiState;

    public PassListActivity() {
        super();
        uiState = new PassListUIState(this) {
            @Override
            public void set(int state) {
                Log.i("setting ui state " + state);
                super.set(state);
                updateUIRegardingToUIState();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.pass_list);
        ButterKnife.inject(this);
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        */
        /*
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setHomeButtonEnabled(true);*/

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.scrollToPosition(0);
        listView.setLayoutManager(llm);

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

        prepareRefreshLayout(listSwipeRefreshLayout);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    private void prepareRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(R.color.icon_blue, R.color.icon_green, R.color.icon_lila, R.color.icon_orange);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (uiState.get() == PassListUIState.UISTATE_LIST) {
                    Tracker.get().trackEvent(TrackerInterface.EVENT_CATEGORY_UI_ACTION, "refresh", "from_swipe", null);
                    scanForPasses();
                }
            }
        });
    }

    private void scanForPasses() {
        new ScanForPassesTask().execute();
    }

    private void scrollToType(String type) {
        for (int i = 0; i < passAdapter.getItemCount(); i++) {
            if (App.getPassStore().getPassbookAt(i).getTypeNotNull().equals(type)) {
                listView.scrollToPosition(i);
                return; // we are done
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (uiState.get() != PassListUIState.UISTATE_LIST) {
            Toast.makeText(this, R.string.please_wait, Toast.LENGTH_LONG).show();
            return true;
        }


        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Tracker.get().trackEvent(TrackerInterface.EVENT_CATEGORY_UI_ACTION, "refresh", "from_optionsitem", null);
                scanForPasses();
                return true;
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

            passAdapter = new PassAdapter(PassListActivity.this);
            passAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    emptyView.setVisibility((passAdapter.getItemCount() == 0)?View.VISIBLE:View.GONE);
                    listView.setVisibility((passAdapter.getItemCount() != 0)?View.VISIBLE:View.GONE);
                    super.onChanged();
                }
            });
            listView.setAdapter(passAdapter);

            if (App.getPassStore().isEmpty()) {
                uiState.set(PassListUIState.UISTATE_SCAN);
                scanTask = new ScanForPassesTask();
                scanTask.execute();
            } else {
                uiState.set(PassListUIState.UISTATE_LIST);
            }

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
        if (uiState.get() == PassListUIState.UISTATE_LIST) {
            getMenuInflater().inflate(R.menu.activity_pass_list_view, menu);
        }
        return true;
    }

    public void updateUIRegardingToUIState() {
        listSwipeRefreshLayout.setRefreshing(uiState.get() != PassListUIState.UISTATE_LIST);

        supportInvalidateOptionsMenu();

        emptyView.setText(Html.fromHtml(uiState.getHtmlResForEmptyView()));

        emptyView.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
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

        if (scanTask != null) {
            scanTask.cancel(true);
        }
        uiState.set(PassListUIState.UISTATE_LIST);
        super.onPause();
    }

    class ImportAndRefreshListAsync extends ImportAsyncTask {

        public ImportAndRefreshListAsync(final Activity passImportActivity, final String path) {
            super(passImportActivity, Uri.parse("file://" + path));
            new PastLocationsStore(passImportActivity).putLocation(path);
        }

        @Override
        protected InputStreamWithSource doInBackground(Void... params) {
            final InputStreamWithSource ins = super.doInBackground(params);
            final InputStreamUnzipControllerSpec spec = new InputStreamUnzipControllerSpec(ins, passImportActivity,
                    new UnzipPassController.SuccessCallback() {
                        @Override
                        public void call(String pathToPassbook) {
                            refreshPasses();
                        }
                    }, new SilentFail());
            processInputStream(spec);
            return ins;
        }

        @Override
        protected void onPostExecute(InputStreamWithSource result) {
            refreshPasses();
            super.onPostExecute(result);
        }
    }

    class ScanForPassesTask extends AsyncTask<Void, Optional<String>, Void> {

        @Override
        @SafeVarargs
        protected final void onProgressUpdate(Optional<String>... values) {
            super.onProgressUpdate(values);
            setActionbarToProgress(values);
        }

        private void setActionbarToProgress(Optional<String>[] values) {
            if (getSupportActionBar() == null) {
                // not here - no work
                return;
            }

            if (values[0].isPresent()) {
                getSupportActionBar().setSubtitle(String.format(getString(R.string.searching_in), values[0].get()));
            } else {
                getSupportActionBar().setSubtitle(null);
            }

        }

        /**
         * recursive traversion from path on to find files named .pkpass
         *
         * @param path
         */
        private void search_in(final File path, final boolean recursive) {

            publishProgress(Optional.of(path.toString()));

            final File[] files = path.listFiles();

            if (files == null || files.length == 0) {
                // no files here
                return;
            }


            for (File file : files) {
                if (recursive && file.isDirectory()) {
                    search_in(file, true);
                } else if (file.getName().endsWith(".pkpass")) {
                    Log.i("found" + file.getAbsolutePath());

                    new ImportAndRefreshListAsync(PassListActivity.this, file.getAbsolutePath()).execute();
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            uiState.set(PassListUIState.UISTATE_SCAN);

            Tracker.get().trackEvent("ui_event", "scan", "started", null);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            uiState.set(PassListUIState.UISTATE_LIST);

            // TODO bring back Tracker.get().trackTiming("timing", System.currentTimeMillis() - start_time, "scan", "scan_time");
        }

        @Override
        protected void onCancelled() {
            Tracker.get().trackEvent("ui_event", "scan", "cancelled", null);
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (String path : new PastLocationsStore(PassListActivity.this).getLocations()) {
                search_in(new File(path), false);
            }

            // note to future_me: yea one thinks we only need to search root here, but root was /system for me and so
            // did not contain "/SDCARD" #dontoptimize
            // on my phone:

            // | /mnt/sdcard/Download << this looks kind of stupid as we do /mnt/sdcard later and hence will go here twice
            // but this helps finding passes in Downloads ( where they are very often ) fast - some users with lots of files on the SDCard gave
            // up the refreshing of passes as it took so long to traverse all files on the SDCard
            // one could think about not going there anymore but a short look at this showed that it seems cost more time to check than what it gains
            // in download there are mostly single files in a flat dir - no huge tree behind this imho
            search_in(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), true);

            // | /system
            search_in(Environment.getRootDirectory(), true);

            // | /mnt/sdcard
            search_in(Environment.getExternalStorageDirectory(), true);

            // | /cache
            search_in(Environment.getDownloadCacheDirectory(), true);

            // | /data
            search_in(Environment.getDataDirectory(), true);

            publishProgress(Optional.<String>absent());
            return null;
        }
    }

}
