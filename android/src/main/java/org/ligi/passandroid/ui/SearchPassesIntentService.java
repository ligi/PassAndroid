package org.ligi.passandroid.ui;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import com.squareup.otto.Bus;

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.SortOrderChangeEvent;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.PastLocationsStore;
import org.ligi.passandroid.reader.AppleStylePassReader;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.ligi.passandroid.ui.UnzipPassController.InputStreamUnzipControllerSpec;

public class SearchPassesIntentService extends IntentService {

    public static final int PROGRESS_NOTIFICATION_ID = 1;
    public static final int FOUND_NOTIFICATION_ID = 2;

    private static final int REQUEST_CODE = 1;

    private NotificationManager notifyManager;
    private NotificationCompat.Builder progressNotificationBuilder;
    private NotificationCompat.Builder findNotificationBuilder;

    private List<String> foundList;


    private long lastProgressUpdate = 0;

    @Inject
    PassStore passStore;

    @Inject
    Bus bus;

    public SearchPassesIntentService() {
        super("SearchPassesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        App.component().inject(this);

        foundList = new ArrayList<>();

        notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, new Intent(getBaseContext(), PassListActivity.class), 0);
        progressNotificationBuilder = new NotificationCompat.Builder(this).setContentTitle(getString(R.string.scanning_for_passes))
                                                                          .setSmallIcon(R.drawable.ic_action_refresh)
                                                                          .setOngoing(true)
                                                                          .setContentIntent(pendingIntent)
                                                                          .setProgress(1, 1, true);

        findNotificationBuilder = new NotificationCompat.Builder(this).setAutoCancel(true).setSmallIcon(R.drawable.ic_launcher);

        for (String path : new PastLocationsStore(getApplicationContext()).getLocations()) {
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
        notifyManager.cancel(PROGRESS_NOTIFICATION_ID);
    }

    /**
     * recursive voyage starting at path to find files named .pkpass
     */
    private void search_in(final File path, final boolean recursive) {

        if (System.currentTimeMillis() - lastProgressUpdate > 1000) {
            lastProgressUpdate = System.currentTimeMillis();
            progressNotificationBuilder.setContentText(path.toString());
            notifyManager.notify(PROGRESS_NOTIFICATION_ID, progressNotificationBuilder.build());
        }

        final File[] files = path.listFiles();

        if (files == null || files.length == 0) {
            // no files here
            return;
        }


        for (final File file : files) {
            if (recursive && file.isDirectory()) {
                search_in(file, true);
            } else if (file.getName().endsWith(".pkpass")) {
                Log.i("found" + file.getAbsolutePath());

                final InputStreamWithSource ins = InputStreamProvider.fromURI(getBaseContext(), Uri.parse("file://" + file.getAbsolutePath()));
                final InputStreamUnzipControllerSpec spec = new InputStreamUnzipControllerSpec(ins,
                                                                                               getBaseContext(),
                                                                                               getOnSuccessCallback(file),
                                                                                               new UnzipPassController.FailCallback() {
                                                                                                   @Override
                                                                                                   public void fail(String reason) {
                                                                                                       Log.i("fail", reason);
                                                                                                   }
                                                                                               });
                UnzipPassController.processInputStream(spec);
            }
        }
    }

    private UnzipPassController.SuccessCallback getOnSuccessCallback(final File file) {
        return new UnzipPassController.SuccessCallback() {
            @Override
            public void call(String uuid) {
                foundList.add(uuid);
                final String language = getBaseContext().getResources().getConfiguration().locale.getLanguage();
                final FiledPass pass = AppleStylePassReader.read(passStore.getPathForID(uuid), language);
                bus.post(new SortOrderChangeEvent());
                final Bitmap iconBitmap = pass.getBitmap(Pass.BITMAP_ICON);
                if (iconBitmap != null) {
                    final Bitmap bitmap = scale2maxSize(iconBitmap, getResources().getDimensionPixelSize(R.dimen.finger));
                    findNotificationBuilder.setLargeIcon(bitmap);
                }
                findNotificationBuilder.setContentTitle("found: " + pass.getDescription());
                if (foundList.size() > 1) {
                    findNotificationBuilder.setContentText("And " + (foundList.size() - 1) + " more ");
                } else {
                    findNotificationBuilder.setContentText(file.getAbsolutePath());
                }
                final Intent intent = new Intent(getBaseContext(), PassViewActivity.class);
                intent.putExtra(PassViewActivityBase.EXTRA_KEY_UUID, uuid);
                findNotificationBuilder.setContentIntent(PendingIntent.getActivity(getBaseContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                notifyManager.notify(FOUND_NOTIFICATION_ID, findNotificationBuilder.build());
            }
        };
    }

    private Bitmap scale2maxSize(Bitmap bitmap, int dimensionPixelSize) {
        final float scale = (float) dimensionPixelSize / ((bitmap.getWidth() > bitmap.getHeight()) ? bitmap.getWidth() : bitmap.getHeight());
        return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), false);
    }

}
