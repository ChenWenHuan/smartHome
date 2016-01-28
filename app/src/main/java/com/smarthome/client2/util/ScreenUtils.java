package com.smarthome.client2.util;



import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Dacer on 10/8/13.
 */
public class ScreenUtils {

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    
    public static int getScreenWidth(Context context)
    {
    	WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    	return wm.getDefaultDisplay().getWidth();
    }
    
    public static int getScreenHeight(Context context)
    {
    	WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    	return wm.getDefaultDisplay().getHeight();
    }
    
}
