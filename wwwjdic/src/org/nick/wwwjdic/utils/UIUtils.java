package org.nick.wwwjdic.utils;

import org.nick.wwwjdic.R;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class UIUtils {

    public static boolean isHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isHoneycombTablet(Context context) {
        return isHoneycomb() && isTablet(context);
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isIcs() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static Drawable getListActivatedDrawable(Context ctx) {
        Drawable result = ctx.getResources().getDrawable(
                R.drawable.list_activated_holo);
        if (isHoneycomb()) {
            TypedArray a = ctx
                    .obtainStyledAttributes(new int[] { android.R.attr.activatedBackgroundIndicator });
            int resource = a.getResourceId(0, 0);
            a.recycle();

            result = ctx.getResources().getDrawable(resource);
            result.setState(new int[] { android.R.attr.state_activated });
        }


        return result;
    }
}
