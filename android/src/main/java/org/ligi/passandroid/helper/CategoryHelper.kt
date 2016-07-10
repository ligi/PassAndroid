package org.ligi.passandroid.helper

import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.PassType

object CategoryHelper {

    @StringRes
    fun getHumanCategoryString(fromPass: PassType): Int {
        when (fromPass) {
            PassType.BOARDING -> return R.string.boarding_pass
            PassType.EVENT -> return R.string.category_event
            PassType.COUPON -> return R.string.category_coupon
            PassType.LOYALTY -> return R.string.category_storecard
            PassType.GENERIC -> return R.string.category_generic
            PassType.VOUCHER -> return R.string.categories_voucher

            else -> return R.string.category_none
        }

    }

    @ColorInt
    fun getCategoryDefaultBG(category: PassType): Int {
        when (category) {
            PassType.BOARDING -> return 0xFF3d73e9.toInt()
            PassType.EVENT -> return 0xFF9f3dd0.toInt()
            PassType.COUPON -> return 0xFF9ccb05.toInt()
            PassType.LOYALTY -> return 0xFFf29b21.toInt()
            PassType.VOUCHER -> return 0xFF2A2727.toInt()
            PassType.GENERIC -> return 0xFFea3c48.toInt()

            else -> return Color.WHITE
        }

    }

    @DrawableRes
    fun getCategoryTopImageRes(type: PassType): Int {
        when (type) {
            PassType.BOARDING -> return R.drawable.cat_bp
            PassType.EVENT -> return R.drawable.cat_et
            PassType.COUPON -> return R.drawable.cat_cp
            PassType.LOYALTY -> return R.drawable.cat_sc
            PassType.VOUCHER -> return R.drawable.cat_ps
            PassType.GENERIC -> return R.drawable.cat_none

            else -> return R.drawable.cat_none
        }
    }

}
