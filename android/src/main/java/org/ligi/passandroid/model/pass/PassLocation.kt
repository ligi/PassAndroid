package org.ligi.passandroid.model.pass

class PassLocation {

    var name: String? = null

    var lat: Double = 0.toDouble()
    var lon: Double = 0.toDouble()

    fun getNameWithFallback(pass: Pass): String? {
        if (name == null || name!!.isEmpty()) {
            // fallback for passes with locations without description - e.g. AirBerlin
            return pass.description
        }
        return name
    }

}
