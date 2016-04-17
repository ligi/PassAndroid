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
import org.ligi.passandroid.model.pass.PassBarCodeFormat.*
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
        randomButton.setOnClickListener({
            messageInput.setText(UUID.randomUUID().toString().toUpperCase())
        })

        intentFragment = IntentFragment()
        barcodeFormat = barCode.format

        scanButton.setOnClickListener({
            val barCodeIntentIntegrator = BarCodeIntentIntegrator(intentFragment)

            if (barcodeFormat == QR_CODE) {
                barCodeIntentIntegrator.initiateScan(BarCodeIntentIntegrator.QR_CODE_TYPES)
            } else if (barcodeFormat == AZTEC) {
                barCodeIntentIntegrator.initiateScan(setOf("AZTEC"))
            } else if (barcodeFormat == PDF_417) {
                barCodeIntentIntegrator.initiateScan(setOf("PDF417"))
            } else if (barcodeFormat == CODE_39) {
                barCodeIntentIntegrator.initiateScan(setOf("CODE_39"))
            } else if (barcodeFormat == CODE_128) {
                barCodeIntentIntegrator.initiateScan(setOf("CODE_128"))
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
        BarcodeUIController(rootView, barCode, context, PassViewHelper(context))
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
