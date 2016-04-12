package org.ligi.passandroid.helper;

import android.graphics.Color;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.pass.PassType;

public class CategoryHelper {

    public static int getHumanCategoryString(PassType fromPass) {
        switch (fromPass) {
            case BOARDING:
                return R.string.boarding_pass;

            case EVENT:
                return R.string.category_event;

            case COUPON:
                return R.string.category_coupon;

            case LOYALTY:
                return R.string.category_storecard;

            case GENERIC:
                return R.string.category_generic;

            case VOUCHER:
                return R.string.categories_voucher;

            default:
                return R.string.category_none;
        }

    }

    public static int getCategoryDefaultBG(PassType category) {
        switch (category) {
            case BOARDING:
                return 0xFF3d73e9;

            case EVENT:
                return 0xFF9f3dd0;

            case COUPON:
                return 0xFF9ccb05;

            case LOYALTY:
                return 0xFFf29b21;

            case VOUCHER:
                return 0xFF2A2727;

            case GENERIC:
                return 0xFFea3c48;

            default:
                return Color.WHITE;
        }

    }

    public static int getCategoryTopImageRes(PassType type) {
        switch (type) {
            case BOARDING:
                return R.drawable.cat_bp;

            case EVENT:
                return R.drawable.cat_et;

            case COUPON:
                return R.drawable.cat_cp;

            case LOYALTY:
                return R.drawable.cat_sc;

            case VOUCHER:
                return R.drawable.cat_ps;

            case GENERIC:
            default:
                return R.drawable.cat_none;

        }
    }

}
