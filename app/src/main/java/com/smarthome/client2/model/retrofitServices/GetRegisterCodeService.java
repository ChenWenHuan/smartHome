package com.smarthome.client2.model.retrofitServices;

import com.smarthome.client2.model.getRegisterCode.GetRegisterVerifyCode;
import com.smarthome.client2.model.getRegisterCode.GetRegisterCodeResult;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by wenhuanchen on 12/14/15.
 */
public interface GetRegisterCodeService {
    @POST("/account/getValidCode.action")
    @Headers({
            "Content-type: application/json"
    })
    Observable<GetRegisterCodeResult> getRegisterCode(
            @Body GetRegisterVerifyCode regCode
    );

    @POST("/account/getValidCode.action")
    @Headers({
            "Content-type: application/json"
    })
    Observable<Response<ResponseBody>> getRegisterCodeRawResp(
            @Body GetRegisterVerifyCode regCode
    );
}
