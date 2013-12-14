package org.ligi;

import org.junit.Test;
import org.ligi.passandroid.model.*;

import java.util.HashSet;
import java.util.Set;
import org.ligi.passandroid.helper.CategoryHelper;

public class TheCategoryHelper {

    @Test
    public void all_categories_should_b_translated() {

        Set<Integer> probe = new HashSet<>();

        for (String type : Passbook.TYPES) {
            probe.add(CategoryHelper.getHumanCategoryString(type));
        }

        assert (probe.size() == Passbook.TYPES.length);
        // TODO replace with assertThat - just IDE fuckup with testing and so many flavors atm
    }
}