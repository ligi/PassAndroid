package org.ligi.passandroid.reader

import org.json.JSONObject


fun JSONObject.getBarcodeJson(): JSONObject? {
    if (has("barcode")) {
        return getJSONObject("barcode")
    }

    if (has("barcodes")) {

        getJSONArray("barcodes").let {
            if (length() > 0) {
                return it.getJSONObject(0)
            }
        }

    }

    return null
}

