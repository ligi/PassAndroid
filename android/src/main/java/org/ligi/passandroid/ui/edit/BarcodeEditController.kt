package org.ligi.passandroid.ui.edit

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import org.ligi.axt.simplifications.SimpleTextWatcher
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.BarCode
import org.ligi.passandroid.model.pass.PassBarCodeFormat
import org.ligi.passandroid.model.pass.PassBarCodeFormat.EAN_13
import org.ligi.passandroid.model.pass.PassBarCodeFormat.QR_CODE
import org.ligi.passandroid.ui.BarcodeUIController
import org.ligi.passandroid.ui.PassViewHelper
import java.util.*

class BarcodeEditController(val rootView: View, internal val context: AppCompatActivity, barCode: BarCode) {

    val alternativeMessageInput: EditText by lazy { rootView.findViewById(R.id.alternativeMessageInput) as EditText }
    val messageInput: EditText by lazy { rootView.findViewById(R.id.messageInput) as EditText }
    val scanButton: ImageButton by lazy { rootView.findViewById(R.id.scanButton) as ImageButton }
    val randomButton: ImageButton by lazy { rootView.findViewById(R.id.randomButton) as ImageButton }
    val radioGroup: RadioGroup by lazy { rootView.findViewById(R.id.barcodeRadioGroup) as RadioGroup }

    var barcodeFormat: PassBarCodeFormat?

    internal val intentFragment: Fragment

    class IntentFragment : Fragment() {

        var scanCallback: (String) -> Unit = {}

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (data != null && data.hasExtra("SCAN_RESULT")) {
                scanCallback(data.getStringExtra("SCAN_RESULT"));
            }
        }
    }

    private fun bindRadio(formats: Array<PassBarCodeFormat>) {
        formats.forEach {
            val radioButton = RadioButton(context)
            radioGroup.addView(radioButton)

            radioButton.text = it.name
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    barcodeFormat = it
                    refresh()
                }
            }

            radioButton.isChecked = barcodeFormat == it
        }

    }


    init {
        intentFragment = IntentFragment()
        barcodeFormat = barCode.format

        randomButton.setOnClickListener({
            if (barcodeFormat == EAN_13) {
                messageInput.setText(EANHelper.getRandomEAN13())
            } else {
                messageInput.setText(UUID.randomUUID().toString().toUpperCase())
            }

            refresh()
        })



        scanButton.setOnClickListener({
            val barCodeIntentIntegrator = BarCodeIntentIntegrator(intentFragment)

            if (barcodeFormat == QR_CODE) {
                barCodeIntentIntegrator.initiateScan(BarCodeIntentIntegrator.QR_CODE_TYPES)
            } else {
                barCodeIntentIntegrator.initiateScan(setOf(barcodeFormat!!.name))
            }

        })

        intentFragment.scanCallback = { newMessage ->
            messageInput.setText(newMessage)
            refresh()
        }
        context.supportFragmentManager.beginTransaction().add(intentFragment, "intent_fragment").commit()

        bindRadio(PassBarCodeFormat.values())

        messageInput.setText(barCode.message)
        messageInput.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                refresh()
            }
        })

        alternativeMessageInput.setText(barCode.alternativeText)

        refresh()
    }

    fun refresh() {
        val barcodeUIController = BarcodeUIController(rootView, barCode, context, PassViewHelper(context))
        val isBarcodeShown = barcodeUIController.barcode_img.visibility == View.VISIBLE

        if (!isBarcodeShown) {
            messageInput.error = "Invalid message"
        } else {
            messageInput.error = null
        }
    }

    val barCode: BarCode
        get() {
            val barCode = BarCode(barcodeFormat, messageInput.text.toString())
            val alternativeText = alternativeMessageInput.text.toString()
            if (!alternativeText.isEmpty()) {
                barCode.alternativeText = alternativeText
            }
            return barCode
        }
}
