package org.ligi.passandroid.model

import org.ligi.passandroid.model.pass.PassType.*

object PassDefinitions {

    val TYPE_TO_NAME = mapOf(COUPON to "coupon",
            EVENT to "eventTicket",
            BOARDING to "boardingPass",
            GENERIC to "generic",
            LOYALTY to "storeCard")

    val NAME_TO_TYPE = TYPE_TO_NAME.entries.associate { it.value to it.key }

}
