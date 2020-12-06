package org.ligi.passandroid.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ligi.kaxt.startActivityFromClass
import org.ligi.passandroid.R
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.PassStoreProjection
import org.ligi.passandroid.model.Settings
import org.ligi.passandroid.ui.pass_view_holder.CondensedPassViewHolder
import org.ligi.passandroid.ui.pass_view_holder.PassViewHolder
import org.ligi.passandroid.ui.pass_view_holder.VerbosePassViewHolder

class PassAdapter(
        private val passListActivity: AppCompatActivity,
        private val passStoreProjection: PassStoreProjection
) : RecyclerView.Adapter<PassViewHolder>(), KoinComponent {

    private val passStore: PassStore by inject ()
    private val settings: Settings by inject ()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PassViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)

        val res = inflater.inflate(R.layout.pass_list_item, viewGroup, false) as CardView
        return if (settings.isCondensedModeEnabled()) {
            CondensedPassViewHolder(res)
        } else {
            VerbosePassViewHolder(res)
        }
    }

    override fun onBindViewHolder(viewHolder: PassViewHolder, position: Int) {
        val pass = passStoreProjection.passList[position]

        viewHolder.apply(pass, passStore, passListActivity)

        val root = viewHolder.view

        root.setOnClickListener {
            passStore.currentPass = pass
            passListActivity.startActivityFromClass(PassViewActivity::class.java)
        }

        root.setOnLongClickListener {
            Snackbar.make(root, R.string.please_use_the_swipe_feature, Snackbar.LENGTH_LONG).show()
            true
        }
    }

    override fun getItemId(position: Int) = position.toLong()
    private val list = passStoreProjection.passList
    override fun getItemCount() = list.size

}
