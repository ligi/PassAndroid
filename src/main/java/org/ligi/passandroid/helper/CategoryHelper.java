package org.ligi.passandroid.helper;

import android.graphics.Color;

import org.ligi.passandroid.R;

public class CategoryHelper {

    public final static String[] ALL_CATEGORIES = {"boardingPass", "eventTicket", "coupon", "storeCard", "generic"};

    public final static int getHumanCategoryString(String fromPass) {
        switch (fromPass) {
            case "boardingPass":
                return R.string.boarding_pass;

            case "eventTicket":
                return R.string.category_event;

            case "coupon":
                return R.string.category_coupon;

            case "storeCard":
                return R.string.category_storecard;

            case "generic":
                return R.string.category_generic;

            default:
                return R.string.category_none;
        }

    }

    public final static int getCategoryDefaultBG(String category) {
        switch (category) {
            case "boardingPass":
                return 0xFF3d73e9;

            case "eventTicket":
                return 0xFF9f3dd0;

            case "coupon":
                return 0xFF9ccb05;

            case "storeCard":
                return 0xFFf29b21;

            case "generic":
                return 0xFFea3c48;

            default:
                return Color.WHITE;
        }

    }

    public final static int getCategoryDefaultFG(String category) {
        switch (category) {
            case "boardingPass":
                return Color.BLACK;

            case "eventTicket":
                return Color.BLACK;

            case "coupon":
                return Color.BLACK;

            case "storeCard":
                return Color.BLACK;

            case "generic":
                return Color.BLACK;

            default:
                return Color.BLACK;
        }

    }


    public final static int getCategoryTopImageRes(String fromPass) {
        switch (fromPass) {
            case "boardingPass":
                return R.drawable.cat_bp;

            case "eventTicket":
                return R.drawable.cat_et;

            case "coupon":
                return R.drawable.cat_cp;

            case "storeCard":
                return R.drawable.cat_sc;

            case "generic":
            default:
                return R.drawable.cat_none;

        }
    }


    public final static String getCategoryShortStr(String fromPass) {
        switch (fromPass) {
            case "boardingPass":
                return "BP";

            case "eventTicket":
                return "T";

            case "coupon":
                return "%";

            case "storeCard":
                return "SC";

            case "generic":
            default:
                return "*";

        }
    }


}
