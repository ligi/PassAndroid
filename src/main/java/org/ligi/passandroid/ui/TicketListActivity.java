package org.ligi.passandroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.service.MarketService;
import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.TrackerInterface;
import org.ligi.passandroid.events.NavigationOpenedEvent;
import org.ligi.passandroid.events.SortOrderChangeEvent;
import org.ligi.passandroid.events.TypeFocusEvent;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.io.File;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

import static org.ligi.passandroid.ui.UnzipPassController.SilentFail;
import static org.ligi.passandroid.ui.UnzipPassController.SilentWin;

public class TicketListActivity extends ActionBarActivity {

    private LayoutInflater inflater;
    private PassAdapter passadapter;
    private boolean scanning = false;

    private ScanForPassesTask scan_task = null;
    private ActionBarDrawerToggle drawerToggle;

    @InjectView(R.id.content_list)
    ListView listView;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;

    @InjectView(R.id.emptyView)
    TextView emptyView;

    @InjectView(R.id.list_swiperefresh_layout)
    SwipeRefreshLayout listSwipeRefreshLayout;

    @InjectView(R.id.empty_swiperefresh_layout)
    SwipeRefreshLayout emptySwipeRefreshLayout;

    private ActionMode actionMode;

    @Subscribe
    public void sortOrderChange(SortOrderChangeEvent orderChangeEvent) {
        refreshPasses();
    }

    @Subscribe
    public void typeFocus(TypeFocusEvent typeFocusEvent) {
        scrollToType(typeFocusEvent.type);
        drawer.closeDrawers();
    }

    @OnItemClick(R.id.content_list)
    void listItemClick(int position) {
        Intent intent = new Intent(TicketListActivity.this, TicketViewActivity.class);
        intent.putExtra("path", App.getPassStore().getPassbookAt(position).getPath());
        startActivity(intent);
    }

    public void refreshPasses() {
        App.getPassStore().refreshPassesList();
        App.getPassStore().sort(App.getSettings().getSortOrder());
        passadapter.notifyDataSetChanged();
    }

    public TicketListActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.ticket_list);
        ButterKnife.inject(this);

        passadapter = new PassAdapter(this);
        listView.setAdapter(passadapter);

        inflater = getLayoutInflater();

        listView.setEmptyView(emptySwipeRefreshLayout);

        // don't want too many windows in worst case - so check for errors first
        if (TraceDroid.getStackTraceFiles().length > 0) {
            Tracker.get().trackEvent("ui_event", "send", "stacktraces", null);
            TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
        } else { // if no error - check if there is a new version of the app
            Tracker.get().trackEvent("ui_event", "processInputStream", "updatenotice", null);
            MarketService ms = new MarketService(this);
            ms.level(MarketService.MINOR).checkVersion();

            AppRate.with(this)
                    .retryPolicy(RetryPolicy.EXPONENTIAL)
                    .initialLaunchCount(5)
                    .checkAndShow();
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                App.getBus().post(new NavigationOpenedEvent());
            }
        };
        drawer.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {

                listView.setItemChecked(position, true);
                view.refreshDrawableState();

                if (actionMode != null) {
                    // no need to restart -> NTFS restarting is even dangerous as this deselects the item
                    return true;
                }

                actionMode = startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                        getMenuInflater().inflate(R.menu.activity_ticket_view, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        if (new PassMenuOptions(TicketListActivity.this, App.getPassStore().getPassbookAt(position)).process(menuItem)) {
                            actionMode.finish();
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode _actionMode) {
                        listView.setItemChecked(position, false);
                        actionMode = null;
                    }
                });

                return true;

            }
        });

        prepareRefreshLayout(listSwipeRefreshLayout);
        prepareRefreshLayout(emptySwipeRefreshLayout);

    }

    private void prepareRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorScheme(R.color.icon_blue, R.color.icon_green, R.color.icon_lila, R.color.icon_orange);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Tracker.get().trackEvent(TrackerInterface.EVENT_CATEGORY_UI_ACTION, "refresh", "from_swipe", null);
                scanForPasses();
            }
        });
    }

    private void scanForPasses() {
        App.getPassStore().deleteCache();
        new ScanForPassesTask().execute();
    }

    private void scrollToType(String type) {
        for (int i = 0; i < passadapter.getCount(); i++) {
            if (App.getPassStore().getReducedPassbookAt(i).getTypeNotNull().equals(type)) {
                listView.setSelection(i);
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

        if (App.getPassStore().isEmpty()) {
            scan_task = new ScanForPassesTask();
            scan_task.execute();
        }

        updateUIToScanningState();

        Tracker.get().trackEvent("ui_event", "resume", "passes", (long) App.getPassStore().passCount());

        App.getBus().register(this);

        refreshPasses();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (!scanning) {
            getMenuInflater().inflate(R.menu.activity_ticket_list_view, menu);
        }
        return true;
    }

    public void updateUIToScanningState() {

        listSwipeRefreshLayout.setRefreshing(scanning);
        emptySwipeRefreshLayout.setRefreshing(scanning);

        supportInvalidateOptionsMenu();

        String html;
        if (scanning) {
            html = getString(R.string.scan_empty_text);
        } else {
            html = getString(R.string.no_passes_empty_text);
        }

        emptyView.setText(Html.fromHtml(html));

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

        if (scan_task != null) {
            scan_task.cancel(true);
        }
        scanning = false;
        super.onPause();
    }

    class ImportAndRefreshListAsync extends ImportAsyncTask {

        public ImportAndRefreshListAsync(Activity ticketImportActivity, Uri intent_uri) {
            super(ticketImportActivity, intent_uri);
        }

        @Override
        protected InputStream doInBackground(Void... params) {
            InputStream ins = super.doInBackground(params);
            UnzipPassController.processInputStream(ins, ticketImportActivity, new SilentWin(), new SilentFail());
            return ins;
        }

        @Override
        protected void onPostExecute(InputStream result) {
            refreshPasses();
            super.onPostExecute(result);
        }
    }

    class ScanForPassesTask extends AsyncTask<Void, Optional<String>, Void> {

        @Override
        protected void onProgressUpdate(Optional<String>... values) {
            super.onProgressUpdate(values);
            if (Build.VERSION.SDK_INT > 10) {
                if (values[0].isPresent()) {
                    getActionBar().setSubtitle(String.format(getString(R.string.searching_in), values[0].get()));
                } else {
                    getActionBar().setSubtitle(null);
                }

            }
        }

        /**
         * recursive traversion from path on to find files named .pkpass
         *
         * @param path
         */
        private void search_in(String path) {

            publishProgress(Optional.of(path));

            if (path == null) {
                Log.w("trying to search in null path");
                return;
            }

            final File dir = new File(path);
            final File[] files = dir.listFiles();

            if (files == null || files.length == 0) {
                // no files here
                return;
            }


            for (File file : files) {
                if (file.isDirectory()) {
                    search_in(file.toString());
                } else if (file.getName().endsWith(".pkpass")) {
                    Log.i("found" + file.getAbsolutePath());
                    new ImportAndRefreshListAsync(TicketListActivity.this, Uri.parse("file://" + file.getAbsolutePath())).execute();
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            scanning = true;

            Tracker.get().trackEvent("ui_event", "scan", "started", null);

            updateUIToScanningState();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            scanning = false;


            // TODO bring back Tracker.get().trackTiming("timing", System.currentTimeMillis() - start_time, "scan", "scan_time");
            updateUIToScanningState();
        }

        @Override
        protected void onCancelled() {
            Tracker.get().trackEvent("ui_event", "scan", "cancelled", null);
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // note to future_me: yea one thinks we only need to search root here, but root was /system for me and so
            // did not contain "/SDCARD" #dontoptimize
            // on my phone:

            // | /mnt/sdcard/Download << this looks kind of stupid as we do /mnt/sdcard later and hence will go here twice
            // but this helps finding passes in Downloads ( where they are very often ) fast - some users with lots of files on the SDCard gave
            // up the refreshing of passes as it took so long to traverse all files on the SDCard
            // one could think about not going there anymore but a short look at this showed that it seems cost more time to check than what it gains
            // in download there are mostly single files in a flat dir - no huge tree behind this imho
            search_in(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());

            // | /system
            search_in(Environment.getRootDirectory().toString());

            // | /mnt/sdcard
            search_in(Environment.getExternalStorageDirectory().toString());

            // | /cache
            search_in(Environment.getDownloadCacheDirectory().toString());

            // | /data
            search_in(Environment.getDataDirectory().toString());

            publishProgress(Optional.<String>absent());
            return null;
        }
    }

}
