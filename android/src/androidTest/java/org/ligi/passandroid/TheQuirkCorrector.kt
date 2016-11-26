package org.ligi.passandroid

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.passandroid.functions.loadPassFromAsset

class TheQuirkCorrector {

    @Test
    fun testWestbahnDescriptionIsFixed() {
        loadPassFromAsset("passes/workarounds/westbahn/special.pkpass") {
            assertThat(it!!.description).isEqualTo("Wien Westbahnhof->Amstetten")
        }
    }

}
