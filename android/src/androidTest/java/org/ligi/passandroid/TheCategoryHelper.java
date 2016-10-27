package org.ligi.passandroid;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.PassDefinitions;
import org.ligi.passandroid.model.pass.PassType;
import static org.assertj.core.api.Assertions.assertThat;

public class TheCategoryHelper {

    @Test
    public void testAllCategoriesAreTranslated() {

        final Set<Integer> probe = new HashSet<>();

        for (PassType type : PassDefinitions.INSTANCE.getTYPE_TO_NAME().keySet()) {
            probe.add(CategoryHelper.INSTANCE.getHumanCategoryString(type));
        }

        assertThat(probe.size()).isEqualTo(PassDefinitions.INSTANCE.getTYPE_TO_NAME().keySet().size());
    }

}
