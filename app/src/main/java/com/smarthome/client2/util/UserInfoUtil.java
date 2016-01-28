package com.smarthome.client2.util;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.bean.MemBean;
import com.smarthome.client2.bean.UserInfo;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.common.TLog;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.widget.MemberDao;

/**
 * [用户信息查询帮助类]<BR>
 * 用于处理用户信息查询等相关解析封装
 * @author Annie
 * @version [ODP Client R001C01LAI141, 2014年12月25日]
 */
public class UserInfoUtil
{
    /**
     * the default name of user head icon
     */
    public static final String USER_PICNAME_DEFAULT = "head.jpg";

    public static final String USER_FAMILY_PICNAME_DEFAULT = "familly.jpg";
    
    public static final int USER_INFO_MSG_ID = 0x0080;
    public static final int USER_INFO_ERROR_MSG_ID = 0x0081;

    /**
     * result code from InfoFragment to FamilyDetailActivity
     */
    public static final int INTENT_NICKNAME_RESULT = 1;

    /**
     * the key for nick name
     */
    public static final String INTENT_KEY_NICKNAME = "new_alias";

    /**
     * key of jason retcode
     */
    public static final String KEY_JASON_RETCODE = "retcode";

    /**
     * key of jason data
     */
    public static final String KEY_JASON_DATA = "data";

    /**
     * key of the UserInfo
     */
    public static final String KEY_USER_INFO = "UserInfo";

    /**
     * key of the userId
     */
    public static final String KEY_ID = "id";

    /**
     * key of the userId
     */
    public static final String KEY_USER_ID = "userId";

    /**
     * key of the memberId
     */
    public static final String KEY_MEMBER_ID = "acctid";

    /**
     * key of height
     */
    public static final String KEY_USER_HEIGHT = "height";

    /**
     * key of the weight
     */
    public static final String KEY_USER_WEIGHT = "weight";

    /**
     * key of the birthday
     */
    public static final String KEY_USER_BIRTHDAY = "birthday";

    /**
     * key of the telephone number
     */
    public static final String KEY_USER_TELNUM = "telnum";

    /**
     * key of the real name
     */
    public static final String KEY_USER_REALNAME = "userrealname";

    /**
     * key of the alias name
     */
    public static final String KEY_USER_ALIAS = "alias";

    /**
     * key of gender
     */
    public static final String KEY_USER_GENDER = "gender";

    /**
     * key of the device code
     */
    public static final String KEY_USER_DEVICECODE = "imei";

    /**
     * key of the device ID
     */
    public static final String KEY_USER_DEVICEID = "deviceId";

    /**
     * key of the path for user icon
     */
    public static final String KEY_USER_HEADPIC_PATH = "headpicpath";

    /**
     * key of the name for user icon
     */
    public static final String KEY_USER_HEADPIC_NAME = "headpicname";

    /**
     * key of the group ID
     */
    public static final String KEY_USER_GROUPID = "groupId";

    /**
     * key of the old device ID
     */
    public static final String KEY_OLD_DEVICEID = "devicecode";

    /**
     * key of the old phone number
     */
    public static final String KEY_OLD_PHONE = "phone";

    /**
     * 日志开关
     */
    private static final boolean DEBUG = true;

    /**
     * 根据网络返回解析用户信息<BR>
     * 根据网络返回解析用户信息--用于用户信息修改界面
     * @param result 网络返回
     * @param userInfo 用户信息
     * @return 用户信息
     */
    public static boolean parseToUserInfo(UserInfo resultUserInfo,String parseData)
    {
        try
        {
            JSONObject json = new JSONObject(parseData);
            JSONObject data = json.getJSONObject("data");
            if (!data.isNull(KEY_USER_HEIGHT))
            {
                resultUserInfo.setHeight(String.valueOf(data.getInt(KEY_USER_HEIGHT)));
            }
            if (!data.isNull(KEY_USER_WEIGHT))
            {
                resultUserInfo.setWeight(data.getString(KEY_USER_WEIGHT));
            }
            if (!data.isNull(KEY_USER_BIRTHDAY))
            {
                resultUserInfo.setBirthday(data.getString(KEY_USER_BIRTHDAY));
            }
            if (data.has(KEY_USER_TELNUM))
            {
                resultUserInfo.setTelnum(data.getString(KEY_USER_TELNUM));
            }
            if (data.has(KEY_USER_REALNAME))
            {
                resultUserInfo.setName(data.getString(KEY_USER_REALNAME));
            }
            else
            {
                resultUserInfo.setName(data.getString(KEY_USER_ALIAS));
            }
            if (data.has(KEY_USER_GROUPID)){
                resultUserInfo.setGroupId(data.getString(KEY_USER_GROUPID));
            }
            if (data.has(KEY_USER_GENDER))
            {
                resultUserInfo.setGender(data.getString(KEY_USER_GENDER));
            }
            if (data.has(KEY_USER_HEADPIC_PATH))
            {
                resultUserInfo.setHeadPicPath(data.getString(KEY_USER_HEADPIC_PATH));
            }
            if (data.has(KEY_USER_HEADPIC_NAME))
            {
                resultUserInfo.setHeadPicName(data.getString(KEY_USER_HEADPIC_NAME));
            }
            return true;
        }
        catch (JSONException e)
        {
            log("------parseToUserInfo------" + e.toString());
            return false;
        }
    }
    
    public static int parserUserDeviceType(String result){
    	
    	JSONObject jsonObj = null;
    	JSONArray userDeviceList = null;
        JSONObject dataJson;
        
        
		try {
			jsonObj = new JSONObject(result);
			dataJson = jsonObj.getJSONObject("data");
			userDeviceList = dataJson.getJSONArray("deviceList");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return 0;
		}
        //设备列表信息
		if (userDeviceList != null && userDeviceList.length() == 0){
			return 4;
		}
        if (userDeviceList != null){
        	for (int i = 0; i < userDeviceList.length(); i++)
            {
                try {
    				JSONObject obj = userDeviceList.getJSONObject(i);
    				String deviceType = obj.getString("devicetype");
    				return Integer.parseInt(deviceType);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				log("parserUserLinkTopDeviceInfo---------JSONException:" + e.toString());
    				e.printStackTrace();
    				return 0;
    			}
            }
        }
        return 0;
    }
    
    public static String[] parserUserDevice(String result){
    	
    	String[] retDeviceInfo = new String[5];
    	JSONObject jsonObj = null;
    	JSONArray userDeviceList = null;
        JSONObject dataJson;
        
		try {
			jsonObj = new JSONObject(result);
			dataJson = jsonObj.getJSONObject("data");
			userDeviceList = dataJson.getJSONArray("deviceList");
			if (userDeviceList == null){
				return null;
			}
		} catch (JSONException e2) {
			e2.printStackTrace();
			return null;
		}
        //设备列表信息
        if (userDeviceList != null){
        	for (int i = 0; i < userDeviceList.length(); i++)
            {
                try {
    				JSONObject obj = userDeviceList.getJSONObject(i);
    				String deviceType = obj.getString("devicetype");
    				if (deviceType.equals("6")){
    					String watchIDAccount = obj.getString("devicecode");
    					retDeviceInfo[0] = deviceType; 
    					retDeviceInfo[1] = watchIDAccount.split(":")[0];    					
    					retDeviceInfo[2] = obj.getString("mobilecode");
    					retDeviceInfo[3] = watchIDAccount.split(":")[1];
    					retDeviceInfo[4] = obj.getString("id");
    					return retDeviceInfo;
    				}else if (deviceType.equals("1") || deviceType.equals("2")){
    					retDeviceInfo[0] = deviceType;
    					retDeviceInfo[1] = obj.getString("devicecode");
    					retDeviceInfo[2] = obj.getString("mobilecode");
    					retDeviceInfo[4] = obj.getString("id");
    					return retDeviceInfo;
    				}
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				return null;
    			}
            }
        }
        return null;
    	
    }
     
    public static String[] parserUserLinkTopDeviceInfo(String result){
    	
    	String[] retDeviceInfo = new String[3];
    	JSONObject jsonObj = null;
    	JSONArray userDeviceList = null;
        JSONObject dataJson;
        
		try {
			jsonObj = new JSONObject(result);
			dataJson = jsonObj.getJSONObject("data");
			userDeviceList = dataJson.getJSONArray("deviceList");
		} catch (JSONException e2) {
			e2.printStackTrace();
			return null;
		}
        //设备列表信息
        if (userDeviceList != null){
        	for (int i = 0; i < userDeviceList.length(); i++)
            {
                try {
    				JSONObject obj = userDeviceList.getJSONObject(i);
    				String deviceType = obj.getString("devicetype");
    				if (deviceType.equals("6")){
    					String watchIDAccount = obj.getString("devicecode");
    					retDeviceInfo[0] = watchIDAccount.split(":")[0];
    					retDeviceInfo[1] = watchIDAccount.split(":")[1];
    					retDeviceInfo[2] = obj.getString("mobilecode");
    					return retDeviceInfo;
    				}
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				return null;
    			}
            }
        }
        return null;
    }
 
    /**
     * 根据网络返回解析用户详细信息<BR>
     * 根据网络返回解析用户详细信息--用于详情界面
     * @param result 网络返回
     * @param userInfo 用户信息
     * @return 用户信息
     */
    public static UserInfo parseToUserInfoDetail(String result,
            UserInfo userInfo)
    {
        try
        {
            JSONObject json = new JSONObject(result);
            JSONObject data = json.getJSONObject("data");

            if (!data.isNull(KEY_USER_HEIGHT))
            {
                userInfo.setHeight(String.valueOf(data.getInt(KEY_USER_HEIGHT)));
            }
            if (!data.isNull(KEY_USER_WEIGHT))
            {
                userInfo.setWeight(data.getString(KEY_USER_WEIGHT));
            }
            if (!data.isNull(KEY_USER_BIRTHDAY))
            {
                userInfo.setBirthday(data.getString(KEY_USER_BIRTHDAY));
            }
            if (data.has(KEY_USER_TELNUM))
            {
                userInfo.setTelnum(data.getString(KEY_USER_TELNUM));
            }
            if (data.has(KEY_USER_ALIAS))
            {
                userInfo.setName(data.getString(KEY_USER_ALIAS));
            }
            if ((userInfo.isCurrentUser() || !data.has(KEY_USER_ALIAS))
                    && data.has(KEY_USER_REALNAME))
            {
                userInfo.setName(data.getString(KEY_USER_REALNAME));
            }
            if (data.has(KEY_USER_GENDER))
            {
                userInfo.setGender(data.getString(KEY_USER_GENDER));
            }
            if (data.has(KEY_USER_HEADPIC_PATH))
            {
                userInfo.setHeadPicPath(data.getString(KEY_USER_HEADPIC_PATH));
            }
            if (data.has(KEY_USER_HEADPIC_NAME))
            {
                userInfo.setHeadPicName(data.getString(KEY_USER_HEADPIC_NAME));
            }
            return userInfo;
        }
        catch (JSONException e)
        {
            log("------parseToUserInfoDetail------" + e.toString());
            return null;
        }
    }

    /**
     * [刷新widget，更新头像地址，图片缓存]<BR>
     * 刷新widget，更新头像地址，图片缓存,并返回新的头像路径与名称
     * @param context 上下文
     * @param jsonStr 网络返回结果
     * @param userid 用户userId
     * @return 用户信息,其中仅包括新的头像路径与名称
     */
    public static UserInfo refreshWidget(Context context, String jsonStr,
            String userid)
    {
        UserInfo userInfo = null;
        try
        {
            JSONObject jObject = new JSONObject(jsonStr);
            String code;
            if (jObject.has(UserInfoUtil.KEY_JASON_RETCODE))
            {
                code = jObject.getString(UserInfoUtil.KEY_JASON_RETCODE);
                if (!TextUtils.isEmpty(code)
                        && Integer.parseInt(code) == HttpStatus.SC_OK
                        && jObject.has(UserInfoUtil.KEY_JASON_DATA))
                {
                    jObject = jObject.getJSONObject(UserInfoUtil.KEY_JASON_DATA);
                    if (jObject.has(UserInfoUtil.KEY_USER_HEADPIC_PATH)
                            && jObject.has(UserInfoUtil.KEY_USER_HEADPIC_NAME))
                    {
                        userInfo = new UserInfo();
                        String headPath = jObject.getString(UserInfoUtil.KEY_USER_HEADPIC_PATH);
                        userInfo.setHeadPicPath(headPath);
                        String headName = jObject.getString(UserInfoUtil.KEY_USER_HEADPIC_NAME);
                        userInfo.setHeadPicName(headName);
                        MemberDao dao = new MemberDao(
                                SmartHomeApplication.getInstance());
                        dao.update(userid, headPath, headName);
                        dao.close();
                        Intent intent = new Intent(
                                FsConstants.WIDGET_MEMBERS_CHANGE);
                        context.sendBroadcast(intent);
                    }
                }
            }
        }
        catch (JSONException e)
        {
            log("------refreshWidget------" + e.toString());
        }
        return userInfo;
    }

    /**
     * 通过用户userId获取用户信息<BR>
     * 访问网络获取用户信息
     * @param context 上下文
     * @param userid 用户UserId
     */
    public static String getUserInfoByUserId(Context context, String userid)
    {
        String result = "";
        JSONObject requestObject = new JSONObject();
        try
        {
            requestObject.put(KEY_USER_ID, userid);
            final RequestResult requestResult = new RequestResult();
            HttpUtil.postSmartRequest(requestObject,
                    Constants.GET_USER_SYNOPSIS,
                    requestResult,
                    context);
            int sc = requestResult.getCode();
            if (sc == HttpStatus.SC_OK)
            {
                result = requestResult.getResult();                
            }
            else
            {
                result = String.valueOf(sc);                
            }
        }
        catch (JSONException e)
        {
            log("getUserInfoByUserId---------JSONException:" + e.toString());
        }
        return result;
    }

    /**
     * 通过用户memberId获取用户信息<BR>
     * 访问网络获取用户信息
     * @param context 上下文
     * @param memberId 用户memberId
     */
    public static String getUserInfoByMemberId(Context context, String memberId)
    {
        String result = "";
        JSONObject requestObject = new JSONObject();
        try
        {
            requestObject.put(KEY_MEMBER_ID, memberId);
            final RequestResult requestResult = new RequestResult();
            HttpUtil.postSmartRequest(requestObject,
                    Constants.GET_USER_INFO,
                    requestResult,
                    context);
            int sc = requestResult.getCode();
            if (sc == HttpStatus.SC_OK)
            {
                result = requestResult.getResult();
                log("getUserInfoByMemberId---------result:" + result);
            }
            else
            {
                result = String.valueOf(sc);
                log("getUserInfoByMemberId---------code:" + sc);
            }
        }
        catch (JSONException e)
        {
            log("getUserInfoByMemberId---------JSONException:" + e.toString());
        }
        return result;
    }

    /**
     * [新增老人机]<BR>
     * 新增老人机
     * @param context 上下文
     * @param deviceId 老人机设备ID
     * @param phoneNumber 老人机监护号码
     * @param nickname 老人昵称
     * @return 返回结果
     */
    public static String doAddOld(Context context, String action,
            String deviceId, String phoneNumber, String nickname)
    {
        String result = "";
        JSONObject requestObject = new JSONObject();
        try
        {
            requestObject.put(KEY_OLD_DEVICEID, deviceId);
            requestObject.put(KEY_OLD_PHONE, phoneNumber);
            requestObject.put(KEY_USER_ALIAS, nickname);
            log("--doAddOld--" + requestObject);
            final RequestResult requestResult = new RequestResult();
            HttpUtil.postSmartRequest(requestObject,
                    action,
                    requestResult,
                    context);
            int sc = requestResult.getCode();
            if (sc == HttpStatus.SC_OK)
            {
                result = requestResult.getResult();
                log("doAddOld---------result:" + result);
            }
            else
            {
                result = String.valueOf(sc);
                log("doAddOld---------code:" + sc);
            }
        }
        catch (JSONException e)
        {
            log("doAddOld---------JSONException:" + e.toString());
        }
        return result;
    }

    /**
     * [日志输出]<BR>
     * 日志输出
     * @param msg 日志内容
     */
    private static void log(String msg)
    {
        if (DEBUG)
        {
            TLog.Log(msg);
        }
    }
}
