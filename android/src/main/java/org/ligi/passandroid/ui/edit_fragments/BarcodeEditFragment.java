package org.ligi.passandroid.ui.edit_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.zxing.BarcodeFormat;

import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.passandroid.R;
import org.ligi.passandroid.helper.Strings;
import org.ligi.passandroid.model.BarCode;
import org.ligi.passandroid.ui.AsyncSetBarCodeImageTask;

import java.util.Collections;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.text.TextUtils.isEmpty;

public class BarcodeEditFragment extends PassandroidFragment {

    @OnClick(R.id.scanButton)
    public void onScanButtonClick() {
        final BarCodeIntentIntegrator barCodeIntentIntegrator = new BarCodeIntentIntegrator(this);

        if (qrCheck.isChecked()) {
            barCodeIntentIntegrator.initiateScan(BarCodeIntentIntegrator.QR_CODE_TYPES);
        } else if (aztecCheck.isChecked()) {
            barCodeIntentIntegrator.initiateScan(Collections.singleton("AZTEC"));
        } else if (pdfCheck.isChecked()) {
            barCodeIntentIntegrator.initiateScan(Collections.singleton("PDF417"));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && data.hasExtra("SCAN_RESULT")) {
            messageInput.setText(data.getStringExtra("SCAN_RESULT"));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Bind(R.id.selectorQR)
    ImageView selectorQR;

    @Bind(R.id.selectorPDF417)
    ImageView selectorPDF417;

    @Bind(R.id.selectorAZTEC)
    ImageView selectorAztec;

    @Bind(R.id.messageInput)
    EditText messageInput;

    @Bind(R.id.alternativeMessageInput)
    EditText alternativeMessageInput;

    @Bind(R.id.PDFCheck)
    RadioButton pdfCheck;

    @Bind(R.id.QRCheck)
    RadioButton qrCheck;

    @Bind(R.id.AZTecCheck)
    RadioButton aztecCheck;

    @Bind(R.id.barcodeTypeRadioGroup)
    RadioGroup typeGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getPass().getBarCode() != null) {
            getPass().setBarCode(new BarCode(BarcodeFormat.QR_CODE, UUID.randomUUID().toString()));
        }

        final View inflate = inflater.inflate(R.layout.edit_barcode, container, false);
        ButterKnife.bind(this, inflate);

        final BarCode barCode = getPass().getBarCode();

        if (barCode != null) {
            messageInput.setText(barCode.getMessage());
            final String alternativeTextOptional = barCode.getAlternativeText();

            alternativeMessageInput.setText(Strings.nullToEmpty(alternativeTextOptional));

            getCheckboxForBarcodeType(barCode).setChecked(true);
        }

        refresh();

        final SimpleTextWatcher refreshingTextWatcher = new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                refresh();
            }
        };

        messageInput.addTextChangedListener(refreshingTextWatcher);
        alternativeMessageInput.addTextChangedListener(refreshingTextWatcher);

        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                refresh();
            }
        });

        return inflate;
    }

    private void refresh() {

        String message = messageInput.getText().toString();
        if (isEmpty(message)) {
            message = " ";
        }

        final BarcodeFormat format = getBarcodeFormatFromCheckedState();

        final BarCode newBarCode = new BarCode(format, message);
        newBarCode.setAlternativeText(alternativeMessageInput.getText().toString());
        getPass().setBarCode(newBarCode);

        new AsyncSetBarCodeImageTask(selectorQR).execute(new BarCode(BarcodeFormat.QR_CODE, message));
        new AsyncSetBarCodeImageTask(selectorPDF417).execute(new BarCode(BarcodeFormat.PDF_417, message));
        new AsyncSetBarCodeImageTask(selectorAztec).execute(new BarCode(BarcodeFormat.AZTEC, message));
    }

    private BarcodeFormat getBarcodeFormatFromCheckedState() {
        if (pdfCheck.isChecked()) {
            return BarcodeFormat.PDF_417;
        } else if (aztecCheck.isChecked()) {
            return BarcodeFormat.AZTEC;
        }
        return BarcodeFormat.QR_CODE; // default/fallback
    }

    private RadioButton getCheckboxForBarcodeType(BarCode barCode) {
        switch (barCode.getFormat()) {
            case PDF_417:
                return pdfCheck;
            case AZTEC:
                return aztecCheck;

            default:
            case QR_CODE:
                return pdfCheck;
        }
    }

}