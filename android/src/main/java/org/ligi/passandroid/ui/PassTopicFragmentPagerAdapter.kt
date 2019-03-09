package org.ligi.passandroid.ui

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import org.ligi.passandroid.model.PassClassifier

class PassTopicFragmentPagerAdapter(private val passClassifier: PassClassifier, fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    private lateinit var topics: Array<String>

    init {
        notifyDataSetChanged()
    }

    override fun notifyDataSetChanged() {
        val topics = passClassifier.getTopics()
        this.topics = topics.toTypedArray()
        super.notifyDataSetChanged()
    }

    override fun getItem(position: Int) = PassListFragment.newInstance(topics[position])

    // TODO - return POSITION_UNCHANGED in some cases
    override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE

    override fun getCount() = topics.size

    override fun getPageTitle(position: Int) = topics[position]
}