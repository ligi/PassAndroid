package org.ligi.passandroid;


import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.Pass;

import java.util.HashSet;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

public class TheCategoryHelper extends BaseTest {

    @SmallTest
    public void test_all_categories_should_b_translated() {

        final Set<Integer> probe = new HashSet<>();

        for (String type : Pass.TYPES) {
            probe.add(CategoryHelper.getHumanCategoryString(type));
        }

        assertThat(probe.size()).isEqualTo(Pass.TYPES.length);
    }

}
