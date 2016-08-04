package org.ligi.passandroid

import android.support.test.runner.AndroidJUnit4
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TheQuirkCorrector : TheAppleStyleBarcodeReaderBase() {

    @Test
    fun testWestbahnDescriptionIsFixed() {
        loadPassFromAsset("passes/workarounds/westbahn/special.pkpass") {
            assertThat(it.description).isEqualTo("Wien Westbahnhof->Amstetten")
        }
    }

}
