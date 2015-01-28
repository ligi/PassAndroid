package org.ligi.passandroid.model;

import com.google.common.base.Optional;

import org.ligi.passandroid.Tracker;

public class ApplePassbookQuirkCorrector {

    public static void correctQuirks(PassImpl pass) {
        careForTUIFlight(pass);
        careForAirBerlin(pass);
        careForWestbahn(pass);
        careForAirCanada(pass);
        careForUSAirways(pass);
        careForVirginAustralia(pass);
        careForCathayPacific(pass);
        careForSWISS(pass);
        careForRenfe(pass);
    }

    private static void careForRenfe(PassImpl pass) {
        if (!pass.getOrganisation().isPresent() || !pass.getOrganisation().get().equals("RENFE OPERADORA")) {
            return;
        }
        Tracker.get().trackEvent("quirk_fix", "description_replace", "RENFE OPERADORA", 0L);

        final Optional<PassField> optionalDepart = pass.getPrimaryFields().getPassFieldForKey("boardingTime");
        final Optional<PassField> optionalArrive = pass.getPrimaryFields().getPassFieldForKey("destino");

        if (optionalDepart.isPresent() && optionalArrive.isPresent()) {
            Tracker.get().trackEvent("quirk_fix", "description_replace", "RENFE OPERADORA", 1L);
            pass.setDescription(optionalDepart.get().label + " -> " + optionalArrive.get().label);
        }
    }

    private static void careForSWISS(PassImpl pass) {
        if (!pass.getOrganisation().isPresent() || !pass.getOrganisation().get().equals("SWISS")) {
            return;
        }
        Tracker.get().trackEvent("quirk_fix", "description_replace", "SWISS", 0L);

        final Optional<PassField> optionalDepart = pass.getPrimaryFields().getPassFieldForKey("depart");
        final Optional<PassField> optionalArrive = pass.getPrimaryFields().getPassFieldForKey("destination");

        if (optionalDepart.isPresent() && optionalArrive.isPresent()) {
            Tracker.get().trackEvent("quirk_fix", "description_replace", "SWISS", 1L);
            pass.setDescription(optionalDepart.get().value + " -> " + optionalArrive.get().value);
        }

    }


    private static void careForCathayPacific(PassImpl pass) {
        if (!pass.getOrganisation().isPresent() || !pass.getOrganisation().get().equals("Cathay Pacific")) {

            return;
        }

        Tracker.get().trackEvent("quirk_fix", "description_replace", "cathay_pacific", 0L);

        final Optional<PassField> optionalDepart = pass.getPrimaryFields().getPassFieldForKey("departure");
        final Optional<PassField> optionalArrive = pass.getPrimaryFields().getPassFieldForKey("arrival");

        if (optionalDepart.isPresent() && optionalArrive.isPresent()) {
            Tracker.get().trackEvent("quirk_fix", "description_replace", "cathay_pacific", 1L);
            pass.setDescription(optionalDepart.get().label + " -> " + optionalArrive.get().label);
        }
    }


    private static void careForVirginAustralia(PassImpl pass) {
        // also good identifier could be  "passTypeIdentifier": "pass.com.virginaustralia.boardingpass
        if (!pass.getOrganisation().isPresent() || !pass.getOrganisation().get().equals("Virgin Australia")) {

            return;
        }

        Tracker.get().trackEvent("quirk_fix", "description_replace", "virgin_australia", 0L);

        final Optional<PassField> optionalDepart = pass.getPrimaryFields().getPassFieldForKey("origin");
        final Optional<PassField> optionalArrive = pass.getPrimaryFields().getPassFieldForKey("destination");

        if (optionalDepart.isPresent() && optionalArrive.isPresent()) {
            Tracker.get().trackEvent("quirk_fix", "description_replace", "virgin_australia", 1L);
            pass.setDescription(optionalDepart.get().label + " -> " + optionalArrive.get().label);
        }
    }

    private static void careForAirCanada(PassImpl pass) {
        if (!pass.getOrganisation().isPresent() || !pass.getOrganisation().get().equals("Air Canada")) {
            return;
        }

        Tracker.get().trackEvent("quirk_fix", "description_replace", "air_canada", 0L);

        final Optional<PassField> optionalDepart = pass.getPrimaryFields().getPassFieldForKey("depart");
        final Optional<PassField> optionalArrive = pass.getPrimaryFields().getPassFieldForKey("arrive");

        if (optionalDepart.isPresent() && optionalArrive.isPresent()) {
            Tracker.get().trackEvent("quirk_fix", "description_replace", "air_canada", 1L);
            pass.setDescription(optionalDepart.get().label + " -> " + optionalArrive.get().label);
        }
    }

    private static void careForUSAirways(PassImpl pass) {
        if (!pass.getOrganisation().isPresent() || !pass.getOrganisation().get().equals("US Airways")) {
            return;
        }

        Tracker.get().trackEvent("quirk_fix", "description_replace", "usairways", 0L);

        final Optional<PassField> optionalDepart = pass.getPrimaryFields().getPassFieldForKey("depart");
        final Optional<PassField> optionalArrive = pass.getPrimaryFields().getPassFieldForKey("destination");

        if (optionalDepart.isPresent() && optionalArrive.isPresent()) {
            Tracker.get().trackEvent("quirk_fix", "description_replace", "usairways", 1L);
            pass.setDescription(optionalDepart.get().label + " -> " + optionalArrive.get().label);
        }
    }

    private static void careForWestbahn(PassImpl pass) {
        if (pass.getRelevantDate().isPresent() ||
                !pass.getOrganisation().isPresent() ||
                !pass.getOrganisation().get().equals("WESTbahn")
                ) {
            return;
        }

        Tracker.get().trackEvent("quirk_fix", "description_replace", "westbahn", 0L);

        final Optional<PassField> originField = pass.getPrimaryFields().getPassFieldForKey("from");
        final Optional<PassField> destinationField = pass.getPrimaryFields().getPassFieldForKey("to");

        String description = "WESTbahn";

        if (originField.isPresent()) {
            Tracker.get().trackEvent("quirk_fix", "description_replace", "westbahn", 1L);
            description = originField.get().value;
        }

        if (destinationField.isPresent()) {
            Tracker.get().trackEvent("quirk_fix", "description_replace", "westbahn", 2L);
            description += "->" + destinationField.get().value;
        }

        pass.setDescription(description);
    }

    private static void careForTUIFlight(PassImpl pass) {
        if (!pass.getDescription().equals("TUIfly pass")) {
            return;
        }

        Tracker.get().trackEvent("quirk_fix", "description_replace", "tuiflight", 0L);

        final Optional<PassField> originField = pass.getPrimaryFields().getPassFieldForKey("Origin");
        final Optional<PassField> destinationField = pass.getPrimaryFields().getPassFieldForKey("Des");
        final Optional<PassField> seatField = pass.getAuxiliaryFields().getPassFieldForKey("SeatNumber");

        if (originField.isPresent() && destinationField.isPresent()) {
            Tracker.get().trackEvent("quirk_fix", "description_replace", "tuiflight", 1L);
            String description = originField.get().value + "->" + destinationField.get().value;
            if (seatField.isPresent()) {
                Tracker.get().trackEvent("quirk_fix", "description_replace", "tuiflight", 2L);
                description += " @" + seatField.get().value;
            }
            pass.setDescription(description);
        }
    }


    private static void careForAirBerlin(PassImpl pass) {
        if (!pass.getDescription().equals("boardcard")) {
            return;
        }

        final String flightRegex = "\\b\\w{1,3}\\d{3,4}\\b";

        Optional<PassField> flightField = pass.getAuxiliaryFields().getPassFieldThatMatchesLabel(flightRegex);

        if (!flightField.isPresent()) {
            flightField = pass.getHeaderFields().getPassFieldThatMatchesLabel(flightRegex);
        }

        final Optional<PassField> seatField = pass.getAuxiliaryFields().getPassFieldForKey("seat");
        final Optional<PassField> boardingGroupField = pass.getSecondaryFields().getPassFieldForKey("boardingGroup");

        if (flightField.isPresent() && seatField.isPresent() && boardingGroupField.isPresent()) {
            Tracker.get().trackEvent("quirk_fix", "description_replace", "air_berlin", 0L);
            String description = flightField.get().label + " " + flightField.get().value;
            description += " | " + seatField.get().label + " " + seatField.get().value;
            description += " | " + boardingGroupField.get().label + " " + boardingGroupField.get().value;
            pass.setDescription(description);
        } // otherwise fallback to default - better save than sorry
    }
}
