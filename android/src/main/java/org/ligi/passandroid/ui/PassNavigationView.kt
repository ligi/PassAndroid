package org.ligi.passandroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.core.net.toUri
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.navigation_drawer_header.view.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ligi.passandroid.R
import org.ligi.passandroid.model.PassStore

class PassNavigationView(context: Context, attrs: AttributeSet) : NavigationView(context, attrs), KoinComponent {

    val passStore: PassStore by inject()

    private fun getIntent(id: Int) = when (id) {
        R.id.menu_settings -> Intent(context, PreferenceActivity::class.java)
        R.id.menu_github -> intentFromUrl("https://github.com/ligi/PassAndroid")
        R.id.menu_beta -> intentFromUrl("https://play.google.com/apps/testing/org.ligi.passandroid")
        R.id.menu_language -> intentFromUrl("https://transifex.com/projects/p/passandroid")
        R.id.menu_share -> Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, marketUrl)
            type = "text/plain"
        }
        else -> null
    }

    private fun intentFromUrl(url: String) = Intent(Intent.ACTION_VIEW).apply { data = url.toUri() }

    @SuppressLint("RestrictedApi") // FIXME: temporary workaround for false-positive
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setNavigationItemSelectedListener { item ->
            getIntent(item.itemId)?.let {
                context.startActivity(it)
                true
            } ?: false
        }

        passStoreUpdate()
    }

    private val marketUrl by lazy { context.getString(R.string.market_url, context.packageName) }

    fun passStoreUpdate() {

        val passCount = passStore.passMap.size
        getHeaderView(0).pass_count_header.text = context.getString(R.string.passes_nav, passCount)

        val topicCount = passStore.classifier.getTopics().size
        getHeaderView(0).topic_count_header.text = context.getString(R.string.categories_nav, topicCount)

    }
}
