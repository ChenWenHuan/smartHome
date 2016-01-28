package com.smarthome.client2.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.bean.InviteMessageBean;
import com.smarthome.client2.bean.InviteMessageListBean;

public class InviteMessageJsonParser implements BaseJsonParser{
	
	private InviteMessageListBean inviteMessageListBean = new InviteMessageListBean();

	@Override
	public InviteMessageListBean getResult(JSONObject obj) {
		inviteMessageListBean.list.clear();
		return inviteMessageListBean;
	}
	
	public InviteMessageListBean getResult(String s) {
		inviteMessageListBean.list.clear();
		try {
			JSONObject json = new JSONObject(s);
			JSONObject data = json.getJSONObject("data");
			JSONArray inviteMsg = data.getJSONArray("inviteMsg");
			for(int i = 0;i<inviteMsg.length();i++){
				InviteMessageBean bean = new InviteMessageBean();
				JSONObject obj = inviteMsg.getJSONObject(i);
				if(obj.has("id")){
					bean.id = obj.getLong("id");
				}
				
				if(obj.has("contact_group_id")){
					bean.contactGroupId = obj.getLong("contact_group_id");
				}
				
				if(obj.has("invitation_result")){
					bean.invitationResult = obj.getInt("invitation_result");
				}
				
				if(obj.has("send_user_id")){
					bean.sendUserId = obj.getLong("send_user_id");
				}
				
				if(obj.has("send_user_name")){
					bean.sendUserName = obj.getString("send_user_name");
				}
				
				if(obj.has("target_user_id")){
					bean.targetUserId = obj.getLong("target_user_id");
				}
				
				if(obj.has("type")){
					bean.type = obj.getInt("type");
				}
				
				inviteMessageListBean.list.add(bean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return inviteMessageListBean;
	}

}
