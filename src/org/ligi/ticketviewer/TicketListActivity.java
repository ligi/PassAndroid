package org.ligi.ticketviewer;

import java.io.File;
import java.io.FilenameFilter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ligi.ticketviewer.helper.FileHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

public class TicketListActivity extends SherlockListActivity {

	private String[] passes;
	private LayoutInflater inflater;
	private String path;
	private PassAdapter passadapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		path = TicketDefinitions.getPassesDir(this);
		File passes_dir=new File(TicketDefinitions.getPassesDir(this));
		if (!passes_dir.exists())
			passes_dir.mkdirs();
		passes =passes_dir.list(new DirFilter());
		passadapter=new PassAdapter();
		setListAdapter(passadapter);
		
		inflater = getLayoutInflater();
		super.onCreate(savedInstanceState);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				Intent intent = new Intent(TicketListActivity.this, TicketViewActivity.class);
				intent.putExtra("path", path + "/" + passes[pos] + "/");
				startActivity(intent);
			}

		});
		
		TextView empty_view=new TextView(this);
		empty_view.setText("No passes yet - go get some and come back");
		//getListView().setEmptyView(empty_view);
		
		empty_view.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

		((ViewGroup)getListView().getParent()).addView(empty_view);
		getListView().setEmptyView(empty_view);
		
	}
	
	

	@Override
	protected void onResume() {

		super.onResume();
		passes = new File(TicketDefinitions.getPassesDir(this)).list(new DirFilter());
		passadapter.notifyDataSetChanged();

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
			String mPath=path + "/" + passes[position];
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
			
			int size=(int)getResources().getDimension(R.dimen.pass_icon_size);
			ImageView icon_img=(ImageView)res.findViewById(R.id.icon);
			if (path!=null) {
				Bitmap ico=BitmapFactory.decodeFile(mPath+"/logo@2x.png");
				
				if (ico==null)
					ico=BitmapFactory.decodeFile(mPath+"/logo.png");
					
				if (ico!=null)
					icon_img.setImageBitmap(Bitmap.createScaledBitmap(ico,size,size,false));
				
			}
				
			
			return res;
		}

	}
}
