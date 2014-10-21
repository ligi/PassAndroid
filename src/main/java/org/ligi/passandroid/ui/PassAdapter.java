package org.ligi.passandroid.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.PassVisualizer;

class PassAdapter extends BaseAdapter {

    private final PassListActivity passListActivity;

    public PassAdapter(PassListActivity passListActivity) {
        this.passListActivity = passListActivity;
    }

    @Override
    public int getCount() {
        return App.getPassStore().passCount();
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

        View res = passListActivity.getLayoutInflater().inflate(R.layout.pass_list_item, parent, false);

        PassVisualizer.visualize(passListActivity, App.getPassStore().getPassbookAt(position), res);

        return res;
    }

}
