package org.ligi.passandroid.model;

import com.google.common.base.Optional;

public abstract class QuirkCorrectingPassbook extends BasePassbook {

    public void correctQuirks() {
        careForTUIFlight();
        careForAirBerlin();
        careForWestbahn();
    }

    private void careForWestbahn() {
        if (!getRelevantDate().isPresent() &&
                getOrganisation().isPresent() &&
                getOrganisation().get().equals("WESTbahn")
                ) {

            final Optional<PassField> originField = getPrimaryFields().getPassFieldForKey("from");
            final Optional<PassField> destinationField = getPrimaryFields().getPassFieldForKey("to");

            description = "WESTbahn";

            if (originField.isPresent()) {
                description = originField.get().value;
            }

            if (destinationField.isPresent()) {
                description += "->" + destinationField.get().value;
            }
        }
    }

    private void careForTUIFlight() {
        if (getDescription().equals("TUIfly pass")) {
            final Optional<PassField> originField = getPrimaryFields().getPassFieldForKey("Origin");
            final Optional<PassField> destinationField = getPrimaryFields().getPassFieldForKey("Des");
            final Optional<PassField> seatField = getAuxiliaryFields().getPassFieldForKey("SeatNumber");

            if (originField.isPresent() && destinationField.isPresent()) {
                description = originField.get().value + "->" + destinationField.get().value;
                if (seatField.isPresent()) {
                    description += " @" + seatField.get().value;
                }
            }
        }
    }


    private void careForAirBerlin() {
        if (getDescription().equals("boardcard")) {
            final String flightRegex = "\\b\\w{1,3}\\d{3,4}\\b";

            Optional<PassField> flightField = getAuxiliaryFields().getPassFieldThatMatchesLabel(flightRegex);


            if (!flightField.isPresent()) {
                flightField = getHeaderFields().getPassFieldThatMatchesLabel(flightRegex);
            }

            final Optional<PassField> seatField = getAuxiliaryFields().getPassFieldForKey("seat");
            final Optional<PassField> boardingGroupField = getSecondaryFields().getPassFieldForKey("boardingGroup");

            if (flightField.isPresent() && seatField.isPresent() && boardingGroupField.isPresent()) {
                description = flightField.get().label + " " + flightField.get().value;
                description += " | " + seatField.get().label + " " + seatField.get().value;
                description += " | " + boardingGroupField.get().label + " " + boardingGroupField.get().value;
            } // otherwise fallback to default - better save than sorry
        }
    }
}
