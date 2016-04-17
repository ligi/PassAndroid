package org.ligi.passandroid.model

import com.chibatching.kotpref.KotprefModel

class State : KotprefModel() {

    var lastSelectedTab: Int by intPrefVar()
}
