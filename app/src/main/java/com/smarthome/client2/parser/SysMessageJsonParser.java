package com.smarthome.client2.parser;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.smarthome.client2.bean.SysMessageBean;
import com.smarthome.client2.bean.SysMessageListBean;

public class SysMessageJsonParser implements BaseJsonParser{
	
	private SysMessageListBean sysMessageListBean = new SysMessageListBean();

	@Override
	public SysMessageListBean getResult(JSONObject obj) {
		sysMessageListBean.list.clear();
		return sysMessageListBean;
	}
	
	public SysMessageListBean getResult(String s) {
		sysMessageListBean.list.clear();
		try {
			JSONObject json = new JSONObject(s);
			JSONArray sysmessage = json.getJSONArray("data");
			for(int i = 0;i<sysmessage.length();i++){
				SysMessageBean bean = new SysMessageBean();
				JSONObject obj = sysmessage.getJSONObject(i);
				if(obj.has("id")&&!TextUtils.isEmpty(obj.getString("id"))){
					bean.id = obj.getLong("id");
				}
				
				if(obj.has("msgtype")){
					bean.msgtype = obj.getString("msgtype");
				}
				
				if(obj.has("datatype")){
					bean.datatype = obj.getString("datatype");
				}
				
				if(obj.has("title")){
					bean.title = obj.getString("title");
				}
				if(obj.has("senderid")&&!TextUtils.isEmpty(obj.getString("senderid"))){
					bean.senderid = obj.getLong("senderid");
				}
				
				if(obj.has("sendername")){
					bean.sendername = obj.getString("sendername");
				}
				
				if(obj.has("receiveid")&&!TextUtils.isEmpty(obj.getString("receiveid"))){
					bean.receiveid = obj.getLong("receiveid");
				}
				if(obj.has("content")){
					bean.content = obj.getString("content");
				}
				
				if(obj.has("filename")){
					bean.filename = obj.getString("filename");
				}
				
				if(obj.has("filepath")){
					bean.filepath = obj.getString("filepath");
				}
				
				if(obj.has("headpicpath")){
					bean.headpicpath = obj.getString("headpicpath");
				}
				
				if(obj.has("headpicname")){
					bean.headpicname = obj.getString("headpicname");
				}
				
				if(obj.has("sendtime")){
					try {
						String sendtime = obj.getString("sendtime");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
						bean.sendtime = sdf.parse(sendtime).getTime();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				
				if(obj.has("invitationResult")&&!TextUtils.isEmpty(obj.getString("invitationResult"))){
					bean.invitationResult = obj.getInt("invitationResult");
				}
				
				if(obj.has("invitationId")&&!TextUtils.isEmpty(obj.getString("invitationId"))){
					bean.invitationId = obj.getInt("invitationId");
				}
				
				if(obj.has("status")&&!TextUtils.isEmpty(obj.getString("status"))){
					bean.status = obj.getInt("status");
				}
				
				if(obj.has("friendType")&&!TextUtils.isEmpty(obj.getString("friendType"))){
					bean.friendType = obj.getInt("friendType");
				}
				
				sysMessageListBean.list.add(bean);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sysMessageListBean;
	}

}
