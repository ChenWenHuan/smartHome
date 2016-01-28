package com.smarthome.client2.util;

import com.baidu.mapapi.model.LatLng;

public class LocationUtil {
	
	private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;  
	
	public static LatLng GaodeToBaidu(double gg_lon, double gg_lat ){		
		
		double x = gg_lon, y = gg_lat;
	    double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
	    double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
	    double bd_lon = z * Math.cos(theta) + 0.0065;
	    double bd_lat = z * Math.sin(theta) + 0.006;	    
	    return new LatLng(bd_lat, bd_lon);
		
	}
	
	public static LatLng BaiduToGaode(double bd_lon, double bd_lat ){
		
		double x = bd_lon - 0.0065, y = bd_lat - 0.006;
	    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
	    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
	    double gg_lon = z * Math.cos(theta);
	    double gg_lat = z * Math.sin(theta);
	    return new LatLng(gg_lat, gg_lon);
	}

}
