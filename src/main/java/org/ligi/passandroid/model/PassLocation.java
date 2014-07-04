package org.ligi.passandroid.model;

import java.io.Serializable;

public class PassLocation implements Serializable {

    private final Pass connectedPass;
    private String description;
    public LatLng latlng = new LatLng();

    public PassLocation(Pass connectedPass) {
        this.connectedPass = connectedPass;
    }

    public String getDescription() {
        if (description == null) {
            // fallback for passes with locations without description - e.g. AirBerlin
            return connectedPass.getDescription();
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public class LatLng implements Serializable {
        public double lat;
        public double lon;
    }
}
