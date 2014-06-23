package org.ligi.passandroid.model;

import com.google.common.base.Optional;

import org.joda.time.DateTime;

import java.io.Serializable;

public class ReducedPassInformation implements Serializable {

    public String id;
    public String type;
    public String name;
    public int backgroundColor;
    public int foregroundColor;
    public String iconPath;
    public Optional<DateTime> relevantDate;
    public Optional<DateTime> expirationDate;
    public boolean hasLocation;

    public ReducedPassInformation(Passbook pass) {
        type = pass.getType();
        relevantDate = pass.getRelevantDate();
        expirationDate = pass.getExpirationDate();
        backgroundColor = pass.getBackGroundColor();
        foregroundColor = pass.getForegroundColor();
        name = pass.getDescription();
        iconPath = pass.getIconPath();
        id = pass.getId();
        hasLocation = !pass.getLocations().isEmpty();
    }

    public String getTypeNotNull() {
        if (type == null) {
            return "none";
        } else {
            return type;
        }
    }
}
