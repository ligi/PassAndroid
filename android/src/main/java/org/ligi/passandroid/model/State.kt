package org.ligi.passandroid.model

import com.chibatching.kotpref.KotprefModel

object State : KotprefModel() {

    var lastSelectedTab: Int by intPref()
    var lastSelectedPassUUID: String by stringPref()

}
