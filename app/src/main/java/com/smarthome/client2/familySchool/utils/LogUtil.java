package com.smarthome.client2.familySchool.utils;

import android.util.Log;

public class LogUtil {
	
	public static boolean isDubug = false;
	
	public static void d(String tag, String info){
		if (isDubug) {
			Log.d(tag, info);
		}
	}
	
	public static void i(String tag, String info){
		if (isDubug) {
			Log.i(tag, info);
		}
	}
	
	public static void e(String tag, String info){
		if (isDubug) {
			Log.e(tag, info);
		}
	}
	
}
