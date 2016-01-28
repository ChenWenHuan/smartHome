package com.smarthome.client2.parser;

import java.net.URLDecoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.bean.UserMessageBean;
import com.smarthome.client2.bean.UserMessageListBean;

public class UserMessageJsonParser implements BaseJsonParser{
	
	private UserMessageListBean userMessageListBean = new UserMessageListBean();

	@Override
	public UserMessageListBean getResult(JSONObject obj) {
		userMessageListBean.list.clear();
		return userMessageListBean;
	}
	
	public UserMessageListBean getResult(String s) {
		userMessageListBean.list.clear();
		try {
			JSONArray array = new JSONArray(s);
			for(int i = 0;i<array.length();i++){
				UserMessageBean bean = new UserMessageBean();
				JSONObject obj = array.getJSONObject(i);
				if(obj.has("id")){
					bean.id = obj.getString("id");
				}
				
				if(obj.has("fromUser")){
					bean.fromUser = URLDecoder.decode(obj.getString("fromUser"));
				}
				
				if(obj.has("toUser")){
					bean.toUser = obj.getString("toUser");
				}
				
				if(obj.has("content")){
					bean.content = obj.getString("content");
				}
				if(obj.has("createTime")){
					bean.createTime = obj.getLong("createTime");
				}
				
				if(obj.has("status")){
					bean.status = obj.getInt("status");
				}
				
				if(obj.has("type")){
					bean.type = obj.getInt("type");
				}
				if(obj.has("challengeId")){
					bean.challengeId = obj.getString("challengeId");
				}
				
				if(obj.has("fromUserName")){
					bean.fromUserName = obj.getString("fromUserName");
				}
				
				if(obj.has("toUserName")){
					bean.toUserName = obj.getString("toUserName");
				}
				
				userMessageListBean.list.add(bean);
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userMessageListBean;
	}

}
