package org.ligi.passandroid.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Collection;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassStoreChangeEvent;
import org.ligi.passandroid.events.ScanFinishedEvent;
import org.ligi.passandroid.helper.MoveHelper;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.PassStoreProjection;
import org.ligi.passandroid.model.Settings;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;

public class PassListFragment extends Fragment {

    private static final String BUNDLE_KEY_TOPIC = "topic";
    private PassStoreProjection passStoreProjection;
    private PassAdapter adapter;
    private RecyclerView recyclerView;

    public static PassListFragment newInstance(final String topic) {
        PassListFragment myFragment = new PassListFragment();

        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_TOPIC, topic);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Inject
    PassStore passStore;

    @Inject
    Settings settings;

    @Inject
    EventBus bus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.pass_recycler, container, false);
        recyclerView = (RecyclerView) inflate.findViewById(R.id.pass_recyclerview);

        App.component().inject(this);

        passStoreProjection = new PassStoreProjection(passStore, getArguments().getString(BUNDLE_KEY_TOPIC), settings.getSortOrder());
        adapter = new PassAdapter((AppCompatActivity) getActivity(), passStoreProjection);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        SimpleCallback simpleItemTouchCallback = new SimpleCallback(0, LEFT | RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final Pass pass = passStoreProjection.getPassList().get(viewHolder.getAdapterPosition());

                final String nextTopic = calculateNextTopic(swipeDir, pass);

                if (nextTopic != null) {
                    MoveHelper.moveWithUndoSnackbar(passStore.getClassifier(), pass, nextTopic, getActivity());
                } else {
                    MoveToNewTopicUI.show(getActivity(), passStore, pass);
                }


            }

            @Nullable
            private String calculateNextTopic(final int swipeDir, final Pass pass) {

                final Collection<String> topics = passStore.getClassifier().getTopics();

                switch (swipeDir) {
                    case LEFT:
                        return getNextTopicLeft(pass, topics);
                    case RIGHT:
                        return getNextTopicRight(pass, topics);
                }

                return null;
            }

            @Nullable
            private String getNextTopicRight(Pass pass, Collection<String> topics) {

                boolean nextIsCandidate = false;

                for (String topic : topics) {
                    if (nextIsCandidate) {
                        return topic;
                    }
                    if (passStore.getClassifier().getTopic(pass,"").equals(topic)) {
                        nextIsCandidate = true;
                    }
                }
                return null;
            }


            @Nullable
            private String getNextTopicLeft(Pass pass, Collection<String> topics) {
                String prev = null;

                for (String topic : topics) {
                    if (passStore.getClassifier().getTopic(pass,"").equals(topic)) {
                        return prev;
                    }
                    prev = topic;
                }

                return null;
            }

        };

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        bus.register(this);
        return inflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPassStoreChangeEvent(PassStoreChangeEvent passStoreChangeEvent) {
        passStoreProjection.refresh();
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanFinishedEvent(ScanFinishedEvent scanFinishedEvent) {
        passStoreProjection.refresh();
        adapter.notifyDataSetChanged();

    }

}
