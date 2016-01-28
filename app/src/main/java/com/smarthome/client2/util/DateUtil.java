package com.smarthome.client2.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class DateUtil {

	private static DateUtil instance;

	public static DateUtil getInstance() {
		if (instance == null) {
			instance = new DateUtil();
		}
		return instance;
	}

	public Calendar Date2Calendar(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(d.getTime());
		return c;
	}

	public Date Calendar2Date(Calendar c) {
		return c.getTime();
	}

	public Date String2Date(String s) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.parse(s);
	}
	
	public String getCurrentYearMonthDay(){
		
		Calendar cal=Calendar.getInstance();
		
		return Integer.toString(cal.get(Calendar.YEAR)) 
			   + Integer.toString(cal.get(Calendar.MONTH)) 
			   + Integer.toString(cal.get(Calendar.DATE));
		
	}
	
    public static String dateToWeek(){
    	
  	  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  	  StringBuilder  strWeek = new StringBuilder("");
  	  Date mdate = new Date();
		  int b=mdate.getDay();
		  Date fdate ;		  
		  Long fTime=mdate.getTime();
		  for(int a=0;a<7;a++){	  
			  fdate= new Date();
			  fdate.setTime(fTime-(a*24*3600000));
			  strWeek.append(dateFormat.format(fdate));
			  if (a != 6){
				  strWeek.append(",");  
			  }
		  }
		  Log.e("--dateToWeek--", "--weekcommand=" + strWeek.toString());
		  return strWeek.toString();
	  }
}
