package com.smarthome.client2.common;

import android.os.Environment;


public interface SysParamers {
	String URL_TEST = "http://10.20.120.96:8080/Smart_Home_Cloud/AppCommHttpServer";
	
	
	String BASE_URL = "http://10.20.41.5:8080/smart/";
	String URL_LOGIN = BASE_URL + "account/login.action";
	String URL_REGISTER = BASE_URL+"account/register.action";
	String URL_VALIDCODE = BASE_URL+"account/validCode.action";
	String URL_GETNOTICES = BASE_URL+"account/validCode.action";
	String URL_UPDATE_USER_INFO = BASE_URL+"appuser/modify.action";
	String URL_MODIFY_PASS_WORD = "http://10.20.120.96:8080/Smart_Home_Cloud/AppCommHttpServer";
	
	String APP_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.smarthome.client2/";
	String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.smarthome.client2/img/";
	String IMAGE_SHORT_FILE_NAME = "ShortCut.png";
	String IMAGE_SHORT_FILE_PATH = IMAGE_FILE_PATH+IMAGE_SHORT_FILE_NAME;
	String IMAGE_HEAD_FILE_NAME = "head/";
	String IMAGE_HEAD_FILE_PATH = IMAGE_FILE_PATH+IMAGE_HEAD_FILE_NAME;
	
	
	interface MenuItemPosition{
		int index_home = 0;
		int index_family = 1;
		int index_zhihuiquan = 2;
		int index_device = 3;
		int index_setting = 4;
		  int index_location = 5;
	}
	
}
