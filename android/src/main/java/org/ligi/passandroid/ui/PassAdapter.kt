package org.ligi.passandroid.ui

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.salomonbrys.kodein.instance
import org.ligi.kaxt.startActivityFromClass
import org.ligi.passandroid.App
import org.ligi.passandroid.R
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.PassStoreProjection
import org.ligi.passandroid.model.Settings
import org.ligi.passandroid.ui.pass_view_holder.CondensedPassViewHolder
import org.ligi.passandroid.ui.pass_view_holder.PassViewHolder
import org.ligi.passandroid.ui.pass_view_holder.VerbosePassViewHolder

class PassAdapter(private val passListActivity: AppCompatActivity, private val passStoreProjection: PassStoreProjection) : RecyclerView.Adapter<PassViewHolder>() {

    val passStore: PassStore = App.kodein.instance()
    val settings: Settings = App.kodein.instance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PassViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)

        val res = inflater.inflate(R.layout.pass_list_item, viewGroup, false) as CardView
        if (settings.isCondensedModeEnabled()) {
            return CondensedPassViewHolder(res)
        } else {
            return VerbosePassViewHolder(res)
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
