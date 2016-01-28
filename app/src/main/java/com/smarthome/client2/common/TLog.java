package com.smarthome.client2.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TLog {
	
	public static void Log(String content) {
		final StackTraceElement[] stack = new Throwable().getStackTrace();
		final int i = 1;
		final StackTraceElement ste = stack[i];
		android.util.Log.d("com.smarthome.client2", String.format("[%s][%s][%s]%s", ste.getFileName(), ste.getMethodName(), ste.getLineNumber(), content));
	}
	
	public static InputStream Log(InputStream is){
		String s = getStringFromInputStream(is);
		Log(s);
		return getInputStreamFromString(s);
	}

	private static InputStream getInputStreamFromString(String s) {

		InputStream is = null;
		try {
			is = new ByteArrayInputStream(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return is;
	}

	private static String getStringFromInputStream(InputStream is) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String s = "";
		try {
			while( (s = br.readLine()) != null){
				sb.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
}
