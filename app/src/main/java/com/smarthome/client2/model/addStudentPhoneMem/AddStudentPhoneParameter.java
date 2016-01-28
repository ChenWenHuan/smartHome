package com.smarthome.client2.model.addStudentPhoneMem;

/**
 * Created by ajimide on 15-12-23.
 */
public class AddStudentPhoneParameter {
    private String acctCode = "";
    private String email = "";
    private String acctType = "02";
    private String appellationCode = "00";
    private String deviceType = "1";

    private String groupId;          //  mFamilyId
    private String deviceCode;       // mOldPhoneIMEI
    private String maintotarAlias;   //oldPhoneNumber
    private String devTelNum;        //oldPhoneNumber
    private String telNum;           // oldPhoneNumbers

    public AddStudentPhoneParameter(String familyId, String imeiNum, String phoneNum) {
        this.groupId = familyId;
        this.deviceCode = imeiNum;
        this.telNum = phoneNum;
        this.maintotarAlias = phoneNum;
        this.devTelNum = phoneNum;
    }
}