package org.ligi.passandroid.model.pass;

public class PassLocation {

    private String name;
    public double lat;
    public double lon;

    public String getName(Pass pass) {
        if (name == null) {
            // fallback for passes with locations without description - e.g. AirBerlin
            return pass.getDescription();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
