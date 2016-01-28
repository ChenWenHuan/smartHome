package com.smarthome.client2.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkManager {
	
	private Context c;

	static private NetWorkManager instance = null;

	static public NetWorkManager getInstance(Context c) {
		if (instance == null) {
			instance = new NetWorkManager();
			instance.c = c.getApplicationContext();
		}
		return instance;
	}
	
	public boolean isNetWorkEnable() {
		ConnectivityManager manager = (ConnectivityManager) c.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo networkinfo = manager.getActiveNetworkInfo();
			if ((networkinfo != null && networkinfo.isAvailable())) {
				return true;
			}
		}
		return false;
	}
}
