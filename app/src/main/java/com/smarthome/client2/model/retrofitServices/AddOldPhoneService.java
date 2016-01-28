package com.smarthome.client2.model.retrofitServices;

import com.smarthome.client2.model.addOldPhoneMem.AddOldPhoneReqParameter;
import com.smarthome.client2.model.addOldPhoneMem.AddOldPhoneResult;
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
public interface AddOldPhoneService {
    @POST("/family/addNewFamilyMember2.action")
    @Headers({
            "Content-type: application/json"
    })
    Observable<AddOldPhoneResult> addOldPhone(
            @Body AddOldPhoneReqParameter oldPhoneInfo
    );

    @POST("/family/addNewFamilyMember2.action")
    @Headers({
            "Content-type: application/json"
    })
    Observable<Response<ResponseBody>> addOldPhoneRawRespons(
            @Body AddOldPhoneReqParameter oldPhoneInfo
    );
}
