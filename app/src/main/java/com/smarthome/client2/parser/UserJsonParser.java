package com.smarthome.client2.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.bean.BaseBean;
import com.smarthome.client2.bean.UserBean;

public class UserJsonParser implements BaseJsonParser{
//{
//	"useractive":"",
//	"accttype":"2",
//	"nickname":"",
//	"usertype":"",
//	"personalitysignature":"",
//	"acctno":"1",
//	"id":1,
//	"pwd":"1",
//	"schoolname":"",
//	"userrealname":"",
//	"userstatus":"",
//	"headpicpath":"",
//	"telnum":"18651860423",
//	"gender":"",
//	"roleId":0}

	@Override
	public BaseBean getResult(JSONObject obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseBean getResult(String s_obj) {
		
		
		try {
			UserBean bean = new UserBean();
			JSONObject obj = new JSONObject(s_obj);
			
			if(obj.has("useractive")){
				bean.useractive = obj.getInt("useractive");
			}
			
			if(obj.has("accttype")){
				bean.accttype = obj.getInt("accttype");
			}
			if(obj.has("nickname")){
				bean.nickname = obj.getString("nickname");
			}
			if(obj.has("usertype")){
				bean.usertype = obj.getInt("usertype");
			}
			if(obj.has("personalitysignature")){
				bean.personalitysignature = obj.getString("personalitysignature");
			}
			if(obj.has("acctno")){
				bean.acctno = obj.getString("acctno");
			}
			if(obj.has("id")){
				bean.id = obj.getString("id");
			}
			if(obj.has("pwd")){
				bean.pwd = obj.getString("pwd");
			}
			if(obj.has("schoolname")){
				bean.schoolname = obj.getString("schoolname");
			}
			if(obj.has("userrealname")){
				bean.userrealname = obj.getString("userrealname");
			}
			if(obj.has("userstatus")){
				bean.userstatus = obj.getInt("userstatus");
			}
			if(obj.has("headpicpath")){
				bean.headpicpath = obj.getString("headpicpath");
			}
			if(obj.has("telnum")){
				bean.telnum = obj.getString("telnum");
			}
			if(obj.has("gender")){
				bean.gender = obj.getInt("gender");
			}
			if(obj.has("roleId")){
				bean.roleId = obj.getString("roleId");
			}
			
			if(obj.has("year")){
				bean.year = obj.getInt("year");
			}
			if(obj.has("height")){
				bean.height = obj.getInt("height");
			}
			if(obj.has("weight")){
				bean.weight = (float) obj.getDouble("weight");
			}
			
			return bean;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
