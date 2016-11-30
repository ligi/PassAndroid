package org.ligi.passandroid.unittest

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.passandroid.ui.edit.getRandomEAN13
import org.ligi.passandroid.ui.edit.isValidEAN13

class TheEANHelper {

    @Test
    fun randomEAN13HasCorrectLength() {
        assertThat(getRandomEAN13().length).isEqualTo(13)
    }

    @Test
    fun acceptGoodEAN13() {
        assertThat(isValidEAN13("6416016588755")).isTrue()
    }

    @Test
    fun rejectBadEAN13() {
        assertThat(isValidEAN13("foo")).isFalse()
    }
}
