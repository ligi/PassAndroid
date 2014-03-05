package org.ligi.passandroid.model;

import com.google.common.base.Optional;

public class PrettifiedPassbookDecorator {

    private String prettifiedDescription;
    private final Passbook sourcePassbook;

    public PrettifiedPassbookDecorator(Passbook sourcePassbook) {
        this.sourcePassbook = sourcePassbook;

        // default is what we have
        prettifiedDescription = sourcePassbook.getDescription();

        careForAirBerlin();
        careForTUIFlight();
    }

    private void careForTUIFlight() {
        if (sourcePassbook.getDescription().equals("TUIfly pass")) {
            Optional<PassField> flightField = sourcePassbook.getAuxiliaryFields().getPassFieldForKey("Flug");
            Optional<PassField> originField = sourcePassbook.getPrimaryFields().getPassFieldForKey("Origin");
            Optional<PassField> destinationField = sourcePassbook.getPrimaryFields().getPassFieldForKey("Des");
            Optional<PassField> seatField = sourcePassbook.getAuxiliaryFields().getPassFieldForKey("SeatNumber");

            if (flightField.isPresent() && originField.isPresent() && destinationField.isPresent() && seatField.isPresent()) {
                prettifiedDescription = flightField.get().value + " " + originField.get().value + "->" + destinationField.get().value + " @" + seatField.get().value;
            }
        }
    }

    private void careForAirBerlin() {
        if (sourcePassbook.getDescription().equals("boardcard")) {
            final String flightRegex = "\\b\\w{1,3}\\d{3,4}\\b";

            Optional<PassField> flightField = sourcePassbook.getAuxiliaryFields().getPassFieldThatMatchesLabel(flightRegex);
            Optional<PassField> seatField = sourcePassbook.getAuxiliaryFields().getPassFieldForKey("seat");
            Optional<PassField> boardingGroupField = sourcePassbook.getSecondaryFields().getPassFieldForKey("boardingGroup");

            if (flightField.isPresent() && seatField.isPresent() && boardingGroupField.isPresent()) {
                prettifiedDescription = flightField.get().label + " " + flightField.get().value;
                prettifiedDescription = " | " + seatField.get().label + " " + seatField.get().value;
                prettifiedDescription = " | " + boardingGroupField.get().label + " " + boardingGroupField.get().value;
            } // otherwise fallback to default - better save than sorry
        }
    }


    public String getPrettyDescription() {
        return prettifiedDescription;
    }
}
