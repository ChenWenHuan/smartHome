package com.smarthome.client2.common;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.smarthome.client2.config.Preferences;

/**
 * 初始化全局参数
 *
 */
public class GloableData {
	
	private static int screenW;
	private static int screenH;
	private static float density;
	
	public static void initScreen(Activity act) {
		WindowManager windowManager = act.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		GloableData.screenW = display.getWidth();
		GloableData.screenH = display.getHeight();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		GloableData.density = ((float) dm.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
		TLog.Log(GloableData.screenW + "/" + GloableData.screenH + "/" + GloableData.density);
		Preferences.getInstance(act).setScreenH(screenH);
		Preferences.getInstance(act).setScreenW(screenW);
		Preferences.getInstance(act).setScreenDen(density);
	}

	public static int getScreenW(Context act) {
		if (GloableData.screenW == 0 || GloableData.screenH == 0 || GloableData.density == 0) {
			GloableData.screenH = Preferences.getInstance(act).getScreenH();
			GloableData.screenW = Preferences.getInstance(act).getScreenW();
			GloableData.density = Preferences.getInstance(act).getScreenDen();
		}
		return screenW;

	}

	public static int getScreenH(Context act) {
		if (GloableData.screenW == 0 || GloableData.screenH == 0 || GloableData.density == 0) {
			GloableData.screenH = Preferences.getInstance(act).getScreenH();
			GloableData.screenW = Preferences.getInstance(act).getScreenW();
			GloableData.density = Preferences.getInstance(act).getScreenDen();
		}
		return screenH;
	}

	public static float getScreenDensity(Context act) {
		if (GloableData.screenW == 0 || GloableData.screenH == 0 || GloableData.density == 0) {
			GloableData.screenH = Preferences.getInstance(act).getScreenH();
			GloableData.screenW = Preferences.getInstance(act).getScreenW();
			GloableData.density = Preferences.getInstance(act).getScreenDen();
		}
		return density;
	}


	/***
	 * 初始化iemi和imsi的方法
	 */
	public static void initIMSIIMEI(Context ctx) {
		TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = Preferences.getInstance(ctx).getUserIMEI();
		if (imei == null || imei.equals("") || imei.length() != 15) {
			imei = telephonyManager.getDeviceId();
			if (imei == null || imei.equals("")) {
				imei = "01" + System.currentTimeMillis();
				// 自定义 imei
			}
		}
		String imsi = telephonyManager.getSubscriberId();
		if (imsi == null || imsi.equals("")) {
			imsi = imei;
		}
		Preferences.getInstance(ctx).setPhoneIMEIAndIMSI(imei, imsi);
	}
}
