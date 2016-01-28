package com.smarthome.client2.util;

import android.content.Context;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.bean.BaseBean;
import com.smarthome.client2.bean.ClassBean;
import com.smarthome.client2.bean.FamilyBean;
import com.smarthome.client2.bean.FamilyClassBean;
import com.smarthome.client2.bean.FamilyRelativeBean;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.model.login.FamilyGroupList;
import com.smarthome.client2.model.login.FamilyRelativeList;
import com.smarthome.client2.model.login.FamilyUser;
import com.smarthome.client2.model.login.LoginModel;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录帮助类<BR>
 * 包括发送登录请求封装，登录结果解析，登录成功解析的家庭列表信息设置及查询等功能
 *
 * @author Annie
 * @version [ODP Client R001C01LAI141, 2014年12月20日]
 */
public class LoginUtil_vii {
    private static final boolean DEBUG = true;

    private static ArrayList<BaseBean> mAllFamilyList = new ArrayList<BaseBean>();

    private static ArrayList<FamilyBean> mFamilyList = new ArrayList<FamilyBean>();

    private static ArrayList<ClassBean> mClassList = new ArrayList<ClassBean>();

    public static ArrayList<BaseBean> getAllFamilyList() {
        return mAllFamilyList;
    }

    public static ArrayList<FamilyBean> getFamilyList() {
        return mFamilyList;
    }

    public static ArrayList<ClassBean> getClassList() {
        return mClassList;
    }

    /**
     * [设置列表信息]<BR>
     * 设置列表信息
     *
     * @param allfamilyList 所有列表信息
     */
    private static void setAllFamilyList(ArrayList<BaseBean> allfamilyList) {
        ArrayList<BaseBean> newfamilylist = (ArrayList<BaseBean>) allfamilyList.clone();
        mAllFamilyList.clear();
        mAllFamilyList.addAll(newfamilylist);
    }

    /**
     * [设置家庭用户列表信息]<BR>
     * 设置家庭用户列表信息
     *
     * @param familyList 家庭成员列表信息
     */
    public static void setFamilyList(ArrayList<FamilyBean> familyList) {
        ArrayList<FamilyBean> newfamilylist = (ArrayList<FamilyBean>) familyList.clone();
        mFamilyList.clear();
        mFamilyList.addAll(newfamilylist);
        updataAllFamilyList();
    }

    /**
     * 更新成员列表信息<BR>
     * 更新成员列表信息
     */
    private static void updataAllFamilyList() {
        ArrayList<BaseBean> newlist = new ArrayList<BaseBean>();
        if (getFamilyList() != null && getFamilyList().size() > 0) {
            newlist.addAll(getFamilyList());
        }
        if (getClassList() != null && getClassList().size() > 0) {
            newlist.addAll(getClassList());
        }
        if (newlist != null && newlist.size() > 0) {
            setAllFamilyList(newlist);
        }
    }

    /**
     * [设置家庭用户列表信息]<BR>
     * 设置家庭用户列表信息
     *
     * @param familyList 家庭成员列表信息
     */
    private static void setClassList(ArrayList<ClassBean> classList) {
        ArrayList<ClassBean> newclasslist = (ArrayList<ClassBean>) classList.clone();
        mClassList.clear();
        mClassList.addAll(newclasslist);
        updataAllFamilyList();
    }

    /**
     * [登录请求封装方法]<BR>
     * 发送登录请求并返回结果
     *
     * @param context  请求登录时的上下文
     * @param username 用户名
     * @param password 密码
     * @return result 登录请求返回结果
     */
    public static String requestLogin(Context context, String username,
                                      String password) {
        String result = "";
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("acctno", username);
            requestObject.put("pwd", password);
            requestObject.put("clientversion", "v1.1");

            final RequestResult requestResult = new RequestResult();
            HttpUtil.postSmartRequest(requestObject,
                    Constants.LOGIN_V11_ACTION,
                    requestResult,
                    context);
            int sc = requestResult.getCode();
            if (sc == HttpStatus.SC_OK) {
                result = requestResult.getResult();
            } else {
                result = String.valueOf(sc);
            }
        } catch (JSONException e) {
        }
        return result;
    }

    public static String requestNewServerAddr_v11(Context context) {
        String result = "";
        JSONObject requestObject = new JSONObject();

        final RequestResult requestResult = new RequestResult();

        HttpUtil.BASE_URL_SMART = HttpUtil.FIRST_ENTRY_ADDR;

        HttpUtil.postSmartRequest(requestObject,
                Constants.GET_NEW_SERVER_LIST,
                requestResult,
                context);
        int sc = requestResult.getCode();
        result = requestResult.getResult();
        return result;
    }

    public static String requestNewServerAddr_v11(Context context, long serverId) {
        String result = "";
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("getType", "1");
            requestObject.put("bussServerId", serverId);

            final RequestResult requestResult = new RequestResult();

            HttpUtil.BASE_URL_SMART = HttpUtil.FIRST_ENTRY_ADDR; //"http://123.57.206.32:8080/" ;

            HttpUtil.postSmartRequest(requestObject,
                    Constants.GET_NEW_SERVER_ADDR,
                    requestResult,
                    context);
            int sc = requestResult.getCode();
            result = requestResult.getResult();
        } catch (JSONException e) {
            log("requestLogin---------JSONException:" + e.toString());
        }
        return result;
    }

    public static String requestNewServerAddr_v11(Context context, String phoneNum) {
        String result = "";
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("getType", "2");
            requestObject.put("multiAcctCode", phoneNum);

            final RequestResult requestResult = new RequestResult();

            HttpUtil.BASE_URL_SMART = HttpUtil.FIRST_ENTRY_ADDR;

            HttpUtil.postSmartRequest(requestObject,
                    Constants.GET_NEW_SERVER_ADDR,
                    requestResult,
                    context);
            int sc = requestResult.getCode();
            result = requestResult.getResult();
        } catch (JSONException e) {
            log("requestLogin---------JSONException:" + e.toString());
        }
        return result;
    }

    public static void parseLoginInfoToOldInterface(Context context, LoginModel model) {

        ArrayList<FamilyRelativeBean> relativeList = null;

        String tokenCode = "";
        tokenCode = model.getData().getToken().getTokenCode();

        List<FamilyRelativeList> familyRelative = model.getData().getFamilyRelativeList();
        if (familyRelative.size() != 0) {
            relativeList = parserFamilyRelative(context, familyRelative);

            if (relativeList != null
                    && SmartHomeApplication.getInstance().getRelativeData().size() == 0) {

                SmartHomeApplication.getInstance().getRelativeData().addAll(relativeList);
            }
        }
        String userId = model.getData().getId().toString();
        Preferences tmpPreferences = Preferences.getInstance(context.getApplicationContext());
        tmpPreferences.setToken(tokenCode);
        tmpPreferences.setUserID(userId);
        SmartHomeApplication.getInstance().openID = model.getData().getOpenId();
        tmpPreferences.setUserTelNum(model.getData().getTelnum());
        tmpPreferences.setRealName(model.getData().getUserrealname());
        tmpPreferences.setHeadPath(model.getData().getHeadpicpath());
        Boolean bIsTeacher = model.getData().getTeacher();

        tmpPreferences.setIsTeacher(bIsTeacher);

        if (bIsTeacher) {
            tmpPreferences.setTeacherId(model.getData().getTeacherId().toString());
        }
        // 登陆的个人信息
        MemBean loginUserInfo = SmartHomeApplication.getInstance().getLoginMemberInfo();
        loginUserInfo.setGender(model.getData().getGender());
        loginUserInfo.setMemID(model.getData().getId().toString());
        loginUserInfo.setMemGroupID(model.getData().getGroupId().toString());
        loginUserInfo.setMemPhoneNum(model.getData().getTelnum());
        loginUserInfo.setMemHeadImgUrl(model.getData().getHeadpicpath());
        loginUserInfo.setHeight(model.getData().getHeight().toString());
        loginUserInfo.setWeight(model.getData().getWeight().toString());

    }

    public static ArrayList<FamilyClassBean> buildFamilyListData(ArrayList<FamilyBean> family, ArrayList<MemBean> mems, String userID) {

        ArrayList<FamilyClassBean> familyData = new ArrayList<FamilyClassBean>();
        for (int i = 0; i < family.size(); i++) {
            FamilyClassBean familyBean = new FamilyClassBean();
            ArrayList<MemBean> memListData = new ArrayList<MemBean>();
            familyBean.id = family.get(i).mGroupId;
            familyBean.type = "0";
            familyBean.keypersionID = family.get(i).memberid;
            familyBean.name = family.get(i).nickname;
            familyBean.imgUrl = family.get(i).headpicpath;
            familyBean.familyMsg = family.get(i).familyMsg;
            for (int j = 0; j < mems.size(); j++) {
                if (familyBean.id.equals(mems.get(j).memGroupID)) {
                    if (!userID.equals(mems.get(j).memID)) {
                        memListData.add(mems.get(j));
                    }
                }
            }
            familyBean.getList().clear();
            familyBean.getList().addAll(memListData);
            if (familyBean.keypersionID.equals(userID) && memListData.size() > 0) {
                familyData.add(0, familyBean);
            } else {
                familyData.add(familyBean);
            }
        }

        return familyData;
    }

    public static ArrayList<FamilyRelativeBean> parserFamilyRelative(Context ctx, List<FamilyRelativeList> array) {

        ArrayList<FamilyRelativeBean> relativeList = new ArrayList<FamilyRelativeBean>();
        for (int i = 0; i < array.size(); i++) {
            FamilyRelativeBean relativeBean = new FamilyRelativeBean();
            relativeBean.id = array.get(i).getId().toString();
            relativeBean.title = array.get(i).getAppellationName();
            relativeBean.code = array.get(i).getAppellationCode();
            relativeList.add(relativeBean);
        }
        return relativeList;
    }

    public static ArrayList<FamilyBean> parserFamilyGroup(Context ctx, List<FamilyGroupList> array) {

        ArrayList<FamilyBean> familyList = new ArrayList<FamilyBean>();
        for (int i = 0; i < array.size(); i++) {
            FamilyBean familyBean = new FamilyBean();
            familyBean.mGroupId = array.get(i).getId().toString();
            familyBean.nickname = array.get(i).getGroupName();
            familyBean.memberid = array.get(i).getUserId().toString();  //户主的ID
            familyBean.headpicpath = array.get(i).getPicPath();
//                if (!array.get(i).getLatestFamilyLeavMsg().toString().equals("null")) {
//                    JSONObject objMsg = array.get(i).getLatestFamilyLeavMsg() obj.getJSONObject("latestFamilyLeavMsg");
//                    if (objMsg != null) {
//                        familyBean.familyMsg = "【" + objMsg.getString("cwName") + "】" + objMsg.getString("content");
//                    }
//                }
            familyList.add(familyBean);
        }
        return familyList;
    }

    public static ArrayList<MemBean> parserFamilyUsers(Context ctx, List<FamilyUser> array) {

        ArrayList<MemBean> familyMemList = new ArrayList<MemBean>();
        for (int i = 0; i < array.size(); i++) {
            MemBean memBean = new MemBean();
            memBean.memGroupID = array.get(i).getGroupId().toString();
            memBean.memID = array.get(i).getUserId().toString();
            memBean.memType = "3";
            memBean.studentID = array.get(i).getStudentId().toString();
            memBean.memName = array.get(i).getAlias();
            memBean.location = array.get(i).getLatestLocName();
            memBean.memHeadImgUrl = array.get(i).getHeadpicpath();
            memBean.location = array.get(i).getLatestLocName();
            memBean.phoneNum = array.get(i).getTelNum();
            familyMemList.add(memBean);
        }
        return familyMemList;
    }

//
//    public static ArrayList<FamilyClassBean> parserClassUsers(Context ctx, List<Object>  array) {
//
//        ArrayList<FamilyClassBean> classData = new ArrayList<FamilyClassBean>();
//        for (int i = 0; i < array.size();i++) {
//            ArrayList<MemBean> teachersData = null;
//                FamilyClassBean classBean = new FamilyClassBean();
//                classBean.type = "1";
//                classBean.id = array.get(i). obj.getString("id");
//                classBean.name = obj.getString("grade_name") + obj.getString("name");
//                JSONArray teachsArray = obj.getJSONArray("teachers");
//                teachersData = parserClassTeachers(ctx, teachsArray);
//                MemBean allStudent = new MemBean();
//                allStudent.memType = "1";
//                teachersData.add(0, allStudent);
//                if (teachersData != null) {
//                    classBean.getList().addAll(teachersData);
//                }
//                classData.add(classBean);
//        }
//        return classData;
//    }

    public static ArrayList<MemBean> parserClassTeachers(Context ctx, JSONArray array) {

        ArrayList<MemBean> teachsList = new ArrayList<MemBean>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                MemBean memBean = new MemBean();
                memBean.memType = "2";
                memBean.memName = obj.getString("teacher_name");
                memBean.memHeadImgUrl = obj.getString("headpicpath");
                memBean.memID = obj.getString("user_id");
                memBean.phoneNum = obj.getString("telNum");
                teachsList.add(memBean);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                log("parserClassTeachers---------JSONException:" + e.toString());
                e.printStackTrace();
                return null;
            }
        }
        return teachsList;
    }

    /**
     * [根据返回结果解析所有家庭成员及班级列表数据]<BR>
     * 根据返回结果解析所有家庭成员及班级列表数据
     *
     * @param context 上下文
     * @param result  下发数据
     * @return 是否解析成功
     */
    public static boolean updateAllFamilyList(Context context, String result) {
        JSONObject jason;
        try {
            jason = new JSONObject(result);
            log("---updateAllFamilyList-------" + result);
            JSONObject dataJson = jason.getJSONObject("data");// for new
            // JSONArray familyarray = jason.getJSONArray("data");// for old test
            JSONArray familyarray = dataJson.getJSONArray("members");
            updateFamilyList(context, familyarray);
            if (dataJson.has("classes"))// for old test
            {
                JSONArray classarray = dataJson.getJSONArray("classes");
                updateClassList(context, classarray);
            }
        } catch (JSONException e) {
            log("updateAllFamilyList---------JSONException:" + e.toString());
            return false;
        }
        return true;
    }

    /**
     * [根据返回结果解析家庭成员列表数据]<BR>
     * 根据返回结果解析家庭成员列表数据
     *
     * @param context 上下文
     * @param array   成员列表返回信息
     * @throws JSONException 异常
     */
    private static void updateFamilyList(Context context, JSONArray array)
            throws JSONException {
        ArrayList<FamilyBean> familyBeans = new ArrayList<FamilyBean>();
        String memberId = "";
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            FamilyBean familyBean = new FamilyBean();
            familyBean.headpicname = obj.getString("headpicname");
            familyBean.headpicpath = obj.getString("headpicpath");
            memberId = obj.getString("userId");
            familyBean.realname = obj.getString("userrealname");
            familyBean.memberid = memberId;
            familyBean.mMember_userId = memberId;
            familyBean.alias = obj.getString("alias");
            familyBean.mbCurrentUser = "no";
            familyBean.mStudentId = obj.getString("studentId");
            log(i + "----" + familyBean.mStudentId + "   "
                    + familyBean.realname + "  " + familyBean.alias);
            if (!familyBean.mStudentId.equals("")) {
                familyBean.mbStudent = true;
            }
            familyBean.mGroupId = obj.getString("groupId");
            Preferences preferences = Preferences.getInstance(context.getApplicationContext());
            if (preferences.getUserID().equals(memberId)) {
                familyBean.alias = context.getString(R.string.login_name_default);
                familyBean.mbCurrentUser = "yes";
                preferences.setGroupId(familyBean.mGroupId);
                preferences.setUserHeadName(familyBean.headpicname);
                preferences.setUserHeadPath(familyBean.headpicpath);
                preferences.setAccountHeadUrl(familyBean.headpicpath
                        + familyBean.headpicname);
                preferences.setRealName(familyBean.realname);
            } else {
                familyBeans.add(familyBean);
            }
        }
        // 数据库操作不需要
//        MemberDao dao = new MemberDao(context);
//        if (dao != null) {
//            dao.deleteAll();
//            dao.insert(familyBeans);
//            dao.close();
//        }
        setFamilyList(familyBeans);
    }

    /**
     * [根据下发数据解析封装班级信息]<BR>
     * 根据下发数据解析封装班级信息
     *
     * @param context 上下文
     * @param array   班级数据
     * @throws JSONException 异常
     */
    private static void updateClassList(Context context, JSONArray array)
            throws JSONException {
        Preferences preferences = Preferences.getInstance(context.getApplicationContext());
        String teacherId = preferences.getTeacherId();
        ArrayList<ClassBean> classList = new ArrayList<ClassBean>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            ClassBean classBean = new ClassBean();
            classBean.id = obj.getString("id");
            classBean.name = obj.getString("name");
            classBean.grade_id = obj.getString("grade_id");
            classBean.grade_name = obj.getString("grade_name");
            classBean.teacherid = teacherId;
            classList.add(classBean);
        }
        setClassList(classList);
    }

    /**
     * [日志输出]<BR>
     * 日志输出
     *
     * @param msg 日志内容
     */
    private static void log(String msg) {
        if (DEBUG) {
            TLog.Log(msg);
        }
    }
}
