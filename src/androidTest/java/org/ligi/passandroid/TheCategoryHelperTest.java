package org.ligi.passandroid;


import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.Passbook;

import java.util.HashSet;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

public class TheCategoryHelperTest extends ActivityInstrumentationTestCase2<Activity> {


    public TheCategoryHelperTest() {
        super(Activity.class);
    }

    @SmallTest
    public void test_all_categories_should_b_translated() {

        Set<Integer> probe = new HashSet<>();

        for (String type : Passbook.TYPES) {
            probe.add(CategoryHelper.getHumanCategoryString(type));
        }

        assertThat(probe.size()).isEqualTo(Passbook.TYPES.length);
    }

}
