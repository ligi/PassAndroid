package org.ligi.passandroid.unittest;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Test;
import org.ligi.passandroid.ui.edit.EANHelper;
import static org.assertj.core.api.Assertions.assertThat;

public class TheEANHelper {

    @Test
    public void randomEAN13HasCorrectLength() throws URISyntaxException, IOException {
        assertThat(EANHelper.INSTANCE.getRandomEAN13().length()).isEqualTo(13);
    }

    @Test
    public void acceptGoodEAN13() throws URISyntaxException, IOException {
        assertThat(EANHelper.INSTANCE.isValidEAN13("6416016588755")).isTrue();
    }

    @Test
    public void rejectBadEAN13() throws URISyntaxException, IOException {
        assertThat(EANHelper.INSTANCE.isValidEAN13("foo")).isFalse();
    }
}
