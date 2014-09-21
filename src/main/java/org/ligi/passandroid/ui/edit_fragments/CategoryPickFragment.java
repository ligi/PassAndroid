package org.ligi.passandroid.ui.edit_fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.ui.views.CategoryIndicatorView;

public class CategoryPickFragment extends ListFragment {

    private final PassImpl pass;

    public CategoryPickFragment() {
        pass = (PassImpl) App.getPassStore().getCurrentPass().get();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        pass.setType(CategoryHelper.ALL_CATEGORIES[position]);
        App.getBus().post(new PassRefreshEvent(pass));
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListAdapter adapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return CategoryHelper.ALL_CATEGORIES.length;
            }

            @Override
            public String getItem(int position) {
                return CategoryHelper.ALL_CATEGORIES[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final LayoutInflater inflater = LayoutInflater.from(CategoryPickFragment.this.getActivity());
                final View inflate = inflater.inflate(R.layout.item_nav_pass_category, parent, false);

                final CategoryIndicatorView categoryIndicatorView = (CategoryIndicatorView) inflate.findViewById(R.id.categoryView);
                final String category = getItem(position);
                categoryIndicatorView.setImageByCategory(category);
                categoryIndicatorView.setTextBackgroundColor(CategoryHelper.getCategoryDefaultBG(category));
                categoryIndicatorView.setTextColor(CategoryHelper.getCategoryDefaultFG(category));
                categoryIndicatorView.setExtraText(CategoryHelper.getCategoryShortStr(category));
                final TextView tv = (TextView) inflate.findViewById(R.id.navCategoryLabel);
                tv.setText(CategoryHelper.getHumanCategoryString(category));

                return inflate;
            }
        };

        setListAdapter(adapter);
    }

}
