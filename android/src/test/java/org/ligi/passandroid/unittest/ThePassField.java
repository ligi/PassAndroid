package org.ligi.passandroid.unittest;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.ligi.passandroid.model.AppleStylePassTranslation;
import org.ligi.passandroid.model.PassField;
import static org.assertj.core.api.Assertions.assertThat;

public class ThePassField {

    @Test
    public void returnsNullForMissingFields() throws JSONException {
        final PassField tested = new PassField(new JSONObject(), new AppleStylePassTranslation());

        assertThat(tested.key).isNull();
    }


    @Test
    public void returnsKey() throws JSONException {
        final PassField tested = new PassField(new JSONObject().put("key","bar"), new AppleStylePassTranslation());

        assertThat(tested.key).isEqualTo("bar");
    }


}
