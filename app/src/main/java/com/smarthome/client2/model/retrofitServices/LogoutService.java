package com.smarthome.client2.model.retrofitServices;

import com.smarthome.client2.model.logout.LogoutServiceResult;

import retrofit.Call;
import retrofit.http.GET;


/**
 * Created by wenhuanchen on 11/18/15.
 */


public interface LogoutService {
    // 请求如下：
    // http://papp.ejcom.cn/account/login2.action,
    // D0E7B0201CB94D111644FCE141991926,IP127.0.0.1,114.221.126.148:
    // {"acctno":"17768118624","clientversion":"v1.1","pwd":"123456"}
    @GET("account/logout.action")
    Call<LogoutServiceResult> logout();
}