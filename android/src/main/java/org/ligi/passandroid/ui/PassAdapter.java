package org.ligi.passandroid.ui;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import javax.inject.Inject;
import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.PassStoreProjection;
import org.ligi.passandroid.model.Settings;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.ui.pass_view_holder.CondensedPassViewHolder;
import org.ligi.passandroid.ui.pass_view_holder.PassViewHolder;
import org.ligi.passandroid.ui.pass_view_holder.VerbosePassViewHolder;

public class PassAdapter extends RecyclerView.Adapter<PassViewHolder> {

    @Inject
    PassStore passStore;

    @Inject
    Settings settings;

    protected final AppCompatActivity passListActivity;
    private final PassStoreProjection passStoreProjection;

    public PassAdapter(AppCompatActivity passListActivity, PassStoreProjection passStoreProjection) {
        this.passStoreProjection = passStoreProjection;
        App.component().inject(this);
        this.passListActivity = passListActivity;
    }

    @Override
    public PassViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        final CardView res = (CardView) inflater.inflate(R.layout.pass_list_item, viewGroup, false);
        if (settings.isCondensedModeEnabled()) {
            return new CondensedPassViewHolder(res);
        } else {
            return new VerbosePassViewHolder(res);
        }

    }

    @Override
    public void onBindViewHolder(final PassViewHolder viewHolder, int position) {
        final Pass pass = passStoreProjection.getPassList().get(position);

        viewHolder.apply(pass, passStore, passListActivity);

        final CardView root = viewHolder.getView();

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passStore.setCurrentPass(pass);
                AXT.at(passListActivity).startCommonIntent().activityFromClass(PassViewActivity.class);
            }
        });

        root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(root, R.string.please_use_the_swipe_feature, Snackbar.LENGTH_LONG).show();
                return true;
            }

        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private List<Pass> getList() {
        return passStoreProjection.getPassList();
    }

    @Override
    public int getItemCount() {
        return getList().size();
    }

}
