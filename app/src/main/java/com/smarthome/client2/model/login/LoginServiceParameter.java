package com.smarthome.client2.model.login;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wenhuanchen on 11/19/15.
 */
public class LoginServiceParameter{
    // 注意，起的名字和传递的参数必须是一致的
    //
    private String clientversion;
    private String pwd;
    private String acctno;

    public LoginServiceParameter() {
    }

    public LoginServiceParameter(String id, String password, String username) {
        this.clientversion = id;
        this.pwd = password;
        this.acctno = username;
    }

    public String getVersionId() {
        return clientversion;
    }

    public void setVersionId(String id) {
        this.clientversion = id;
    }

    public String getPassword() {
        return pwd;
    }

    public void setPassword(String password) {
        this.pwd = password;
    }

    public String getUsername() {
        return acctno;
    }

    public void setUsername(String username) {
        this.acctno = username;
    }

}