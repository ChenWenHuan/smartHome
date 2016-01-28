package com.smarthome.client2.model.retrofitServices;

import com.smarthome.client2.model.login.LoginModel;
import com.smarthome.client2.model.login.LoginServiceParameter;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONObject;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;


/**
 * Created by wenhuanchen on 11/18/15.
 */


public interface ApiService {
    // 请求如下：
    // http://papp.ejcom.cn/account/login2.action,
    // D0E7B0201CB94D111644FCE141991926,IP127.0.0.1,114.221.126.148:
    // {"acctno":"17768118624","clientversion":"v1.1","pwd":"123456"}
    @POST("account/login2.action")
    @Headers({"Content-type: application/json"})
    Call<LoginModel> login(@Body LoginServiceParameter user);

    @POST("/account/getMain.action")
    @Headers({"Content-type: application/json"})
    Call<ResponseBody> getMain();

    @POST("/family/addNewMemberWithShortMsg.action")
    @Headers({"Content-type: application/json"})
    Call<ResponseBody> addSmartPhoneAccount(@Body AddSmartPhoneAccountParameters parameters);


    public class AddSmartPhoneAccountParameters{

        private  String acctType = "02";
        private  String acctCode = "";
        private  String email = "";
        private  String telNum;
        private  String validCode;
        private  String groupId;
        private  String appellationCode = "00";
        private  String maintotarAlias;
        private  String deviceType = "";
        private  String deviceCode = "";
        private  String devTelNum = "";

        public AddSmartPhoneAccountParameters(){

        }

        public AddSmartPhoneAccountParameters(String acctType,String acctCode,String email,String telNum
                                                ,String validCode,String groupId,String appellationCode,String maintotarAlias
                                                ,String deviceType,String deviceCode,String devTelNum){

            this.acctType =acctType;
            this.acctCode = acctCode;
            this.email    = email;
            this.telNum   = telNum;
            this.validCode = validCode;
            this.groupId = groupId;
            this.appellationCode = appellationCode;
            this.maintotarAlias  = maintotarAlias;
            this.deviceType = deviceType;
            this.deviceCode = deviceCode;
            this.devTelNum  = devTelNum;
        }


        public String getAcctType() {
            return acctType;
        }

        public void setAcctType(String acctType) {
            this.acctType = acctType;
        }

        public String getAcctCode() {
            return acctCode;
        }

        public void setAcctCode(String acctCode) {
            this.acctCode = acctCode;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelNum() {
            return telNum;
        }

        public void setTelNum(String telNum) {
            this.telNum = telNum;
        }

        public String getValidCode() {
            return validCode;
        }

        public void setValidCode(String validCode) {
            this.validCode = validCode;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getAppellationCode() {
            return appellationCode;
        }

        public void setAppellationCode(String appellationCode) {
            this.appellationCode = appellationCode;
        }

        public String getMaintotarAlias() {
            return maintotarAlias;
        }

        public void setMaintotarAlias(String maintotarAlias) {
            this.maintotarAlias = maintotarAlias;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public String getDevTelNum() {
            return devTelNum;
        }

        public void setDevTelNum(String devTelNum) {
            this.devTelNum = devTelNum;
        }
    }
}