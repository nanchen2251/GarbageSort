package com.hongmei.garbagesort.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class HoneyCombMR2V13Compat {

    private static class BaseImpl {

        @SuppressWarnings("deprecation")
        public void getDisplaySize(Display display, Point outSize) {
            outSize.x = display.getWidth();
            outSize.y = display.getHeight();
        }
    }

    private static class HoneyCombMR2Impl extends BaseImpl {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        public void getDisplaySize(Display display, Point outSize) {
            display.getSize(outSize);
        }
    }

    static final BaseImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            IMPL = new HoneyCombMR2Impl();
        } else {
            IMPL = new BaseImpl();
        }
    }

    public static void getDisplaySize(Display display, Point outSize) {
        IMPL.getDisplaySize(display, outSize);
    }

    public static int getDisplayWidth(Display display) {
        Point outSize = new Point();
        getDisplaySize(display, outSize);
        return outSize.x;
    }

    public static void getDisplaySize(Context context, Point outSize) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        getDisplaySize(wm.getDefaultDisplay(), outSize);
    }
}
