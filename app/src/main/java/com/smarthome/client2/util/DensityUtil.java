package com.smarthome.client2.util;

import android.content.Context;

public class DensityUtil {

	 /** 
     * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    /** 
     * ��pxֵת��Ϊspֵ����֤���ִ�С���� 
     *  
     * @param pxValue 
     * @param fontScale 
     *            ��DisplayMetrics��������scaledDensity�� 
     * @return 
     */  
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
  
    /** 
     * ��spֵת��Ϊpxֵ����֤���ִ�С���� 
     *  
     * @param spValue 
     * @param fontScale 
     *            ��DisplayMetrics��������scaledDensity�� 
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  
}  
