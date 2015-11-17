package org.ligi.passandroid.model;

import org.ligi.passandroid.App;
import org.ligi.passandroid.helper.Strings;

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
        if (pass.getOrganisation() == null || !pass.getOrganisation().equals("RENFE OPERADORA")) {
            return;
        }
        App.component().tracker().trackEvent("quirk_fix", "description_replace", "RENFE OPERADORA", 0L);

        final PassField optionalDepart = pass.getPrimaryFields().getPassFieldForKey("boardingTime");
        final PassField optionalArrive = pass.getPrimaryFields().getPassFieldForKey("destino");

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "RENFE OPERADORA", 1L);
            pass.setDescription(optionalDepart.label + " -> " + optionalArrive.label);
        }
    }

    private static void careForSWISS(PassImpl pass) {
        if (pass.getOrganisation() == null || !pass.getOrganisation().equals("SWISS")) {
            return;
        }
        App.component().tracker().trackEvent("quirk_fix", "description_replace", "SWISS", 0L);

        final PassField optionalDepart = pass.getPrimaryFields().getPassFieldForKey("depart");
        final PassField optionalArrive = pass.getPrimaryFields().getPassFieldForKey("destination");

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "SWISS", 1L);
            pass.setDescription(optionalDepart.value + " -> " + optionalArrive.value);
        }
    }

    private static void careForCathayPacific(PassImpl pass) {
        if (pass.getOrganisation() == null || !pass.getOrganisation().equals("Cathay Pacific")) {

            return;
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "cathay_pacific", 0L);

        final PassField optionalDepart = pass.getPrimaryFields().getPassFieldForKey("departure");
        final PassField optionalArrive = pass.getPrimaryFields().getPassFieldForKey("arrival");

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "cathay_pacific", 1L);
            pass.setDescription(optionalDepart.label + " -> " + optionalArrive.label);
        }
    }

    private static void careForVirginAustralia(PassImpl pass) {
        // also good identifier could be  "passTypeIdentifier": "pass.com.virginaustralia.boardingpass
        if (pass.getOrganisation() == null || !pass.getOrganisation().equals("Virgin Australia")) {
            return;
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "virgin_australia", 0L);

        final PassField optionalDepart = pass.getPrimaryFields().getPassFieldForKey("origin");
        final PassField optionalArrive = pass.getPrimaryFields().getPassFieldForKey("destination");

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "virgin_australia", 1L);
            pass.setDescription(optionalDepart.label + " -> " + optionalArrive.label);
        }
    }

    private static void careForAirCanada(PassImpl pass) {
        if (pass.getOrganisation() == null || !pass.getOrganisation().equals("Air Canada")) {
            return;
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "air_canada", 0L);

        final PassField optionalDepart = pass.getPrimaryFields().getPassFieldForKey("depart");
        final PassField optionalArrive = pass.getPrimaryFields().getPassFieldForKey("arrive");

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "air_canada", 1L);
            pass.setDescription(optionalDepart.label + " -> " + optionalArrive.label);
        }
    }

    private static void careForUSAirways(PassImpl pass) {
        if (pass.getOrganisation() == null || !pass.getOrganisation().equals("US Airways")) {
            return;
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "usairways", 0L);

        final PassField optionalDepart = pass.getPrimaryFields().getPassFieldForKey("depart");
        final PassField optionalArrive = pass.getPrimaryFields().getPassFieldForKey("destination");

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "usairways", 1L);
            pass.setDescription(optionalDepart.label + " -> " + optionalArrive.label);
        }
    }

    private static void careForWestbahn(PassImpl pass) {
        if (pass.getRelevantDate() != null || !Strings.nullToEmpty(pass.getOrganisation()).equals("WESTbahn")) {
            return;
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "westbahn", 0L);

        final PassField originField = pass.getPrimaryFields().getPassFieldForKey("from");
        final PassField destinationField = pass.getPrimaryFields().getPassFieldForKey("to");

        String description = "WESTbahn";

        if (originField != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "westbahn", 1L);
            description = originField.value;
        }

        if (destinationField != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "westbahn", 2L);
            description += "->" + destinationField.value;
        }

        pass.setDescription(description);
    }

    private static void careForTUIFlight(PassImpl pass) {
        if (!pass.getDescription().equals("TUIfly pass")) {
            return;
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "tuiflight", 0L);

        final PassField originField = pass.getPrimaryFields().getPassFieldForKey("Origin");
        final PassField destinationField = pass.getPrimaryFields().getPassFieldForKey("Des");
        final PassField seatField = pass.getAuxiliaryFields().getPassFieldForKey("SeatNumber");

        if (originField != null && destinationField != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "tuiflight", 1L);
            String description = originField.value + "->" + destinationField.value;
            if (seatField != null) {
                App.component().tracker().trackEvent("quirk_fix", "description_replace", "tuiflight", 2L);
                description += " @" + seatField.value;
            }
            pass.setDescription(description);
        }
    }


    private static void careForAirBerlin(PassImpl pass) {
        if (!pass.getDescription().equals("boardcard")) {
            return;
        }

        final String flightRegex = "\\b\\w{1,3}\\d{3,4}\\b";

        PassField flightField = pass.getAuxiliaryFields().getPassFieldThatMatchesLabel(flightRegex);

        if (flightField == null) {
            flightField = pass.getHeaderFields().getPassFieldThatMatchesLabel(flightRegex);
        }
        final PassField seatField = pass.getAuxiliaryFields().getPassFieldForKey("seat");
        final PassField boardingGroupField = pass.getSecondaryFields().getPassFieldForKey("boardingGroup");

        if (flightField != null && seatField != null && boardingGroupField != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "air_berlin", 0L);
            String description = flightField.label + " " + flightField.value;
            description += " | " + seatField.label + " " + seatField.value;
            description += " | " + boardingGroupField.label + " " + boardingGroupField.value;
            pass.setDescription(description);
        } // otherwise fallback to default - better save than sorry
    }
}
