package org.ligi.passandroid.ui.edit;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.model.pass.PassType;
import org.ligi.passandroid.ui.views.BaseCategoryIndicatorView;

public class CategoryPickDialog {

    private final static PassType[] passTypes = {PassType.BOARDING, PassType.EVENT, PassType.GENERIC, PassType.LOYALTY, PassType.VOUCHER, PassType.COUPON};

    public static void show(final Context context, final Pass pass, final EventBus bus) {

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
                final LayoutInflater inflater = LayoutInflater.from(context);
                final View inflate = inflater.inflate(R.layout.item_nav_pass_category, parent, false);

                final BaseCategoryIndicatorView categoryIndicatorView = (BaseCategoryIndicatorView) inflate.findViewById(R.id.categoryView);

                final PassType type = getItem(position);
                categoryIndicatorView.setImageByCategory(type);
                categoryIndicatorView.setAccentColor(CategoryHelper.INSTANCE.getCategoryDefaultBG(type));
                final TextView tv = (TextView) inflate.findViewById(R.id.navCategoryLabel);
                tv.setText(CategoryHelper.INSTANCE.getHumanCategoryString(type));

                return inflate;
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int position) {
                pass.setType(passTypes[position]);
                bus.post(new PassRefreshEvent(pass));
            }
        });
        builder.setTitle(R.string.select_category_dialog_title);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

}
