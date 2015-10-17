package org.ligi.passandroid.unittest;

import org.junit.Test;
import org.ligi.passandroid.model.AppleStylePassTranslation;

import static org.assertj.core.api.Assertions.assertThat;

public class TheAppleStylePassTranslation {

    @Test
    public void testThatNullTranslationWorks() {
        final AppleStylePassTranslation tested = new AppleStylePassTranslation();
        tested.loadFromString("");
        assertThat(tested.translate(null)).isNull();
    }

    @Test
    public void testThatBasicParsingWorks() {
        final AppleStylePassTranslation tested = new AppleStylePassTranslation();
        tested.loadFromString("\"foo\"=\"bar\";");
        assertThat(tested.translate("foo")).isEqualTo("bar");
    }
}
