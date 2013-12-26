package org.ligi.passandroid.model;

public class PassLocation {

    private final Passbook connectedPass;
    private String description;

    public PassLocation(Passbook connectedPass) {
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

    public LatLng latlng = new LatLng();


    public class LatLng {
        public double lat;
        public double lon;
    }
}
