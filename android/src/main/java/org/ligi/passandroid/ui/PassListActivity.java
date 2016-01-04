package org.ligi.passandroid.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.NavigationOpenedEvent;
import org.ligi.passandroid.events.SortOrderChangeEvent;
import org.ligi.passandroid.events.TypeFocusEvent;
import org.ligi.passandroid.helper.PassUtil;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.snackengage.SnackEngage;
import org.ligi.snackengage.snacks.DefaultRateSnack;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassListActivity extends PassAndroidActivity {

    private static final int OPEN_FILE_READ_REQUEST_CODE = 1000;
    private PassAdapter passAdapter;

    private ActionBarDrawerToggle drawerToggle;

    @Bind(R.id.content_list)
    RecyclerView recyclerView;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.emptyView)
    TextView emptyView;

    @Bind(R.id.fam)
    FloatingActionsMenu floatingActionsMenu;

    @OnClick(R.id.fab_action_create_pass)
    void onFABClick() {
        final FiledPass pass = PassUtil.createEmptyPass();

        passStore.setCurrentPass(pass);
        pass.save(passStore);
        AXT.at(this).startCommonIntent().activityFromClass(PassEditActivity.class);
        floatingActionsMenu.collapse();
    }

    @OnClick(R.id.fab_action_scan)
    void onScanClick() {
        final Intent intent = new Intent(this, SearchPassesIntentService.class);
        startService(intent);
        floatingActionsMenu.collapse();
    }


    @OnClick(R.id.fab_action_demo_pass)
    void onAddDemoClick() {
        AXT.at(this).startCommonIntent().openUrl("http://ligi.de/passandroid_samples/index.html");
        floatingActionsMenu.collapse();
    }


    @Bind(R.id.fab_action_open_file)
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
            Snackbar.make(floatingActionsMenu,"Unavailable",Snackbar.LENGTH_LONG).show();
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
        passStore.preCachePassesList();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                passStore.refreshPassesList();
                passStore.sort(settings.getSortOrder());

                AXT.at(emptyView).setVisibility(passStore.passCount() == 0);

                passAdapter.notifyDataSetChanged();

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.component().inject(this);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.pass_list);

        getSupportFragmentManager().beginTransaction().add(R.id.left_drawer, new NavigationFragment()).commitAllowingStateLoss();

        ButterKnife.bind(this);

        AXT.at(openFileFAB).setVisibility(Build.VERSION.SDK_INT >= VERSION_STARTING_TO_SUPPORT_STORAGE_FRAMEWORK);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        // don't want too many windows in worst case - so check for errors first
        if (TraceDroid.getStackTraceFiles().length > 0) {
            tracker.trackEvent("ui_event", "send", "stacktraces", null);
            if (settings.doTraceDroidEmailSend()) {
                TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
            }
        } else { // if no error - check if there is a new version of the app
            tracker.trackEvent("ui_event", "processFile", "updatenotice", null);

            SnackEngage.from(this).withSnack(new DefaultRateSnack()).build().engageWhenAppropriate();
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                bus.post(new NavigationOpenedEvent());
            }
        };

        drawer.setDrawerListener(drawerToggle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        passAdapter = new PassAdapter(this);
        recyclerView.setAdapter(passAdapter);
    }

    private void scrollToType(String type) {
        for (int i = 0; i < passAdapter.getItemCount(); i++) {
            if (passStore.getPassbookAt(i).getTypeNotNull().equals(type)) {
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

        bus.register(this);
        refreshPasses();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_pass_list_view, menu);
        return true;
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

}