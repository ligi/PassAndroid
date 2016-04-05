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
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.model.pass.PassType;
import org.ligi.passandroid.ui.views.CategoryIndicatorView;

public class CategoryPickFragment extends ListFragment {

    @Inject
    PassStore passStore;

    @Inject
    EventBus bus;

    private final PassImpl pass;
    private final PassType[] passTypes= {PassType.BOARDING,PassType.EVENT,PassType.GENERIC,PassType.LOYALTY,PassType.VOUCHER,PassType.COUPON};

    public CategoryPickFragment() {
        App.component().inject(this);
        pass = (PassImpl) passStore.getCurrentPass();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        pass.setType(passTypes[position]);
        bus.post(new PassRefreshEvent(pass));
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListAdapter adapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return passTypes.length;
            }

            @Override
            public PassType getItem(int position) {
                return passTypes[position];
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

                final PassType type = getItem(position);
                categoryIndicatorView.setImageByCategory(type);
                categoryIndicatorView.setAccentColor(CategoryHelper.getCategoryDefaultBG(type));
                final TextView tv = (TextView) inflate.findViewById(R.id.navCategoryLabel);
                tv.setText(CategoryHelper.getHumanCategoryString(type));

                return inflate;
            }
        };

        setListAdapter(adapter);
    }

}
