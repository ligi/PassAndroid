package org.ligi.passandroid;


import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import java.util.HashSet;
import java.util.Set;
import org.ligi.passandroid.helper.CategoryHelper;
import org.ligi.passandroid.model.PassDefinitions;
import org.ligi.passandroid.model.pass.PassType;
import static org.assertj.core.api.Assertions.assertThat;

public class TheCategoryHelper extends InstrumentationTestCase {

    @SmallTest
    public void testAllCategoriesAreTranslated() {

        final Set<Integer> probe = new HashSet<>();

        for (PassType type : PassDefinitions.INSTANCE.getTYPES().keySet()) {
            probe.add(CategoryHelper.getHumanCategoryString(type));
        }

        assertThat(probe.size()).isEqualTo(PassDefinitions.INSTANCE.getTYPES().keySet().size());
    }

}
