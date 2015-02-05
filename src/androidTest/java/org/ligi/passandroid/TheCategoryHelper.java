package org.ligi.passandroid;


import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.Pass;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TheCategoryHelper extends InstrumentationTestCase {

    @SmallTest
    public void testAllCategoriesAreTranslated() {

        final Set<Integer> probe = new HashSet<>();

        for (String type : Pass.TYPES) {
            probe.add(CategoryHelper.getHumanCategoryString(type));
        }

        assertThat(probe.size()).isEqualTo(Pass.TYPES.length);
    }

}
