package com.smarthome.client2.model.retrofitServices;

import com.smarthome.client2.model.addStudentPhoneMem.AddStudentPhoneParameter;
import com.squareup.okhttp.ResponseBody;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by ajimide on 15-12-23.
 */
public interface AddStudentPhoneService {
    @POST("/family/addNewFamilyMember.action")
    @Headers({
            "Content-type: application/json"
    })
    Observable<Response<ResponseBody>> addStudentPhoneRawRespons(
            @Body AddStudentPhoneParameter studentPhoneInfo
    );
}
