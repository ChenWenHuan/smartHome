package com.smarthome.client2.model.login;

import java.util.ArrayList;
import java.util.List;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class FamilyGroupList {

    @SerializedName("picPath")
    @Expose
    private String picPath;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("groupName")
    @Expose
    private String groupName;
    @SerializedName("userId")
    @Expose
    private Long userId;
    @SerializedName("latestFamilyLeavMsg")
    @Expose
    private Object latestFamilyLeavMsg;
    @SerializedName("deviceList")
    @Expose
    private Object deviceList;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("famDevList")
    @Expose
    private List<Object> famDevList = new ArrayList<Object>();

    /**
     *
     * @return
     * The picPath
     */
    public String getPicPath() {
        return picPath;
    }

    /**
     *
     * @param picPath
     * The picPath
     */
    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    /**
     *
     * @return
     * The id
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     *
     * @param groupName
     * The groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     *
     * @return
     * The userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     *
     * @param userId
     * The userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     * The latestFamilyLeavMsg
     */
    public Object getLatestFamilyLeavMsg() {
        return latestFamilyLeavMsg;
    }

    /**
     *
     * @param latestFamilyLeavMsg
     * The latestFamilyLeavMsg
     */
    public void setLatestFamilyLeavMsg(Object latestFamilyLeavMsg) {
        this.latestFamilyLeavMsg = latestFamilyLeavMsg;
    }

    /**
     *
     * @return
     * The deviceList
     */
    public Object getDeviceList() {
        return deviceList;
    }

    /**
     *
     * @param deviceList
     * The deviceList
     */
    public void setDeviceList(Object deviceList) {
        this.deviceList = deviceList;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The famDevList
     */
    public List<Object> getFamDevList() {
        return famDevList;
    }

    /**
     *
     * @param famDevList
     * The famDevList
     */
    public void setFamDevList(List<Object> famDevList) {
        this.famDevList = famDevList;
    }

}
