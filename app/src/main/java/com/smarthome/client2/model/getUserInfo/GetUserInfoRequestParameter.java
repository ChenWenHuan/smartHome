package com.smarthome.client2.model.getUserInfo;

/**
 * Created by wenhuanchen on 11/24/15.
 */

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wenhuanchen on 11/19/15.
 */
public class GetUserInfoRequestParameter {
    // 注意，起的名字和传递的参数必须是一致的
    //
    private String userId;


    public GetUserInfoRequestParameter() {
    }

    public GetUserInfoRequestParameter(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}