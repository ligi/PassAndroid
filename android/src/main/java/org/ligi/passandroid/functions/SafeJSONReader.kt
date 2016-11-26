package org.ligi.passandroid.functions

import org.json.JSONException
import org.json.JSONObject

/**
 * I got a really broken passes with invalid json from users.
 * As it is not possible to change the problem in the generator side
 * It has to be worked around here
 */

private val replacementMap = mapOf(

        // first we try without fixing -> always positive and try to have minimal impact
        "" to "",

        // but here the horror starts ..

        // Fix for Virgin Australia
        // "value": "NTL",}
        // a comma should never be before a closing curly brace like this ,}

        // note the \t are greetings to Empire Theatres Tickets - without it their passes do not work

        ",[\n\r\t ]*\\}" to "}",
        /*
        Entrada cine Entradas.com
            {
               "key": "passSourceUpdate",
               "label": "Actualiza tu entrada",
               "value": "http://www.entradas.com/entradas/passbook.do?cutout
            },
        ],
        */
        ",[\n\r\t ]*\\]" to "]",

        /*
        forgotten value aka ( also Entradas.com):
        "locations": [
        {
        "latitude": ,
        "longitude": ,
        "relevantText": "Bienvenido a yelmo cines espacio coruña"
        }
        */

        ":[ ]*,[\n\r\t ]*\"" to ":\"\",",

        /*
        from RENFE OPERADORA Billete de tren

        ],
        "transitType": "PKTransitTypeTrain"
        },
        ,
        "relevantDate": "2013-08-10T19:15+02:00"
        */

        ",[\n\r\t ]*," to ","
)

@Throws(JSONException::class)
fun readJSONSafely(str: String?): JSONObject? {
    if (str == null) {
        return null
    }
    var allReplaced: String = str

    // first try with single fixes
    for ((key, value) in replacementMap) {
        try {
            allReplaced = allReplaced.replace(key.toRegex(), value)
            return JSONObject(str.replace(key.toRegex(), value))
        } catch (e: JSONException) {
            // expected because of problems in JSON we are trying to fix here
        }

    }

    // if that did not work do combination of all - if this is not working a JSONException is thrown
    return JSONObject(allReplaced)
}
