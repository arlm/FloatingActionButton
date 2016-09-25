package com.github.clans.fab;

import android.content.Context;
import android.os.Build;

final class Util {

    private Util() {
    }

    static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    static int getScreenWidth(Context context) {
        final int width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }

    static int getExtendedButtonLandscapeWidth(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.extended_button_width) - dpToPx(context, 5f);
    }

    static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
