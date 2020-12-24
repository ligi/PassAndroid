package org.ligi.passandroid.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.edit.*
import org.koin.android.ext.android.inject
import org.ligi.kaxt.doAfterEdit
import org.ligi.passandroid.R
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

    internal val passStore: PassStore by inject()

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
                    0 -> showCategoryPickDialog(this@PassEditActivity, currentPass, refreshCallback)
                    1 -> showColorPickDialog(this@PassEditActivity, currentPass, refreshCallback)
                    2 -> pickImageWithPermissionCheck(ImageEditHelper.REQ_CODE_PICK_ICON)
                }
            }.show()
        }
        passTitle.doAfterEdit {
            currentPass.description = "$it"
        }

        val currentPass = passStore.currentPass
        if (currentPass != null) {
            this.currentPass = currentPass as PassImpl
        } else {
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.commit {
            add(R.id.container_for_primary_fields, FieldsEditFragment.create(false))
            add(R.id.container_for_secondary_fields, FieldsEditFragment.create(true))
        }

        add_barcode_button.setOnClickListener {
            showBarcodeEditDialog(this@PassEditActivity,
                    refreshCallback,
                    this@PassEditActivity.currentPass,
                    BarCode(PassBarCodeFormat.QR_CODE, UUID.randomUUID().toString().toUpperCase()))
        }
    }

    val refreshCallback = { refresh(currentPass) }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageEditHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun refresh(pass: Pass) {
        val passViewHolder = EditViewHolder(pass_card)

        passViewHolder.apply(pass, passStore, this)

        prepareImageUI(R.id.logo_img, R.id.add_logo, ImageEditHelper.REQ_CODE_PICK_LOGO)
        prepareImageUI(R.id.strip_img, R.id.add_strip, ImageEditHelper.REQ_CODE_PICK_STRIP)
        prepareImageUI(R.id.footer_img, R.id.add_footer, ImageEditHelper.REQ_CODE_PICK_FOOTER)

        add_barcode_button.visibility = if (pass.barCode == null) View.VISIBLE else View.GONE
        val barcodeUIController = BarcodeUIController(window.decorView, pass.barCode, this, passViewHelper)
        barcodeUIController.getBarcodeView().setOnClickListener { showBarcodeEditDialog(this@PassEditActivity, refreshCallback, currentPass, currentPass.barCode!!) }
    }

    @Pass.PassBitmap
    private fun prepareImageUI(@IdRes logo_img: Int, @IdRes add_logo: Int, requestCode: Int) {
        val imageString = ImageEditHelper.getImageStringByRequestCode(requestCode)!!

        val bitmap = currentPass.getBitmap(passStore, imageString)

        val addButton = findViewById<Button>(add_logo)!!
        addButton.visibility = if (bitmap == null) View.VISIBLE else View.GONE

        val listener = View.OnClickListener {
            pickImageWithPermissionCheck(requestCode)
        }

        val logoImage = findViewById<ImageView>(logo_img)
        passViewHelper.setBitmapSafe(logoImage, bitmap)
        logoImage.setOnClickListener(listener)
        addButton.setOnClickListener(listener)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        refresh(currentPass)
    }


    override fun onPause() {
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
