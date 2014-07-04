package org.ligi.passandroid.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.base.Optional;

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.Tracker;
import org.ligi.passandroid.model.Pass;

public class TicketViewActivityBase extends ActionBarActivity {

    protected Bitmap icon_bitmap;
    protected String path;
    public Pass pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Optional<Pass> optionalPass = App.getPassStore().getCurrentPass();

        if (!optionalPass.isPresent()) {
            Tracker.get().trackException("pass not present in TicketViewActivityBase", false);
            finish();
            return;
        }

        pass = optionalPass.get();

        loadIcon();
    }

    private void loadIcon() {
        Display display = getWindowManager().getDefaultDisplay();
        int smallestSide = Math.min(display.getHeight(), display.getWidth());
        int size = (int) (2.0f * smallestSide / 3.0f);

        icon_bitmap = pass.getIconBitmap();

        if (icon_bitmap != null) {
            icon_bitmap = Bitmap.createScaledBitmap(icon_bitmap, size, size, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ticket_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (new PassMenuOptions(this, pass).process(item)) {
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Tracker.get().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Tracker.get().activityStop(this);
    }

}
