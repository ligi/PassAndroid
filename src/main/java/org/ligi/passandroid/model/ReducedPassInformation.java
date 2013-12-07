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
        name = pass.getDescription();
        iconPath = pass.getIconPath();
        id = pass.getId();
    }

    public String id;
    public String type;
    public String name;
    public int backgroundColor;
    public String iconPath;
    public DateTime relevantDate;
}
