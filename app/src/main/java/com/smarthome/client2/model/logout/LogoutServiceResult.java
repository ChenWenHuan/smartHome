package com.smarthome.client2.model.logout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by wenhuanchen on 11/21/15.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogoutServiceResult {

    @SerializedName("retcode")
    @Expose
    private Integer retcode;

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

}