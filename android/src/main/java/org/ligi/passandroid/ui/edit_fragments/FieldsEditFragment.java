package org.ligi.passandroid.ui.edit_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.List;
import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.pass.PassField;

public class FieldsEditFragment extends PassandroidFragment {

    @Bind(R.id.fields_container)
    ViewGroup viewGroup;

    @OnClick(R.id.add_field)
    void onAddField() {
        final PassField passField = new PassField(null, null, null, false);
        addField(passField);
        getPass().getFields().add(passField);
    }

    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater=inflater;
        final View inflate = inflater.inflate(R.layout.edit_fields, container, false);
        ButterKnife.bind(this, inflate);

        for (final PassField passField : getPass().getFields()) {
            addField(passField);
        }
        return inflate;
    }

    public void addField(final PassField passField) {
        final ViewGroup child = (ViewGroup) inflater.inflate(R.layout.edit_field, viewGroup, false);
        new FieldView(child).apply(passField, getPass().getFields());
        viewGroup.addView(child);
    }

    class FieldView {
        @Bind(R.id.label_field_edit)
        TextInputEditText labelEdit;

        @Bind(R.id.value_field_edit)
        TextInputEditText valueEdit;

        @Bind(R.id.delete_button)
        ImageButton deleteButton;


        @Bind(R.id.hide_switch)
        SwitchCompat hideSwitch;

        FieldView(final ViewGroup container) {
            ButterKnife.bind(this,container);
        }

        public void apply(final PassField passField, final List<PassField> fields) {
            labelEdit.setText(passField.getLabel());
            valueEdit.setText(passField.getValue());
            hideSwitch.setChecked(passField.getHide());

            hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                    passField.setHide(isChecked);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    ((View)(v.getParent())).setVisibility(View.GONE);
                    fields.remove(passField);
                }
            });

            valueEdit.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(final Editable s) {
                    passField.setValue(s.toString());
                }
            });

            labelEdit.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(final Editable s) {
                    passField.setLabel(s.toString());
                }
            });
        }
    }

}
