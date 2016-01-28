package com.smarthome.client2.model.retrofitServices;

import com.smarthome.client2.model.getUserInfo.GetUserInfoRequestParameter;
import com.smarthome.client2.model.getUserInfo.GetUserInfoResultModel;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by wenhuanchen on 11/24/15.
 */

public interface GetUserInfoService {
    // 请求如下：
    // http://papp.ejcom.cn/account/login2.action,
    // D0E7B0201CB94D111644FCE141991926,IP127.0.0.1,114.221.126.148:
    // {"acctno":"17768118624","clientversion":"v1.1","pwd":"123456"}
    @POST("/account/getUserSynopsis.action")
    @Headers({
            "Content-type: application/json"
    })
    Call<GetUserInfoResultModel> getUserInfo(
            @Header("Authorization") String authorization,
            @Header("Cookie") String cookie,
            @Body GetUserInfoRequestParameter userId
    );
}