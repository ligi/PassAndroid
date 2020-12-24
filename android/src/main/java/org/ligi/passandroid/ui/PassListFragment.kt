package org.ligi.passandroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.pass_recycler.view.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.ligi.passandroid.R
import org.ligi.passandroid.functions.moveWithUndoSnackbar
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.PassStoreProjection
import org.ligi.passandroid.model.Settings

class PassListFragment : Fragment() {

    private lateinit var passStoreProjection: PassStoreProjection
    private lateinit var adapter: PassAdapter

    val passStore: PassStore by inject()
    val settings: Settings by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.pass_recycler, container, false)

        passStoreProjection = PassStoreProjection(passStore, arguments?.getString(BUNDLE_KEY_TOPIC)!!, settings.getSortOrder())
        adapter = PassAdapter(activity as AppCompatActivity, passStoreProjection)

        inflate.pass_recyclerview.adapter = adapter

        inflate.pass_recyclerview.layoutManager = LinearLayoutManager(activity)

        val simpleItemTouchCallback = object : SimpleCallback(0, LEFT or RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder)
                    = false

            override fun onSwiped(viewHolder: ViewHolder, swipeDir: Int) {
                this@PassListFragment.onSwiped(viewHolder.adapterPosition, swipeDir)
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(inflate.pass_recyclerview)

        lifecycleScope.launch {
            for (update in passStore.updateChannel.openSubscription()) {
                passStoreProjection.refresh()
                adapter.notifyDataSetChanged()
            }
        }
        return inflate
    }

    @VisibleForTesting
    fun onSwiped(pos: Int, swipeDir: Int) {
        val pass = passStoreProjection.passList[pos]
        val nextTopic = passStore.classifier.getTopicWithOffset(pass, if (swipeDir == LEFT) -1 else 1)

        activity?.let {
            if (nextTopic != null) {
                moveWithUndoSnackbar(passStore.classifier, pass, nextTopic, it)
            } else {
                MoveToNewTopicUI(it, passStore, pass).show()
            }
        }
    }

    companion object {
        private const val BUNDLE_KEY_TOPIC = "topic"

        fun newInstance(topic: String) = PassListFragment().apply {
            arguments = bundleOf(BUNDLE_KEY_TOPIC to topic)
        }
    }

}
