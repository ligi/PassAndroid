package org.ligi.ticketviewer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.androidquery.service.MarketService;
import com.google.analytics.tracking.android.EasyTracker;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ligi.ticketviewer.helper.FileHelper;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;

public class TicketListActivity extends SherlockListActivity {

    private String[] passes;
    private LayoutInflater inflater;
    private String path;
    private PassAdapter passadapter;
    private boolean scanning = false;
    private TextView empty_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);


        refresh_passes_list();

        passadapter = new PassAdapter();
        setListAdapter(passadapter);

        inflater = getLayoutInflater();
        getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Intent intent = new Intent(TicketListActivity.this, TicketViewActivity.class);
                intent.putExtra("path", path + "/" + passes[pos] + "/");
                startActivity(intent);
            }

        });

        empty_view = new TextView(this);
        empty_view.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        ((ViewGroup) getListView().getParent()).addView(empty_view);
        getListView().setEmptyView(empty_view);


        // don't want too many windows in worst case
        switch ((int) (System.currentTimeMillis() % 2)) {
            case 0:
                TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
                break;
            case 1:

                MarketService ms = new MarketService(this);
                ms.level(MarketService.MINOR).checkVersion();
                break;
        }

        if (passes == null || passes.length == 0)
            new ScanForPassesTask().execute();
    }

    private void refresh_passes_list() {
        path = TicketDefinitions.getPassesDir(this);
        File passes_dir = new File(TicketDefinitions.getPassesDir(this));
        if (!passes_dir.exists())
            passes_dir.mkdirs();
        passes = passes_dir.list(new DirFilter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                new ScanForPassesTask().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onResume() {

        super.onResume();
        passes = new File(TicketDefinitions.getPassesDir(this)).list(new DirFilter());
        passadapter.notifyDataSetChanged();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (!scanning)
            getSupportMenuInflater().inflate(R.menu.activity_ticket_list_view, menu);
        return true;
    }

    public void updateUIToScanningState() {
        setSupportProgressBarIndeterminateVisibility(scanning);
        supportInvalidateOptionsMenu();


        if (scanning)
            empty_view.setText("No passes yet - searching for passes");
        else
            empty_view.setText("No passes yet - try to get some - then click on them or hit refresh");


    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    class ImportAndShowAsyncTask extends ImportAsyncTask {

        public ImportAndShowAsyncTask(Activity ticketImportActivity, Uri intent_uri) {
            super(ticketImportActivity, intent_uri);
        }

        @Override
        protected void onPostExecute(InputStream result) {
            if (result != null) {

                UnzipPasscodeDialog.show(result, ticketImportActivity, new UnzipPasscodeDialog.FinishCallback() {

                    @Override
                    public Void call(String path) {
                        Log.i("refreshing");
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                refresh_passes_list();
                                passadapter.notifyDataSetChanged();

                            }
                        });
                        return null;
                    }
                });
            }
            super.onPostExecute(result);
        }
    }

    class ScanForPassesTask extends AsyncTask<Void, Void, Void> {


        /**
         * recursive traversion from path on to find files named .pkpass
         *
         * @param path
         */
        private void search_in(String path) {

            if (path == null) {
                Log.w("trying to search in null path");
                return;
            }

            File dir = new File(path);
            File[] files = dir.listFiles();

            if (files != null) for (File file : files) {
                if (file.isDirectory())
                    search_in(file.toString());
                else if (file.getName().endsWith(".pkpass")) {
                    Log.i("found" + file.getAbsolutePath());
                    new ImportAndShowAsyncTask(TicketListActivity.this, Uri.parse("file://" + file.getAbsolutePath())).execute();
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            scanning = true;
            updateUIToScanningState();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            scanning = false;

            updateUIToScanningState();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // note to future_me: yea one thinks we only need to search root here, but root was /system for me and so
            // did not contain "/SDCARD" #dontoptimize
            // on my phone:
            // search in /system
            // (26110): search in /mnt/sdcard
            // (26110): search in /cache
            // (26110): search in /data
            search_in(Environment.getRootDirectory().toString());
            search_in(Environment.getExternalStorageDirectory().toString());
            search_in(Environment.getDownloadCacheDirectory().toString());
            search_in(Environment.getDataDirectory().toString());
            return null;
        }
    }

    class SearchForFilesDialog extends Dialog {

        public SearchForFilesDialog(Context context) {
            super(context);
        }
    }

    class DirFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String filename) {
            return dir.isDirectory();
        }

    }

    class PassAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return passes.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String mPath = path + "/" + passes[position];
            PassbookParser passbookParser = new PassbookParser(mPath);

            View res = inflater.inflate(R.layout.pass_list_item, null);
            TextView tv = (TextView) res.findViewById(R.id.label);
            TextView more_tv = (TextView) res.findViewById(R.id.descr);
            try {
                JSONObject pass_json = new JSONObject(FileHelper.file2String(new File(mPath + "/pass.json")));
                tv.setText(pass_json.getString("description"));
                String more_str = "";
                if (pass_json.has("eventTicket")) {
                    JSONObject eventTicket = pass_json.getJSONObject("eventTicket");

                    if (eventTicket.has("primaryFields")) {
                        JSONArray pri_arr = eventTicket.getJSONArray("primaryFields");
                        for (int i = 0; i < pri_arr.length(); i++) {
                            JSONObject sec_obj = pri_arr.getJSONObject(i);
                            more_str += sec_obj.getString("label") + ":" + sec_obj.getString("value") + "\n";
                        }
                    }
                    if (eventTicket.has("secondaryFields")) {
                        JSONArray sec_arr = eventTicket.getJSONArray("secondaryFields");
                        for (int i = 0; i < sec_arr.length(); i++) {
                            JSONObject sec_obj = sec_arr.getJSONObject(i);
                            more_str += sec_obj.getString("label") + ":" + sec_obj.getString("value") + "\n";
                        }
                    }
                }

                more_tv.setText(more_str);

            } catch (Exception e) {

            }

            int size = (int) getResources().getDimension(R.dimen.pass_icon_size);
            ImageView icon_img = (ImageView) res.findViewById(R.id.icon);
            if (path != null) {
                Bitmap ico = passbookParser.getIconBitmap();

                if (ico != null)
                    icon_img.setImageBitmap(Bitmap.createScaledBitmap(ico, size, size, false));

            }

            icon_img.setBackgroundColor(passbookParser.getBgcolor());

            return res;
        }

    }
}
