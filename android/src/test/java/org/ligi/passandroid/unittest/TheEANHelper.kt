package org.ligi.passandroid.unittest

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.passandroid.ui.edit.EANHelper

class TheEANHelper {

    @Test
    fun randomEAN13HasCorrectLength() {
        assertThat(EANHelper.getRandomEAN13().length).isEqualTo(13)
    }

    @Test
    fun acceptGoodEAN13() {
        assertThat(EANHelper.isValidEAN13("6416016588755")).isTrue()
    }

    @Test
    fun rejectBadEAN13() {
        assertThat(EANHelper.isValidEAN13("foo")).isFalse()
    }
}
