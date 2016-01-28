package com.smarthome.client2.model.getRegisterCode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by wenhuanchen on 12/14/15.
 */
public class GetRegisterCodeResult {
    @SerializedName("retcode")
    @Expose
    private Integer retcode;

    @SerializedName("data")
    @Expose
    private String data;

    @SerializedName("retmsg")
    @Expose
    private String retmsg;

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

    /**
     * @return The retmsg
     */
    public String getRetmsg() {
        return retmsg;
    }

    /**
     * @param retmsg The retmsg
     */
    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    /**
     * @return The data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(String data) {
        this.data = data;
    }

}
