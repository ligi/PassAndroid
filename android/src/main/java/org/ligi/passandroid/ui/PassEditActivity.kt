package org.ligi.passandroid.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.edit.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.ligi.kaxt.doAfterEdit
import org.ligi.passandroid.App
import org.ligi.passandroid.R
import org.ligi.passandroid.events.PassRefreshEvent
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.BarCode
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassBarCodeFormat
import org.ligi.passandroid.model.pass.PassImpl
import org.ligi.passandroid.ui.edit.FieldsEditFragment
import org.ligi.passandroid.ui.edit.ImageEditHelper
import org.ligi.passandroid.ui.edit.dialogs.showBarcodeEditDialog
import org.ligi.passandroid.ui.edit.dialogs.showCategoryPickDialog
import org.ligi.passandroid.ui.edit.dialogs.showColorPickDialog
import org.ligi.passandroid.ui.pass_view_holder.EditViewHolder
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.*

@RuntimePermissions
class PassEditActivity : AppCompatActivity() {

    private lateinit var currentPass: PassImpl
    private val imageEditHelper by lazy { ImageEditHelper(this, passStore) }

    internal val passStore: PassStore = App.kodein.instance()
    internal val bus: EventBus = App.kodein.instance()

    private val passViewHelper: PassViewHelper by lazy { PassViewHelper(this) }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun pickImage(req_code_pick_icon: Int) {
        imageEditHelper.startPick(req_code_pick_icon)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.edit)

        categoryView.setOnClickListener {
            AlertDialog.Builder(this).setItems(R.array.category_edit_options) { _, i ->
                when (i) {
                    0 -> showCategoryPickDialog(this@PassEditActivity, currentPass, bus)
                    1 -> showColorPickDialog(this@PassEditActivity, currentPass, bus)
                    2 -> PassEditActivityPermissionsDispatcher.pickImageWithCheck(this@PassEditActivity, ImageEditHelper.REQ_CODE_PICK_ICON)
                }
            }.show()
        }
        passTitle.doAfterEdit {
            currentPass.description = it.toString()
        }

        val currentPass = passStore.currentPass
        if (currentPass != null) {
            this.currentPass = currentPass as PassImpl
        } else {
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.container_for_primary_fields, FieldsEditFragment.create(false))
        fragmentTransaction.add(R.id.container_for_secondary_fields, FieldsEditFragment.create(true))

        fragmentTransaction.commit()

        add_barcode_button.setOnClickListener {
            showBarcodeEditDialog(this@PassEditActivity,
                    bus,
                    this@PassEditActivity.currentPass,
                    BarCode(PassBarCodeFormat.QR_CODE, UUID.randomUUID().toString().toUpperCase()))
        }
    }


    @Subscribe
    fun onPassRefresh(event: PassRefreshEvent) {
        refresh(currentPass)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageEditHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PassEditActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    private fun refresh(pass: Pass) {
        val passViewHolder = EditViewHolder(pass_card)

        passViewHolder.apply(pass, passStore, this)

        prepareImageUI(R.id.logo_img, R.id.add_logo, ImageEditHelper.REQ_CODE_PICK_LOGO)
        prepareImageUI(R.id.strip_img, R.id.add_strip, ImageEditHelper.REQ_CODE_PICK_STRIP)
        prepareImageUI(R.id.footer_img, R.id.add_footer, ImageEditHelper.REQ_CODE_PICK_FOOTER)

        add_barcode_button.visibility = if (pass.barCode == null) View.VISIBLE else View.GONE
        val barcodeUIController = BarcodeUIController(window.decorView, pass.barCode, this, passViewHelper)
        barcodeUIController.getBarcodeView().setOnClickListener { showBarcodeEditDialog(this@PassEditActivity, bus, currentPass, currentPass.barCode!!) }
    }

    @Pass.PassBitmap
    private fun prepareImageUI(@IdRes logo_img: Int, @IdRes add_logo: Int, requestCode: Int) {
        val imageString = ImageEditHelper.getImageStringByRequestCode(requestCode)!!

        val bitmap = currentPass.getBitmap(passStore, imageString)

        val addButton = findViewById(add_logo)!!
        addButton.visibility = if (bitmap == null) View.VISIBLE else View.GONE

        val listener = View.OnClickListener {
            PassEditActivityPermissionsDispatcher.pickImageWithCheck(this@PassEditActivity, requestCode)
        }

        val logoImage = findViewById(logo_img) as ImageView
        passViewHelper.setBitmapSafe(logoImage, bitmap)
        logoImage.setOnClickListener(listener)
        addButton.setOnClickListener(listener)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        bus.register(this)
        refresh(currentPass)
    }


    override fun onPause() {
        bus.unregister(this)
        passStore.save(currentPass)
        passStore.notifyChange()
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)

    }

}
