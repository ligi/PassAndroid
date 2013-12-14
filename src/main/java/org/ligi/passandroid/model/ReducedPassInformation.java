package org.ligi.passandroid.model;

import org.joda.time.DateTime;

import java.io.Serializable;

public class ReducedPassInformation implements Serializable {

    public ReducedPassInformation(Passbook pass) {
        type = pass.getType();
        if (pass.hasRelevantDate()) {
            relevantDate = pass.getRelevantDate();
        }
        backgroundColor = pass.getBackGroundColor();
        foregroundColor = pass.getForegroundColor();
        name = pass.getDescription();
        iconPath = pass.getIconPath();
        id = pass.getId();
        hasLocation = !pass.getLocations().isEmpty();
    }

    public String id;
    public String type;
    public String name;
    public int backgroundColor;
    public int foregroundColor;
    public String iconPath;
    public DateTime relevantDate;
    public boolean hasLocation;

    public String getTypeNotNull() {
        if (type == null) {
            return "none";
        } else {
            return type;
        }
    }
}
