
/**
 * Created by wenhuanchen on 12/8/15.
 */
package com.smarthome.client2.model.getMessage;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("senderid")
    @Expose
    private Integer senderid;
    @SerializedName("filepath")
    @Expose
    private String filepath;
    @SerializedName("friendType")
    @Expose
    private String friendType;
    @SerializedName("sendername")
    @Expose
    private String sendername;
    @SerializedName("invitationResult")
    @Expose
    private String invitationResult;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("receiveid")
    @Expose
    private Integer receiveid;
    @SerializedName("invitationId")
    @Expose
    private Integer invitationId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("headpicname")
    @Expose
    private String headpicname;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("receiverOpenId")
    @Expose
    private String receiverOpenId;
    @SerializedName("headpicpath")
    @Expose
    private String headpicpath;
    @SerializedName("msgtype")
    @Expose
    private String msgtype;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("datatype")
    @Expose
    private String datatype;
    @SerializedName("sendtime")
    @Expose
    private String sendtime;

    /**
     *
     * @return
     * The senderid
     */
    public Integer getSenderid() {
        return senderid;
    }

    /**
     *
     * @param senderid
     * The senderid
     */
    public void setSenderid(Integer senderid) {
        this.senderid = senderid;
    }

    /**
     *
     * @return
     * The filepath
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     *
     * @param filepath
     * The filepath
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    /**
     *
     * @return
     * The friendType
     */
    public String getFriendType() {
        return friendType;
    }

    /**
     *
     * @param friendType
     * The friendType
     */
    public void setFriendType(String friendType) {
        this.friendType = friendType;
    }

    /**
     *
     * @return
     * The sendername
     */
    public String getSendername() {
        return sendername;
    }

    /**
     *
     * @param sendername
     * The sendername
     */
    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    /**
     *
     * @return
     * The invitationResult
     */
    public String getInvitationResult() {
        return invitationResult;
    }

    /**
     *
     * @param invitationResult
     * The invitationResult
     */
    public void setInvitationResult(String invitationResult) {
        this.invitationResult = invitationResult;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The receiveid
     */
    public Integer getReceiveid() {
        return receiveid;
    }

    /**
     *
     * @param receiveid
     * The receiveid
     */
    public void setReceiveid(Integer receiveid) {
        this.receiveid = receiveid;
    }

    /**
     *
     * @return
     * The invitationId
     */
    public Integer getInvitationId() {
        return invitationId;
    }

    /**
     *
     * @param invitationId
     * The invitationId
     */
    public void setInvitationId(Integer invitationId) {
        this.invitationId = invitationId;
    }

    /**
     *
     * @return
     * The content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @return
     * The headpicname
     */
    public String getHeadpicname() {
        return headpicname;
    }

    /**
     *
     * @param headpicname
     * The headpicname
     */
    public void setHeadpicname(String headpicname) {
        this.headpicname = headpicname;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The receiverOpenId
     */
    public String getReceiverOpenId() {
        return receiverOpenId;
    }

    /**
     *
     * @param receiverOpenId
     * The receiverOpenId
     */
    public void setReceiverOpenId(String receiverOpenId) {
        this.receiverOpenId = receiverOpenId;
    }

    /**
     *
     * @return
     * The headpicpath
     */
    public String getHeadpicpath() {
        return headpicpath;
    }

    /**
     *
     * @param headpicpath
     * The headpicpath
     */
    public void setHeadpicpath(String headpicpath) {
        this.headpicpath = headpicpath;
    }

    /**
     *
     * @return
     * The msgtype
     */
    public String getMsgtype() {
        return msgtype;
    }

    /**
     *
     * @param msgtype
     * The msgtype
     */
    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    /**
     *
     * @return
     * The filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     *
     * @param filename
     * The filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     *
     * @return
     * The datatype
     */
    public String getDatatype() {
        return datatype;
    }

    /**
     *
     * @param datatype
     * The datatype
     */
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    /**
     *
     * @return
     * The sendtime
     */
    public String getSendtime() {
        return sendtime;
    }

    /**
     *
     * @param sendtime
     * The sendtime
     */
    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

}
