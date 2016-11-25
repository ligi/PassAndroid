package org.ligi.passandroid.unittest

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.passandroid.model.AppleStylePassTranslation

class TheAppleStylePassTranslation {

    @Test
    fun testThatNullTranslationWorks() {
        val tested = AppleStylePassTranslation()
        tested.loadFromString("")
        assertThat(tested.translate(null)).isNull()
    }

    @Test
    fun testThatBasicParsingWorks() {
        val tested = AppleStylePassTranslation()
        tested.loadFromString("\"foo\"=\"bar\";")
        assertThat(tested.translate("foo")).isEqualTo("bar")
    }
}
