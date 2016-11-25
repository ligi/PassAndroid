package org.ligi.passandroid

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.passandroid.helper.CategoryHelper
import org.ligi.passandroid.model.PassDefinitions

class TheCategoryHelper {

    @Test
    fun testAllCategoriesAreTranslated() {

        val allTranslationSet = PassDefinitions.TYPE_TO_NAME.keys
                .map { CategoryHelper.getHumanCategoryString(it) }
                .toSet()

        assertThat(allTranslationSet.size).isEqualTo(PassDefinitions.TYPE_TO_NAME.keys.size)
    }

}
