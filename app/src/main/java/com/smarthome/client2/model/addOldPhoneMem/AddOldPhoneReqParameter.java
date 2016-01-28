package com.smarthome.client2.model.addOldPhoneMem;

import com.smarthome.client2.util.StringUtils;

/**
 * Created by wenhuanchen on 12/14/15.
 */
public class AddOldPhoneReqParameter {

    private String acctCode = "";
    private String email = "";
    private String acctType = "02";
    private String appellationCode = "00";
    private String deviceType = "2";

    private String groupId;          //  mFamilyId
    private String deviceCode;       // mOldPhoneIMEI
    private String maintotarAlias;   //oldPhoneNumber
    private String devTelNum;        //oldPhoneNumber
    private String telNum;           // oldPhoneNumber
    private String authcode;

    public AddOldPhoneReqParameter(String familyId, String imeiNum, String phoneNum,String authcode) {
        this.groupId = familyId;
        this.deviceCode = imeiNum;
        this.telNum = phoneNum;
        this.maintotarAlias = phoneNum;
        this.devTelNum = phoneNum;
        this.authcode = authcode;
    }
}
