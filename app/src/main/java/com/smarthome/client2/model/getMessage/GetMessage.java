/**
 * Created by wenhuanchen on 12/8/15.
 */


package com.smarthome.client2.model.getMessage;
import java.util.ArrayList;
import java.util.List;
//import javax.annotation.Generated;
import com.smarthome.client2.model.baseModel.BaseModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetMessage extends BaseModel {

    @SerializedName("retcode")
    @Expose
    private Integer retcode;
    @SerializedName("data")
    @Expose
    private List<Datum> data = new ArrayList<Datum>();

    /**
     *
     * @return
     * The retcode
     */
    public Integer getRetcode() {
        return retcode;
    }

    /**
     *
     * @param retcode
     * The retcode
     */
    public void setRetcode(Integer retcode) {
        this.retcode = retcode;
    }

    /**
     *
     * @return
     * The data
     */
    public List<Datum> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<Datum> data) {
        this.data = data;
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

