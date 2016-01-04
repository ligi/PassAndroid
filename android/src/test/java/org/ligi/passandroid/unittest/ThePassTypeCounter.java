package org.ligi.passandroid.unittest;

import org.json.JSONException;
import org.junit.Test;
import org.ligi.passandroid.helper.PassTypeCounter;
import org.ligi.passandroid.model.CountedType;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.PassImpl;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ThePassTypeCounter {

    @Test
    public void shouldReturn0ForEmptyPassList() throws JSONException {
        final Set<CountedType> count = PassTypeCounter.count(new ArrayList<FiledPass>());

        assertThat(count.size()).isEqualTo(0);
    }


    @Test
    public void shouldBeAbleToCount() throws JSONException {

        final Set<CountedType> count = PassTypeCounter.count(new ArrayList<FiledPass>() {
            {
                add(getPassWithType("foo"));
                add(getPassWithType("bar"));
                add(getPassWithType("bar"));
            }
        });

        assertThat(count).containsOnly(new CountedType("foo",1),new CountedType("bar",2));
    }

    private FiledPass getPassWithType(final String type) {
        final PassImpl pass = new PassImpl();
        pass.setType(type);
        return pass;
    }

}
