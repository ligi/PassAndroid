package org.ligi.passandroid.helper;

import android.graphics.Color;

import org.ligi.passandroid.R;

public class CategoryHelper {

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
                return Color.BLUE;

            case "eventTicket":
                return Color.YELLOW;

            case "coupon":
                return Color.GREEN;

            case "storeCard":
                return Color.WHITE;

            case "generic":
                return Color.DKGRAY;

            default:
                return Color.BLACK;
        }

    }

    public final static int getCategoryDefaultFG(String category) {
        switch (category) {
            case "boardingPass":
                return Color.WHITE;

            case "eventTicket":
                return Color.BLACK;

            case "coupon":
                return Color.BLACK;

            case "storeCard":
                return Color.BLACK;

            case "generic":
                return Color.WHITE;

            default:
                return Color.WHITE;
        }

    }


    public final static int getCategoryTopImageRes(String fromPass) {
        switch (fromPass) {
            case "boardingPass":
                return R.drawable.category_boarding;

            case "eventTicket":
                return R.drawable.category_event;

            case "coupon":
                return R.drawable.category_coupon;

            case "storeCard":
                return R.drawable.category_store;

            case "generic":
            default:
                return R.drawable.category_generic;

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
