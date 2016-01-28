package com.smarthome.client2.model.getUserInfo;

import com.smarthome.client2.model.baseModel.BaseModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by wenhuanchen on 11/24/15.
 */

//        import javax.annotation.Generated;


public class GetUserInfoResultModel extends BaseModel {

    @SerializedName("retcode")
    @Expose
    private Integer retcode;
    @SerializedName("data")
    @Expose
    private GetUserInfoResultData data;

    /**
     * @return The retcode
     */
    public Integer getRetcode() {
        return retcode;
    }

    /**
     * @param retcode The retcode
     */
    public void setRetcode(Integer retcode) {
        this.retcode = retcode;
    }

    public GetUserInfoResultModel withRetcode(Integer retcode) {
        this.retcode = retcode;
        return this;
    }

    /**
     * @return The data
     */
    public GetUserInfoResultData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(GetUserInfoResultData data) {
        this.data = data;
    }

    public GetUserInfoResultModel withData(GetUserInfoResultData data) {
        this.data = data;
        return this;
    }


    @Override
    public String modelToString() {
        return null;
    }

    @Override
    public BaseModel stringToModel(String s_model) {
        return null;
    }

    @Override
    public String getKey() {
        return "GetUserInfoResultModel";
    }
}