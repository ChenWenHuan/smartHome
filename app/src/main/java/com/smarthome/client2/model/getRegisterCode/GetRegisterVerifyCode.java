package com.smarthome.client2.model.getRegisterCode;

/**
 * Created by wenhuanchen on 12/14/15.
 */
public class GetRegisterVerifyCode {
    private String telNum;
//    boolean reg = true;
    public GetRegisterVerifyCode(String regPhone) {
        this.telNum = regPhone;
    }
}
