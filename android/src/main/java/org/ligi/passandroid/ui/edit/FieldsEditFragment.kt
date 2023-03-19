package org.ligi.passandroid.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import org.koin.android.ext.android.inject
import org.ligi.kaxt.doAfterEdit
import org.ligi.passandroid.R
import org.ligi.passandroid.databinding.EditFieldsBinding
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.PassField
import org.ligi.passandroid.model.pass.PassImpl


class FieldsEditFragment : Fragment() {

    val passStore by inject<PassStore>()

    private fun getPass(): PassImpl = passStore.currentPass as PassImpl

    private var isEditingHiddenFields: Boolean = false

    private lateinit var inflater: LayoutInflater

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.inflater = inflater
        val inflate = EditFieldsBinding.inflate(layoutInflater, container, false)

        arguments?.let { isEditingHiddenFields = it.getBoolean(ARGUMENT_KEY) }

        if (isEditingHiddenFields) {
            inflate.addField.setText(R.string.add_back_fields)
        } else {
            inflate.addField.setText(R.string.add_front_field)
        }
        getPass().fields
            .filter { it.hide == isEditingHiddenFields }
            .forEach { addField(it, inflate.fieldsContainer) }

        inflate.addField.setOnClickListener {
            val passField = PassField(null, null, null, isEditingHiddenFields)
            addField(passField, inflate.fieldsContainer)
            getPass().fields.add(passField)
        }
        return inflate.root
    }

    private fun addField(passField: PassField, viewGroup: ViewGroup) {
        val child = inflater.inflate(R.layout.edit_field, viewGroup, false) as ViewGroup
        FieldView(child).apply(passField, getPass().fields)
        viewGroup.addView(child)
    }

    internal inner class FieldView(private val container: ViewGroup) {

        fun apply(passField: PassField, fields: MutableList<PassField>) {
            container.findViewById<TextInputEditText>(R.id.label_field_edit).apply {
                setText(passField.label)
                doAfterEdit {
                    passField.label = "$it"
                }
            }
            container.findViewById<TextInputEditText>(R.id.value_field_edit).apply {
                setText(passField.value)
                doAfterEdit {
                    passField.value = "$it"
                }
            }

            container.findViewById<View>(R.id.delete_button).setOnClickListener { v ->
                (v.parent as View).visibility = View.GONE
                fields.remove(passField)
            }

        }
    }

    companion object {

        private const val ARGUMENT_KEY = "KEY"

        fun create(primary: Boolean): FieldsEditFragment {
            val fieldsEditFragment = FieldsEditFragment()
            fieldsEditFragment.arguments = bundleOf(ARGUMENT_KEY to primary)
            return fieldsEditFragment
        }
    }

}
