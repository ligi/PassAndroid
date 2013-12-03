package org.ligi.ticketviewer.helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * I got a really broken pass with invalid json from a user. The source was Virgin Australia
 * the bad part looked like this:
 * <p/>
 * "value": "NTL",}
 * <p/>
 * As it is not possible to change the problem in the generator side
 */
public class SafeJSONReader {

    private static Map<String, String> replacementMap = new LinkedHashMap() {{
        // first we try without fixing -> always positive and try to have minimal impact
        put("", "");

        // but here the horror starts ..

        // Fix for Virgin Australia
        // "value": "NTL",}
        // a comma should never be before a closing curly brace like this ,}

        // note the \t are greetings to Empire Theatres Tickets - without it their passes do not work

        put(",[\n\r\t ]*\\}", "}");

        /*
        Entrada cine Entradas.com
             {
                "key": "passSourceUpdate",
                "label": "Actualiza tu entrada",
                "value": "http://www.entradas.com/entradas/passbook.do?cutout
            },

        ],
        */
        put(",[\n\r\t ]*\\]", "]");

        /*
        forgotten value aka ( also Entradas.com):
        "locations": [
        {
            "latitude": ,
            "longitude": ,
            "relevantText": "Bienvenido a yelmo cines espacio coruña"
        }
        */
        put(":[ ]*,[\n\r\t ]*\"", ":\"\",");

        /*
        from RENFE OPERADORA Billete de tren

        ],
        "transitType": "PKTransitTypeTrain"
        },
        ,
        "relevantDate": "2013-08-10T19:15+02:00"
         */

        put(",[\n\r\t ]*,", ",");

    }};

    public static JSONObject readJSONSafely(String str) throws JSONException {
        if (str == null) {
            return null;
        }
        String allReplaced = str;

        // first try with single fixes
        for (Map.Entry<String, String> replacementPair : replacementMap.entrySet()) {
            try {
                allReplaced = allReplaced.replaceAll(replacementPair.getKey(), replacementPair.getValue());
                return new JSONObject(str.replaceAll(replacementPair.getKey(), replacementPair.getValue()));
            } catch (JSONException e) {
                // expected because of problems in JSON we are trying to fix here
            }
        }

        // if that did not work do combination of all - if this is not working a JSONException is thrown
        return new JSONObject(allReplaced);
    }
}
