package org.ligi.passandroid.ui

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.design.widget.NavigationView
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.navigation_drawer_header.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.ligi.axt.AXT
import org.ligi.passandroid.App
import org.ligi.passandroid.R
import org.ligi.passandroid.events.PassStoreChangeEvent
import org.ligi.passandroid.model.PassStore
import javax.inject.Inject

class PassNavigationView(context: Context, attrs: AttributeSet) : NavigationView(context, attrs) {

    @Inject
    lateinit internal var passStore: PassStore

    @Inject
    lateinit internal var bus: EventBus

    init {

        setNavigationItemSelectedListener(OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_plus -> {
                    AXT.at(getContext()).startCommonIntent().openUrl("https://plus.google.com/communities/116353894782342292067")
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_github -> {
                    AXT.at(getContext()).startCommonIntent().openUrl("https://github.com/ligi/PassAndroid")
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_share -> {
                    AXT.at(getContext()).startCommonIntent().shareUrl(marketUrl)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_beta -> {
                    AXT.at(getContext()).startCommonIntent().openUrl("https://play.google.com/apps/testing/org.ligi.passandroid")
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_settings -> {
                    AXT.at(getContext()).startCommonIntent().activityFromClass(PreferenceActivity::class.java)
                    return@OnNavigationItemSelectedListener true
                }
            }

            false
        })
    }

    override fun inflateHeaderView(@LayoutRes res: Int): View {
        val view = super.inflateHeaderView(res)

        App.component().inject(this)

        bus.register(this)
        onPassStoreChangeEvent(PassStoreChangeEvent)
        return view
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bus.unregister(this)
    }

    private val marketUrl: String
        get() = context.getString(R.string.market_url, context.packageName)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPassStoreChangeEvent(passStoreChangeEvent: PassStoreChangeEvent) {

        val passCount = passStore.passMap.size
        getHeaderView(0).pass_count_header.text = context.getString(R.string.passes_nav, passCount)

        val topicCount = passStore.classifier.getTopics().size
        getHeaderView(0).topic_count_header.text = context.getString(R.string.categories_nav, topicCount)

    }
}
