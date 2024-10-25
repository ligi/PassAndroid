package org.ligi.passandroid.ui

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.ligi.kaxt.startActivityFromClass
import org.ligi.kaxt.startActivityFromURL
import org.ligi.passandroid.R
import org.ligi.passandroid.databinding.PassListBinding
import org.ligi.passandroid.functions.createAndAddEmptyPass
import org.ligi.passandroid.model.PassStoreProjection
import org.ligi.passandroid.model.State
import org.ligi.passandroid.scan.PassScanActivity
import org.ligi.snackengage.SnackEngage
import org.ligi.snackengage.snacks.BaseSnack
import org.ligi.snackengage.snacks.DefaultRateSnack
import org.ligi.tracedroid.TraceDroid
import org.ligi.tracedroid.sending.sendTraceDroidStackTracesIfExist
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import permissions.dispatcher.ktx.constructPermissionsRequest

private const val OPEN_FILE_READ_REQUEST_CODE = 1000
private const val VERSION_STARTING_TO_SUPPORT_STORAGE_FRAMEWORK = 19

class PassListActivity : PassAndroidActivity() {

    private lateinit var binding: PassListBinding
    private val drawerToggle by lazy { ActionBarDrawerToggle(this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close) }

    private val adapter by lazy { PassTopicFragmentPagerAdapter(passStore.classifier, supportFragmentManager) }


    @TargetApi(16)
    private fun showDeniedFor() {
        Snackbar.make(binding.fam, "no permission to scan", Snackbar.LENGTH_INDEFINITE).show()
    }

    @TargetApi(16)
    fun scan() = constructPermissionsRequest(Manifest.permission.READ_EXTERNAL_STORAGE, onPermissionDenied = ::showDeniedFor, onNeverAskAgain = ::showDeniedFor) {
        startActivity(Intent(this, PassScanActivity::class.java))
    }.launch()

    @TargetApi(VERSION_STARTING_TO_SUPPORT_STORAGE_FRAMEWORK)
    internal fun onAddOpenFileClick() {
        try {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*" // tried with octet stream - no use
            startActivityForResult(intent, OPEN_FILE_READ_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.fam, "Unavailable", Snackbar.LENGTH_LONG).show()
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == OPEN_FILE_READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                val targetIntent = Intent(this, PassImportActivity::class.java)
                targetIntent.data = resultData.data
                startActivity(targetIntent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = PassListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        // don't want too many windows in worst case - so check for errors first
        if (TraceDroid.stackTraceFiles.isNotEmpty()) {
            tracker.trackEvent("ui_event", "send", "stacktraces", null)

            if (settings.doTraceDroidEmailSend()) {
                sendTraceDroidStackTracesIfExist("ligi+passandroid@ligi.de", this)
            }
        } else { // if no error - check if there is a new version of the app
            tracker.trackEvent("ui_event", "processFile", "updatenotice", null)

            SnackEngage.from(binding.fam).withSnack(DefaultRateSnack().withDuration(BaseSnack.DURATION_INDEFINITE))
                    .build().engageWhenAppropriate()
        }

        binding.drawerLayout.addDrawerListener(drawerToggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.viewPager.adapter = adapter

        if (adapter.count > 0) {
            binding.viewPager.currentItem = State.lastSelectedTab
        }

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                State.lastSelectedTab = position
                invalidateOptionsMenu()
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        passStore.syncPassStoreWithClassifier(getString(R.string.topic_new))

        refresh()

        binding.fabActionCreatePass.setOnClickListener {
            val pass = createAndAddEmptyPass(passStore, resources)

            binding.fam.collapse()
            startActivityFromClass(PassEditActivity::class.java)

            val newTitle = if (binding.tabLayout.selectedTabPosition < 0) {
                getString(R.string.topic_new)
            } else {
                adapter.getPageTitle(binding.tabLayout.selectedTabPosition)
            }

            passStore.classifier.moveToTopic(pass, newTitle)
        }

        binding.fabActionScan.setOnClickListener {
            scan()
            binding.fam.collapse()
        }

        binding.fabActionDemoPass.setOnClickListener {
            startActivityFromURL("http://espass.it/examples")
            binding.fam.collapse()
        }

        if (Build.VERSION.SDK_INT >= VERSION_STARTING_TO_SUPPORT_STORAGE_FRAMEWORK) {
            binding.fabActionOpenFile.setOnClickListener {
                onAddOpenFileClick()
            }
            binding.fabActionOpenFile.visibility = VISIBLE
        }

        lifecycleScope.launch {
            for (update in passStore.updateChannel.openSubscription()) {
                binding.navigationView.passStoreUpdate()

               refresh()
            }


        }
    }

    fun refresh() {
        adapter.notifyDataSetChanged()

        setupWithViewPagerIfNeeded()

        invalidateOptionsMenu()
        val empty = passStore.classifier.topicByIdMap.isEmpty()
        binding.emptyView.visibility = if (empty) VISIBLE else GONE
        val onlyDefaultTopicExists = passStore.classifier.getTopics().all { it == getString(R.string.topic_new) }
        binding.tabLayout.visibility = if (onlyDefaultTopicExists) GONE else VISIBLE
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_help -> {
            startActivityFromClass(HelpActivity::class.java)
            true
        }

        R.id.menu_emptytrash -> {
            AlertDialog.Builder(this)
                    .setMessage(getString(R.string.empty_trash_dialog_message))
                    .setIcon(R.drawable.ic_alert_warning)
                    .setTitle(getString(R.string.empty_trash_dialog_title))
                    .setPositiveButton(R.string.emtytrash_label) { _, _ ->
                        val passStoreProjection = PassStoreProjection(passStore,
                                getString(R.string.topic_trash),
                                null)

                        for (pass in passStoreProjection.passList) {
                            passStore.deletePassWithId(pass.id)
                        }
                    }.setNegativeButton(android.R.string.cancel, null).show()
            true
        }
        else -> drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        adapter.notifyDataSetChanged()
        refresh()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_emptytrash).isVisible = adapter.count > 0 && adapter.getPageTitle(binding.viewPager.currentItem) == getString(R.string.topic_trash)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_pass_list_view, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    private fun setupWithViewPagerIfNeeded() {
        if (!areTabLayoutAndViewPagerInSync()) {
            binding.tabLayout.setupWithViewPager(binding.viewPager)
        }
    }

    private fun areTabLayoutAndViewPagerInSync(): Boolean {
        if (adapter.count != binding.tabLayout.tabCount) {
            return false
        }

        for (i in 0 until adapter.count) {
            val tabAt = binding.tabLayout.getTabAt(i)
            if (tabAt == null || adapter.getPageTitle(i) != tabAt.text) {
                return false
            }
        }
        return true
    }

    override fun onBackPressed() {
        when {
            binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> binding.drawerLayout.closeDrawer(GravityCompat.START)
            binding.fam.isExpanded -> binding.fam.collapse()
            else -> super.onBackPressed()
        }
    }
}