package org.ligi.passandroid.model;

import com.google.common.base.Optional;
import com.google.zxing.BarcodeFormat;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePassbook implements Passbook {
    protected Optional<String> organisation = Optional.absent();
    protected String type;
    protected boolean passbook_valid = true; // be positive
    protected String barcodeMessage;
    protected BarcodeFormat barcodeFormat;
    protected int backGroundColor;
    protected int foregroundColor;
    protected String description;
    protected Optional<DateTime> relevantDate = Optional.absent();
    protected Optional<DateTime> expirationDate = Optional.absent();
    protected PassFieldList primaryFields, secondaryFields, backFields, auxiliaryFields, headerFields;
    protected List<PassLocation> locations = new ArrayList<>();


    @Override
    public Optional<String> getOrganisation() {
        return organisation;
    }

    @Override
    public int getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public Optional<DateTime> getRelevantDate() {
        return relevantDate;
    }

    @Override
    public Optional<DateTime> getExpirationDate() {
        return expirationDate;
    }

    @Override
    public int getBackGroundColor() {
        return backGroundColor;
    }


    @Override
    public boolean isValid() {
        return passbook_valid;
    }

    @Override
    public BarcodeFormat getBarcodeFormat() {
        return barcodeFormat;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public PassFieldList getPrimaryFields() {
        return primaryFields;
    }

    @Override
    public PassFieldList getSecondaryFields() {
        return secondaryFields;
    }

    @Override
    public PassFieldList getBackFields() {
        return backFields;
    }

    @Override
    public PassFieldList getAuxiliaryFields() {
        return auxiliaryFields;
    }

    @Override
    public PassFieldList getHeaderFields() {
        return headerFields;
    }

    @Override
    public List<PassLocation> getLocations() {
        return locations;
    }

}
