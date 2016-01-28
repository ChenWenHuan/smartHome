package com.smarthome.client2.parser;


import java.util.Hashtable;

import org.json.JSONObject;

import com.smarthome.client2.bean.BaseBean;


public interface BaseJsonHashParser {
	Hashtable<String, BaseBean> getResult(JSONObject obj);
}

