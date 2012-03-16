package org.nick.wwwjdic.utils;

import org.nick.wwwjdic.R;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

public class UIUtils {

    public static boolean isHoneycomb() {
        // Can use static final constants like HONEYCOMB, declared in later
        // versions
        // of the OS since they are inlined at compile time. This is guaranteed
        // behavior.
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

    public static int getListActivatedResource(Context ctx) {
        int resource = R.drawable.list_activated_holo;
        // null if used in layout?
        if (ctx == null) {
            return resource;
        }
        // XXX
        //        if (isHoneycomb()) {
        //            TypedArray a = ctx
        //                    .obtainStyledAttributes(new int[] { android.R.attr.activatedBackgroundIndicator });
        //            resource = a.getResourceId(0, 0);
        //            a.recycle();
        //        }

        return resource;
    }
}
