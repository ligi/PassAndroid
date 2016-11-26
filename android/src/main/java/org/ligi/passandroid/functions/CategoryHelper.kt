package org.ligi.passandroid.functions

import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.PassType

@StringRes
fun getHumanCategoryString(fromPass: PassType) = when (fromPass) {
    PassType.BOARDING -> R.string.boarding_pass
    PassType.EVENT -> R.string.category_event
    PassType.COUPON -> R.string.category_coupon
    PassType.LOYALTY -> R.string.category_storecard
    PassType.GENERIC -> R.string.category_generic
    PassType.VOUCHER -> R.string.categories_voucher

    else -> R.string.category_none
}


@ColorInt
fun getCategoryDefaultBG(category: PassType) = when (category) {
    PassType.BOARDING -> 0xFF3d73e9
    PassType.EVENT -> 0xFF9f3dd0
    PassType.COUPON -> 0xFF9ccb05
    PassType.LOYALTY -> 0xFFf29b21
    PassType.VOUCHER -> 0xFF2A2727
    PassType.GENERIC -> 0xFFea3c48

    else -> Color.WHITE.toLong()
}.toInt()


@DrawableRes
fun getCategoryTopImageRes(type: PassType) = when (type) {
    PassType.BOARDING -> R.drawable.cat_bp
    PassType.EVENT -> R.drawable.cat_et
    PassType.COUPON -> R.drawable.cat_cp
    PassType.LOYALTY -> R.drawable.cat_sc
    PassType.VOUCHER -> R.drawable.cat_ps
    PassType.GENERIC -> R.drawable.cat_none

    else -> R.drawable.cat_none
}
