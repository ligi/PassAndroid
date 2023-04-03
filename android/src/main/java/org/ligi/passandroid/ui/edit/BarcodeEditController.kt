package org.ligi.passandroid.ui.edit

import android.content.Intent
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import org.ligi.kaxt.doAfterEdit
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.BarCode
import org.ligi.passandroid.model.pass.PassBarCodeFormat
import org.ligi.passandroid.model.pass.PassBarCodeFormat.*
import org.ligi.passandroid.ui.BarcodeUIController
import org.ligi.passandroid.ui.PassViewHelper
import java.util.*

class BarcodeEditController(private val rootView: View, internal val context: AppCompatActivity, barCode: BarCode) {
    private var alternativeMessageInput: AppCompatEditText
    private var messageInput: AppCompatEditText
    private var barcodeFormat: PassBarCodeFormat?
    private val intentFragment: Fragment
    private val passFormatRadioButtons: MutableMap<PassBarCodeFormat, RadioButton> = EnumMap(PassBarCodeFormat::class.java)

    class IntentFragment : Fragment() {
        var scanCallback: (format: String, result: String) -> Unit = { _, _ -> }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            data?.let { dataNotNull ->
                val scanResultFormat = dataNotNull.getStringExtra("SCAN_RESULT_FORMAT") ?: return
                val scanResult = dataNotNull.getStringExtra("SCAN_RESULT") ?: return
                scanCallback(scanResultFormat, scanResult)
            }
        }
    }

    private fun bindRadio(formats: Array<PassBarCodeFormat>) {
        formats.forEach {
            val radioButton = RadioButton(context)
            rootView.findViewById<RadioGroup>(R.id.barcodeRadioGroup).addView(radioButton)
            passFormatRadioButtons[it] = radioButton

            radioButton.text = it.name
            radioButton.setOnCheckedChangeListener { _, isChecked ->
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


        messageInput =rootView.findViewById(R.id.messageInput)
        alternativeMessageInput =rootView.findViewById(R.id.alternativeMessageInput)

        rootView.findViewById<AppCompatImageButton>(R.id.randomButton).setOnClickListener {
            messageInput.setText(when (barcodeFormat) {
                EAN_8 -> getRandomEAN8()
                EAN_13 -> getRandomEAN13()
                ITF -> getRandomITF()
                else -> UUID.randomUUID().toString().uppercase(Locale.ROOT)
            })
            refresh()
        }

        rootView.findViewById<View>(R.id.scanButton).setOnClickListener {
            val barCodeIntentIntegrator = BarCodeIntentIntegrator(intentFragment)
            barCodeIntentIntegrator.initiateScan(PassBarCodeFormat.values().map { it.name })
        }

        intentFragment.scanCallback = { newFormat, newMessage ->
            messageInput.setText(newMessage)
            rootView.findViewById<RadioGroup>(R.id.barcodeRadioGroup).check(passFormatRadioButtons[PassBarCodeFormat.valueOf(newFormat)]!!.id)
            refresh()
        }
        context.supportFragmentManager.commit { add(intentFragment, "intent_fragment") }

        bindRadio(PassBarCodeFormat.values())

        messageInput.setText(barCode.message)
        messageInput.doAfterEdit {
            refresh()
        }

        alternativeMessageInput.setText(barCode.alternativeText)

        refresh()
    }

    fun refresh() {
        val barcodeUIController = BarcodeUIController(rootView, getBarCode(), context, PassViewHelper(context))
        val isBarcodeShown = barcodeUIController.getBarcodeView().visibility == View.VISIBLE

        if (!isBarcodeShown) {
            messageInput.error = "Invalid message"
        } else {
            messageInput.error = null
        }
    }

    fun getBarCode() = BarCode(barcodeFormat, messageInput.text.toString()).apply {
        val newAlternativeText = alternativeMessageInput.text.toString()
        if (newAlternativeText.isNotEmpty()) {
            alternativeText = newAlternativeText
        }
    }
}
