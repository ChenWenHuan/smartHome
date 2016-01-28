package com.smarthome.client2.parser;

import org.json.JSONObject;

import com.smarthome.client2.bean.BaseBean;

public interface BaseJsonParser {

	BaseBean getResult(JSONObject obj);
	
	BaseBean getResult(String s_obj);
	
}
