package org.ligi.passandroid.model

import org.ligi.passandroid.model.pass.PassType
import org.ligi.passandroid.model.pass.PassType.*

object PassDefinitions {

    val TYPES = mapOf(COUPON to "coupon",
            EVENT to "eventTicket",
            BOARDING to "boardingPass",
            GENERIC to "generic",
            LOYALTY to "storeCard")

    fun getTypeForString(str:String) : PassType {
        TYPES.forEach {
            if (it.value.equals(str)) {
                return it.key
            }
        }

        return GENERIC
    }

}
