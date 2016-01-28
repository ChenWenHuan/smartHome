package com.smarthome.client2.model.login;

import java.util.ArrayList;
import java.util.List;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class Data {

    @SerializedName("useractive")
    @Expose
    private String useractive;
    @SerializedName("createTime")
    @Expose
    private String createTime;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("familyRelativeList")
    @Expose
    private List<FamilyRelativeList> familyRelativeList = new ArrayList<FamilyRelativeList>();
    @SerializedName("weight")
    @Expose
    private Long weight;
    @SerializedName("familyGroupList")
    @Expose
    private List<FamilyGroupList> familyGroupList = new ArrayList<FamilyGroupList>();
    @SerializedName("familySchool")
    @Expose
    private Boolean familySchool;
    @SerializedName("mainGuarder")
    @Expose
    private Long mainGuarder;
    @SerializedName("areaId")
    @Expose
    private Long areaId;
    @SerializedName("function")
    @Expose
    private Object function;
    @SerializedName("roleList")
    @Expose
    private List<Object> roleList = new ArrayList<Object>();
    @SerializedName("headpicname")
    @Expose
    private String headpicname;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("kuqi")
    @Expose
    private List<Object> kuqi = new ArrayList<Object>();
    @SerializedName("studentphone")
    @Expose
    private Object studentphone;
    @SerializedName("height")
    @Expose
    private Long height;
    @SerializedName("student")
    @Expose
    private Boolean student;
    @SerializedName("token")
    @Expose
    private Token token;
    @SerializedName("older")
    @Expose
    private Boolean older;
    @SerializedName("headpicpath")
    @Expose
    private String headpicpath;
    @SerializedName("telnum")
    @Expose
    private String telnum;
    @SerializedName("clientVer")
    @Expose
    private String clientVer;
    @SerializedName("deviceList")
    @Expose
    private List<Object> deviceList = new ArrayList<Object>();
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("teacherClasses")
    @Expose
    private List<Object> teacherClasses = new ArrayList<Object>();
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("alias")
    @Expose
    private String alias;
    @SerializedName("oldiephone")
    @Expose
    private Object oldiephone;
    @SerializedName("familyGroup")
    @Expose
    private Object familyGroup;
    @SerializedName("personalitysignature")
    @Expose
    private String personalitysignature;
    @SerializedName("familyUsers")
    @Expose
    private List<FamilyUser> familyUsers = new ArrayList<FamilyUser>();
    @SerializedName("groupId")
    @Expose
    private Long groupId;
    @SerializedName("pwd")
    @Expose
    private String pwd;
    @SerializedName("userrealname")
    @Expose
    private String userrealname;
    @SerializedName("userstatus")
    @Expose
    private String userstatus;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("roles")
    @Expose
    private String roles;
    @SerializedName("studentInfo")
    @Expose
    private Object studentInfo;
    @SerializedName("account")
    @Expose
    private String account;
    @SerializedName("timecard")
    @Expose
    private Object timecard;
    @SerializedName("isfamilyuser")
    @Expose
    private String isfamilyuser;
    @SerializedName("teacher")
    @Expose
    private Boolean teacher;
    @SerializedName("teacherId")
    @Expose
    private Long teacherId;
    @SerializedName("openId")
    @Expose
    private String openId;

    /**
     *
     * @return
     * The useractive
     */
    public String getUseractive() {
        return useractive;
    }

    /**
     *
     * @param useractive
     * The useractive
     */
    public void setUseractive(String useractive) {
        this.useractive = useractive;
    }

    /**
     *
     * @return
     * The createTime
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     *
     * @param createTime
     * The createTime
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     *
     * @return
     * The birthday
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     *
     * @param birthday
     * The birthday
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     *
     * @return
     * The familyRelativeList
     */
    public List<FamilyRelativeList> getFamilyRelativeList() {
        return familyRelativeList;
    }

    /**
     *
     * @param familyRelativeList
     * The familyRelativeList
     */
    public void setFamilyRelativeList(List<FamilyRelativeList> familyRelativeList) {
        this.familyRelativeList = familyRelativeList;
    }

    /**
     *
     * @return
     * The weight
     */
    public Long getWeight() {
        return weight;
    }

    /**
     *
     * @param weight
     * The weight
     */
    public void setWeight(Long weight) {
        this.weight = weight;
    }

    /**
     *
     * @return
     * The familyGroupList
     */
    public List<FamilyGroupList> getFamilyGroupList() {
        return familyGroupList;
    }

    /**
     *
     * @param familyGroupList
     * The familyGroupList
     */
    public void setFamilyGroupList(List<FamilyGroupList> familyGroupList) {
        this.familyGroupList = familyGroupList;
    }

    /**
     *
     * @return
     * The familySchool
     */
    public Boolean getFamilySchool() {
        return familySchool;
    }

    /**
     *
     * @param familySchool
     * The familySchool
     */
    public void setFamilySchool(Boolean familySchool) {
        this.familySchool = familySchool;
    }

    /**
     *
     * @return
     * The mainGuarder
     */
    public Long getMainGuarder() {
        return mainGuarder;
    }

    /**
     *
     * @param mainGuarder
     * The mainGuarder
     */
    public void setMainGuarder(Long mainGuarder) {
        this.mainGuarder = mainGuarder;
    }

    /**
     *
     * @return
     * The areaId
     */
    public Long getAreaId() {
        return areaId;
    }

    /**
     *
     * @param areaId
     * The areaId
     */
    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    /**
     *
     * @return
     * The function
     */
    public Object getFunction() {
        return function;
    }

    /**
     *
     * @param function
     * The function
     */
    public void setFunction(Object function) {
        this.function = function;
    }

    /**
     *
     * @return
     * The roleList
     */
    public List<Object> getRoleList() {
        return roleList;
    }

    /**
     *
     * @param roleList
     * The roleList
     */
    public void setRoleList(List<Object> roleList) {
        this.roleList = roleList;
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
     * The kuqi
     */
    public List<Object> getKuqi() {
        return kuqi;
    }

    /**
     *
     * @param kuqi
     * The kuqi
     */
    public void setKuqi(List<Object> kuqi) {
        this.kuqi = kuqi;
    }

    /**
     *
     * @return
     * The studentphone
     */
    public Object getStudentphone() {
        return studentphone;
    }

    /**
     *
     * @param studentphone
     * The studentphone
     */
    public void setStudentphone(Object studentphone) {
        this.studentphone = studentphone;
    }

    /**
     *
     * @return
     * The height
     */
    public Long getHeight() {
        return height;
    }

    /**
     *
     * @param height
     * The height
     */
    public void setHeight(Long height) {
        this.height = height;
    }

    /**
     *
     * @return
     * The student
     */
    public Boolean getStudent() {
        return student;
    }

    /**
     *
     * @param student
     * The student
     */
    public void setStudent(Boolean student) {
        this.student = student;
    }

    /**
     *
     * @return
     * The token
     */
    public Token getToken() {
        return token;
    }

    /**
     *
     * @param token
     * The token
     */
    public void setToken(Token token) {
        this.token = token;
    }

    /**
     *
     * @return
     * The older
     */
    public Boolean getOlder() {
        return older;
    }

    /**
     *
     * @param older
     * The older
     */
    public void setOlder(Boolean older) {
        this.older = older;
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
     * The telnum
     */
    public String getTelnum() {
        return telnum;
    }

    /**
     *
     * @param telnum
     * The telnum
     */
    public void setTelnum(String telnum) {
        this.telnum = telnum;
    }

    /**
     *
     * @return
     * The clientVer
     */
    public String getClientVer() {
        return clientVer;
    }

    /**
     *
     * @param clientVer
     * The clientVer
     */
    public void setClientVer(String clientVer) {
        this.clientVer = clientVer;
    }

    /**
     *
     * @return
     * The deviceList
     */
    public List<Object> getDeviceList() {
        return deviceList;
    }

    /**
     *
     * @param deviceList
     * The deviceList
     */
    public void setDeviceList(List<Object> deviceList) {
        this.deviceList = deviceList;
    }

    /**
     *
     * @return
     * The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     *
     * @param gender
     * The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     *
     * @return
     * The teacherClasses
     */
    public List<Object> getTeacherClasses() {
        return teacherClasses;
    }

    /**
     *
     * @param teacherClasses
     * The teacherClasses
     */
    public void setTeacherClasses(List<Object> teacherClasses) {
        this.teacherClasses = teacherClasses;
    }

    /**
     *
     * @return
     * The nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     *
     * @param nickname
     * The nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     *
     * @return
     * The alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     *
     * @param alias
     * The alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     *
     * @return
     * The oldiephone
     */
    public Object getOldiephone() {
        return oldiephone;
    }

    /**
     *
     * @param oldiephone
     * The oldiephone
     */
    public void setOldiephone(Object oldiephone) {
        this.oldiephone = oldiephone;
    }

    /**
     *
     * @return
     * The familyGroup
     */
    public Object getFamilyGroup() {
        return familyGroup;
    }

    /**
     *
     * @param familyGroup
     * The familyGroup
     */
    public void setFamilyGroup(Object familyGroup) {
        this.familyGroup = familyGroup;
    }

    /**
     *
     * @return
     * The personalitysignature
     */
    public String getPersonalitysignature() {
        return personalitysignature;
    }

    /**
     *
     * @param personalitysignature
     * The personalitysignature
     */
    public void setPersonalitysignature(String personalitysignature) {
        this.personalitysignature = personalitysignature;
    }

    /**
     *
     * @return
     * The familyUsers
     */
    public List<FamilyUser> getFamilyUsers() {
        return familyUsers;
    }

    /**
     *
     * @param familyUsers
     * The familyUsers
     */
    public void setFamilyUsers(List<FamilyUser> familyUsers) {
        this.familyUsers = familyUsers;
    }

    /**
     *
     * @return
     * The groupId
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     *
     * @param groupId
     * The groupId
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     *
     * @return
     * The pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     *
     * @param pwd
     * The pwd
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     *
     * @return
     * The userrealname
     */
    public String getUserrealname() {
        return userrealname;
    }

    /**
     *
     * @param userrealname
     * The userrealname
     */
    public void setUserrealname(String userrealname) {
        this.userrealname = userrealname;
    }

    /**
     *
     * @return
     * The userstatus
     */
    public String getUserstatus() {
        return userstatus;
    }

    /**
     *
     * @param userstatus
     * The userstatus
     */
    public void setUserstatus(String userstatus) {
        this.userstatus = userstatus;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The address
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The roles
     */
    public String getRoles() {
        return roles;
    }

    /**
     *
     * @param roles
     * The roles
     */
    public void setRoles(String roles) {
        this.roles = roles;
    }

    /**
     *
     * @return
     * The studentInfo
     */
    public Object getStudentInfo() {
        return studentInfo;
    }

    /**
     *
     * @param studentInfo
     * The studentInfo
     */
    public void setStudentInfo(Object studentInfo) {
        this.studentInfo = studentInfo;
    }

    /**
     *
     * @return
     * The account
     */
    public String getAccount() {
        return account;
    }

    /**
     *
     * @param account
     * The account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     *
     * @return
     * The timecard
     */
    public Object getTimecard() {
        return timecard;
    }

    /**
     *
     * @param timecard
     * The timecard
     */
    public void setTimecard(Object timecard) {
        this.timecard = timecard;
    }

    /**
     *
     * @return
     * The isfamilyuser
     */
    public String getIsfamilyuser() {
        return isfamilyuser;
    }

    /**
     *
     * @param isfamilyuser
     * The isfamilyuser
     */
    public void setIsfamilyuser(String isfamilyuser) {
        this.isfamilyuser = isfamilyuser;
    }

    /**
     *
     * @return
     * The teacher
     */
    public Boolean getTeacher() {
        return teacher;
    }

    /**
     *
     * @param teacher
     * The teacher
     */
    public void setTeacher(Boolean teacher) {
        this.teacher = teacher;
    }

    /**
     *
     * @return
     * The teacherId
     */
    public Long getTeacherId() {
        return teacherId;
    }

    /**
     *
     * @param teacherId
     * The teacherId
     */
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    /**
     *
     * @return
     * The openId
     */
    public String getOpenId() {
        return openId;
    }

    /**
     *
     * @param openId
     * The openId
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

}
