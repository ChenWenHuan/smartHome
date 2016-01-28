package com.smarthome.client2.model.login;

// import javax.annotation.Generated;
import com.smarthome.client2.model.baseModel.BaseModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class LoginModel extends BaseModel {

    @SerializedName("retcode")
    @Expose
    private Long retcode;
    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("retmsg")
    @Expose
    private String  retmsg;

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    /**
     *
     * @return
     * The retcode
     */
    public Long getRetcode() {
        return retcode;
    }

    /**
     *
     * @param retcode
     * The retcode
     */
    public void setRetcode(Long retcode) {
        this.retcode = retcode;
    }

    /**
     *
     * @return
     * The data
     */
    public Data getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(Data data) {
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
        return "LoginModel";
    }
}
