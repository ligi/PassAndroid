package org.ligi.passandroid.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.Collection;
import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ligi.axt.AXT;
import org.ligi.axt.listeners.ActivityFinishingOnClickListener;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassStoreChangeEvent;
import org.ligi.passandroid.events.ScanFinishedEvent;
import org.ligi.passandroid.events.ScanProgressEvent;
import org.ligi.passandroid.helper.PassTemplates;
import org.ligi.passandroid.model.PassClassifier;
import org.ligi.passandroid.model.PassStoreProjection;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.snackengage.SnackEngage;
import org.ligi.snackengage.snacks.DefaultRateSnack;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

public class PassListActivity extends PassAndroidActivity {

    private static final int OPEN_FILE_READ_REQUEST_CODE = 1000;

    private ActionBarDrawerToggle drawerToggle;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.emptyView)
    TextView emptyView;

    @BindView(R.id.fam)
    FloatingActionsMenu floatingActionsMenu;

    private PassTopicFragmentPagerAdapter adapter;
    private ProgressDialog pd;

    @OnClick(R.id.fab_action_create_pass)
    void onFABClick() {
        final Pass pass = PassTemplates.createAndAddEmptyPass(passStore, getResources());

        floatingActionsMenu.collapse();
        AXT.at(this).startCommonIntent().activityFromClass(PassEditActivity.class);

        final String newTitle;
        if (tabLayout.getSelectedTabPosition() < 0) {
            newTitle = getString(R.string.topic_new);
        } else {
            newTitle = adapter.getPageTitle(tabLayout.getSelectedTabPosition()).toString();
        }

        passStore.getClassifier().moveToTopic(pass, newTitle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanProgress(final ScanProgressEvent event) {
        if (pd != null && pd.isShowing()) {
            pd.setMessage(event.message);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanFinished(final ScanFinishedEvent event) {

        if (pd != null && pd.isShowing()) {
            final String message = getString(R.string.scan_finished_dialog_text, event.getFoundPasses().size());
            pd.dismiss();
            new AlertDialog.Builder(PassListActivity.this).setTitle(R.string.scan_finished_dialog_title)
                                                          .setMessage(message)
                                                          .setPositiveButton(android.R.string.ok, null)
                                                          .show();
        }

    }

    @OnClick(R.id.fab_action_scan)
    void onScanClick() {
        final Intent intent = new Intent(this, SearchPassesIntentService.class);
        startService(intent);

        pd = new ProgressDialog(this);
        pd.setTitle(R.string.scan_progressdialog_title);
        pd.setMessage(getString(R.string.scan_progressdialog_message));
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.scan_dialog_send_background_button), new ActivityFinishingOnClickListener(this));
        pd.show();

        floatingActionsMenu.collapse();
    }

    @OnClick(R.id.fab_action_demo_pass)
    void onAddDemoClick() {
        AXT.at(this).startCommonIntent().openUrl("http://espass.it/examples");
        floatingActionsMenu.collapse();
    }

    @BindView(R.id.fab_action_open_file)
    FloatingActionButton openFileFAB;
    public final static int VERSION_STARTING_TO_SUPPORT_STORAGE_FRAMEWORK = 19;

    @OnClick(R.id.fab_action_open_file)
    @TargetApi(VERSION_STARTING_TO_SUPPORT_STORAGE_FRAMEWORK)
    void onAddOpenFileClick() {
        try {
            final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*"); // tried with octet stream - no use
            startActivityForResult(intent, OPEN_FILE_READ_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(floatingActionsMenu, "Unavailable", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == OPEN_FILE_READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                final Intent targetIntent = new Intent(this, PassImportActivity.class);
                targetIntent.setData(resultData.getData());
                startActivity(targetIntent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.component().inject(this);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.pass_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        AXT.at(openFileFAB).setVisibility(Build.VERSION.SDK_INT >= VERSION_STARTING_TO_SUPPORT_STORAGE_FRAMEWORK);

        // don't want too many windows in worst case - so check for errors first
        if (TraceDroid.getStackTraceFiles().length > 0) {
            tracker.trackEvent("ui_event", "send", "stacktraces", null);
            if (settings.doTraceDroidEmailSend()) {
                TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
            }
        } else { // if no error - check if there is a new version of the app
            tracker.trackEvent("ui_event", "processFile", "updatenotice", null);

            SnackEngage.from(floatingActionsMenu).withSnack(new DefaultRateSnack()).build().engageWhenAppropriate();
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close);

        drawer.addDrawerListener(drawerToggle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        adapter = new PassTopicFragmentPagerAdapter(passStore.getClassifier(), getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        if (adapter.getCount() > 0) {
            viewPager.setCurrentItem(state.getLastSelectedTab());
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                state.setLastSelectedTab(position);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
        passStore.syncPassStoreWithClassifier(getString(R.string.topic_new));

        onPassStoreChangeEvent(null);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_help:
                AXT.at(this).startCommonIntent().activityFromClass(HelpActivity.class);
                return true;

            case R.id.menu_emptytrash:
                new AlertDialog.Builder(this).setMessage(getString(R.string.empty_trash_dialog_message))
                                             .setIcon(R.drawable.ic_alert_warning)
                                             .setTitle(getString(R.string.empty_trash_dialog_title))
                                             .setPositiveButton(R.string.emtytrash_label, new DialogInterface.OnClickListener() {
                                                 @Override
                                                 public void onClick(final DialogInterface dialog, final int which) {
                                                     final PassStoreProjection passStoreProjection = new PassStoreProjection(passStore,
                                                                                                                             getString(R.string.topic_trash),
                                                                                                                             null);

                                                     for (final Pass pass : passStoreProjection.getPassList()) {
                                                         passStore.deletePassWithId(pass.getId());
                                                     }

                                                 }
                                             })
                                             .setNegativeButton(android.R.string.cancel, null)
                                             .show();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        bus.register(this);

        adapter.notifyDataSetChanged();
        onPassStoreChangeEvent(null);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_emptytrash)
            .setVisible((adapter.getCount() > 0) && adapter.getPageTitle(viewPager.getCurrentItem()).equals(getString(R.string.topic_trash)));
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pass_list_view, menu);
        return super.onCreateOptionsMenu(menu);
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
        bus.unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPassStoreChangeEvent(PassStoreChangeEvent passStoreChangeEvent) {
        adapter.notifyDataSetChanged();

        setupWithViewPagerIfNeeded();

        supportInvalidateOptionsMenu();

        final boolean empty = passStore.getClassifier().getTopicByIdMap().isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        tabLayout.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void setupWithViewPagerIfNeeded() {
        if (!areTabLayoutAndViewPagerInSync()) {
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private boolean areTabLayoutAndViewPagerInSync() {
        if (adapter.getCount() == tabLayout.getTabCount()) {
            for (int i = 0; i < adapter.getCount(); i++) {
                final TabLayout.Tab tabAt = tabLayout.getTabAt(i);
                if (tabAt == null || !adapter.getPageTitle(i).equals(tabAt.getText())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static class PassTopicFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private String[] topic_array;
        private final PassClassifier passClassifier;

        public PassTopicFragmentPagerAdapter(PassClassifier passClassifier, FragmentManager fragmentManager) {
            super(fragmentManager);
            this.passClassifier = passClassifier;
            notifyDataSetChanged();

        }

        @Override
        public void notifyDataSetChanged() {
            final Collection<String> topics = passClassifier.getTopics();
            topic_array = topics.toArray(new String[topics.size()]);
            super.notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return PassListFragment.newInstance(topic_array[position]);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE; // TODO - return POSITION_UNCHANGED in some cases
        }

        @Override
        public int getCount() {
            return topic_array.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return topic_array[position];
        }
    }

}