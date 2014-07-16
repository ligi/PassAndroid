package org.ligi.passandroid.model;

import com.google.common.base.Optional;

public class ApplePassbookQuirkCorrector {

    public static void correctQuirks(PassImpl pass ) {
        careForTUIFlight(pass);
        careForAirBerlin(pass);
        careForWestbahn(pass);
        careForAirCanada(pass);
    }

    private static void careForAirCanada(PassImpl pass) {
        if (!pass.getOrganisation().isPresent() || !pass.getOrganisation().get().equals("Air Canada")) {
            return;
        }

        final Optional<PassField> optionalDepart = pass.getPrimaryFields().getPassFieldForKey("depart");
        final Optional<PassField> optionalArrive = pass.getPrimaryFields().getPassFieldForKey("arrive");

        if (optionalDepart.isPresent() && optionalArrive.isPresent()) {
            pass.setDescription(optionalDepart.get().label + " -> " + optionalArrive.get().label);
        }
    }

    private static void careForWestbahn(PassImpl pass) {
        if (!pass.getRelevantDate().isPresent() &&
                pass.getOrganisation().isPresent() &&
                pass.getOrganisation().get().equals("WESTbahn")
                ) {

            final Optional<PassField> originField = pass.getPrimaryFields().getPassFieldForKey("from");
            final Optional<PassField> destinationField = pass.getPrimaryFields().getPassFieldForKey("to");

            String description = "WESTbahn";

            if (originField.isPresent()) {
                description = originField.get().value;
            }

            if (destinationField.isPresent()) {
                description += "->" + destinationField.get().value;
            }

            pass.setDescription(description);
        }
    }

    private static void careForTUIFlight(PassImpl pass) {
        if (pass.getDescription().equals("TUIfly pass")) {
            final Optional<PassField> originField = pass.getPrimaryFields().getPassFieldForKey("Origin");
            final Optional<PassField> destinationField = pass.getPrimaryFields().getPassFieldForKey("Des");
            final Optional<PassField> seatField = pass.getAuxiliaryFields().getPassFieldForKey("SeatNumber");

            if (originField.isPresent() && destinationField.isPresent()) {
                String description = originField.get().value + "->" + destinationField.get().value;
                if (seatField.isPresent()) {
                    description += " @" + seatField.get().value;
                }
                pass.setDescription(description);
            }
        }
    }


    private static void careForAirBerlin(PassImpl pass) {
        if (pass.getDescription().equals("boardcard")) {
            final String flightRegex = "\\b\\w{1,3}\\d{3,4}\\b";

            Optional<PassField> flightField = pass.getAuxiliaryFields().getPassFieldThatMatchesLabel(flightRegex);


            if (!flightField.isPresent()) {
                flightField = pass.getHeaderFields().getPassFieldThatMatchesLabel(flightRegex);
            }

            final Optional<PassField> seatField = pass.getAuxiliaryFields().getPassFieldForKey("seat");
            final Optional<PassField> boardingGroupField = pass.getSecondaryFields().getPassFieldForKey("boardingGroup");

            if (flightField.isPresent() && seatField.isPresent() && boardingGroupField.isPresent()) {
                String description = flightField.get().label + " " + flightField.get().value;
                description += " | " + seatField.get().label + " " + seatField.get().value;
                description += " | " + boardingGroupField.get().label + " " + boardingGroupField.get().value;
                pass.setDescription(description);
            } // otherwise fallback to default - better save than sorry
        }
    }
}
