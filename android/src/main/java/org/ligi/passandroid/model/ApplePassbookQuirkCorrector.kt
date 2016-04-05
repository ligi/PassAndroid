package org.ligi.passandroid.model

import org.ligi.passandroid.App
import org.ligi.passandroid.helper.Strings
import org.ligi.passandroid.model.pass.PassField
import org.ligi.passandroid.model.pass.PassImpl

object ApplePassbookQuirkCorrector {

    fun correctQuirks(pass: PassImpl) {
        careForTUIFlight(pass)
        careForAirBerlin(pass)
        careForWestbahn(pass)
        careForAirCanada(pass)
        careForUSAirways(pass)
        careForVirginAustralia(pass)
        careForCathayPacific(pass)
        careForSWISS(pass)
        careForRenfe(pass)
    }

    fun getPassFieldForKey(pass: PassImpl, key: String): PassField? {
        return pass.fields.firstOrNull() { it.key != null && it.key == key }
    }

    fun getPassFieldThatMatchesLabel(pass: PassImpl, matcher: String): PassField? {
        return pass.fields.firstOrNull() {
            val label = it.label
            label != null && label.matches(matcher.toRegex())
        }
    }

    private fun careForRenfe(pass: PassImpl) {
        if (pass.creator == null || pass.creator != "RENFE OPERADORA") {
            return
        }
        App.component().tracker().trackEvent("quirk_fix", "description_replace", "RENFE OPERADORA", 0L)

        val optionalDepart = getPassFieldForKey(pass, "boardingTime")
        val optionalArrive = getPassFieldForKey(pass, "destino")

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "RENFE OPERADORA", 1L)
            pass.description = optionalDepart.label + " -> " + optionalArrive.label
        }
    }

    private fun careForSWISS(pass: PassImpl) {
        if (pass.creator == null || pass.creator != "SWISS") {
            return
        }
        App.component().tracker().trackEvent("quirk_fix", "description_replace", "SWISS", 0L)

        val optionalDepart = getPassFieldForKey(pass, "depart")
        val optionalArrive = getPassFieldForKey(pass, "destination")

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "SWISS", 1L)
            pass.description = optionalDepart.value + " -> " + optionalArrive.value
        }
    }

    private fun careForCathayPacific(pass: PassImpl) {
        if (pass.creator == null || pass.creator != "Cathay Pacific") {

            return
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "cathay_pacific", 0L)

        val optionalDepart = getPassFieldForKey(pass, "departure")
        val optionalArrive = getPassFieldForKey(pass, "arrival")

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "cathay_pacific", 1L)
            pass.description = optionalDepart.label + " -> " + optionalArrive.label
        }
    }

    private fun careForVirginAustralia(pass: PassImpl) {
        // also good identifier could be  "passTypeIdentifier": "pass.com.virginaustralia.boardingpass
        if (pass.creator == null || pass.creator != "Virgin Australia") {
            return
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "virgin_australia", 0L)

        val optionalDepart = getPassFieldForKey(pass,"origin")
        val optionalArrive = getPassFieldForKey(pass,"destination")

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "virgin_australia", 1L)
            pass.description = optionalDepart.label + " -> " + optionalArrive.label
        }
    }

    private fun careForAirCanada(pass: PassImpl) {
        if (pass.creator == null || pass.creator != "Air Canada") {
            return
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "air_canada", 0L)

        val optionalDepart = getPassFieldForKey(pass,"depart")
        val optionalArrive = getPassFieldForKey(pass,"arrive")

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "air_canada", 1L)
            pass.description = optionalDepart.label + " -> " + optionalArrive.label
        }
    }

    private fun careForUSAirways(pass: PassImpl) {
        if (pass.creator == null || pass.creator != "US Airways") {
            return
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "usairways", 0L)

        val optionalDepart = getPassFieldForKey(pass,"depart")
        val optionalArrive = getPassFieldForKey(pass,"destination")

        if (optionalDepart != null && optionalArrive != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "usairways", 1L)
            pass.description = optionalDepart.label + " -> " + optionalArrive.label
        }
    }

    private fun careForWestbahn(pass: PassImpl) {
        if (pass.calendarTimespan != null || Strings.nullToEmpty(pass.creator) != "WESTbahn") {
            return
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "westbahn", 0L)

        val originField = getPassFieldForKey(pass, "from")
        val destinationField = getPassFieldForKey(pass, "to")

        var description = "WESTbahn"

        if (originField != null ) {
            val value = originField.value
            if (value != null) {
                App.component().tracker().trackEvent("quirk_fix", "description_replace", "westbahn", 1L)
                description = value
            }
        }

        if (destinationField != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "westbahn", 2L)
            description += "->" + destinationField.value!!
        }

        pass.description = description
    }

    private fun careForTUIFlight(pass: PassImpl) {
        if (pass.description != "TUIfly pass") {
            return
        }

        App.component().tracker().trackEvent("quirk_fix", "description_replace", "tuiflight", 0L)

        val originField = getPassFieldForKey(pass,"Origin")
        val destinationField = getPassFieldForKey(pass,"Des")
        val seatField = getPassFieldForKey(pass,"SeatNumber")

        if (originField != null && destinationField != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "tuiflight", 1L)
            var description = originField.value + "->" + destinationField.value
            if (seatField != null) {
                App.component().tracker().trackEvent("quirk_fix", "description_replace", "tuiflight", 2L)
                description += " @" + seatField.value!!
            }
            pass.description = description
        }
    }


    private fun careForAirBerlin(pass: PassImpl) {
        if (pass.description != "boardcard") {
            return
        }

        val flightRegex = "\\b\\w{1,3}\\d{3,4}\\b"

        var flightField = getPassFieldThatMatchesLabel(pass,flightRegex)

        if (flightField == null) {
            flightField = getPassFieldThatMatchesLabel(pass,flightRegex)
        }
        val seatField = getPassFieldForKey(pass,"seat")
        val boardingGroupField = getPassFieldForKey(pass,"boardingGroup")

        if (flightField != null && seatField != null && boardingGroupField != null) {
            App.component().tracker().trackEvent("quirk_fix", "description_replace", "air_berlin", 0L)
            var description = flightField.label + " " + flightField.value
            description += " | " + seatField.label + " " + seatField.value
            description += " | " + boardingGroupField.label + " " + boardingGroupField.value
            pass.description = description
        } // otherwise fallback to default - better save than sorry
    }
}
