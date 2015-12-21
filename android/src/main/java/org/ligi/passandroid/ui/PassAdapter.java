package org.ligi.passandroid.ui;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.PassStoreProjection;

import java.util.List;

import javax.inject.Inject;

public class PassAdapter extends RecyclerView.Adapter<PassViewHolder> {

    @Inject
    PassStore passStore;

    protected final AppCompatActivity passListActivity;
    private final PassStoreProjection passStoreProjection;

    ActionMode actionMode;

    public PassAdapter(AppCompatActivity passListActivity, PassStoreProjection passStoreProjection) {
        this.passStoreProjection = passStoreProjection;
        App.component().inject(this);
        this.passListActivity = passListActivity;
    }

    @Override
    public PassViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        final View res = inflater.inflate(R.layout.pass_list_item, viewGroup, false);
        return new PassViewHolder(res);
    }

    @Override
    public void onBindViewHolder(final PassViewHolder viewHolder,  int longClickedCardPosition) {
        final Pass pass = passStoreProjection.getPassList().get(longClickedCardPosition);

        viewHolder.apply(pass, passListActivity);

        final CardView root = viewHolder.root;

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.setCardElevation(v.getContext().getResources().getDimension(R.dimen.card_longclick_elevation));
                passStore.setCurrentPass(pass);
                AXT.at(passListActivity).startCommonIntent().activityFromClass(PassViewActivity.class);
            }
        });

        root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final Pass pass = getList().get(viewHolder.getAdapterPosition());

                if (actionMode != null) {
                    final boolean clickedOnDifferentItem = actionMode.getTag() == null || !actionMode.getTag().equals(pass);
                    if (clickedOnDifferentItem) {
                        actionMode.finish();
                    } else {
                        return true;
                    }
                }

                actionMode = passListActivity.startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                        passListActivity.getMenuInflater().inflate(R.menu.activity_pass_action_mode, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        if (new PassMenuOptions(passListActivity, getList().get(viewHolder.getAdapterPosition())).process(menuItem)) {
                            actionMode.finish();
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode _actionMode) {
                        root.setCardElevation(root.getContext().getResources().getDimension(R.dimen.cardview_default_elevation));
                        actionMode = null;
                    }
                });

                actionMode.setTag(getList().get(viewHolder.getAdapterPosition()));

                root.setCardElevation(v.getContext().getResources().getDimension(R.dimen.card_longclick_elevation));

                return true;

            }

        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private List<FiledPass> getList() {
        return passStoreProjection.getPassList();
    }

    @Override
    public int getItemCount() {
        return getList().size();
    }

}
