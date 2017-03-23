package org.ligi.passandroid.ui.edit.dialogs

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.greenrobot.eventbus.EventBus
import org.ligi.passandroid.R
import org.ligi.passandroid.events.PassRefreshEvent
import org.ligi.passandroid.functions.getCategoryDefaultBG
import org.ligi.passandroid.functions.getHumanCategoryString
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassType
import org.ligi.passandroid.ui.views.BaseCategoryIndicatorView

private val passTypes = arrayOf(PassType.BOARDING, PassType.EVENT, PassType.GENERIC, PassType.LOYALTY, PassType.VOUCHER, PassType.COUPON)

fun showCategoryPickDialog(context: Context, pass: Pass, bus: EventBus) {

    val adapter = object : BaseAdapter() {

        override fun getCount() = passTypes.size

        override fun getItem(position: Int) = passTypes[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(context)
            val inflate = inflater.inflate(R.layout.item_nav_pass_category, parent, false)

            val categoryIndicatorView = inflate.findViewById(R.id.categoryView) as BaseCategoryIndicatorView

            val type = getItem(position)
            categoryIndicatorView.setImageByCategory(type)
            categoryIndicatorView.setAccentColor(getCategoryDefaultBG(type))
            val tv = inflate.findViewById(R.id.navCategoryLabel) as TextView
            tv.setText(getHumanCategoryString(type))

            return inflate
        }
    }

    val builder = AlertDialog.Builder(context)
    builder.setAdapter(adapter) { _, position ->
        pass.type = passTypes[position]
        bus.post(PassRefreshEvent(pass))
    }
    builder.setTitle(R.string.select_category_dialog_title)
    builder.setNegativeButton(android.R.string.cancel, null)
    builder.show()
}
