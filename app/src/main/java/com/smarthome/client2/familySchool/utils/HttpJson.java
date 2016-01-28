package com.smarthome.client2.familySchool.utils;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpJson {

	private JSONObject jObject;

	public HttpJson() {
		jObject = new JSONObject();
	}

	public void put(String key, Object value) {
		if (key != null && key.length() > 0) {
			try {
				jObject.put(key, value);
			} catch (JSONException e) {
				LogUtil.e("HttpJson", "JSONException");
				e.printStackTrace();
			}
		}
	}

	public HttpEntity getEntity() throws UnsupportedEncodingException {
		LogUtil.i("params", jObject.toString());
		return new StringEntity(jObject.toString(), HTTP.UTF_8);
	}

}
