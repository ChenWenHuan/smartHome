package com.smarthome.client2.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.smarthome.client2.activity.LoginActivity_sm;
import com.smarthome.client2.common.Constants;

public class Preferences
{
    private static final String SHARE_PREFERENCE_NAME = "smarthome_user";

    public static Preferences sf = null;

    private Context ctx;

    private SharedPreferences sp = null;

    private Editor editor;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private Preferences(Context ctx)
    {
        this.ctx = ctx;
        sp = ctx.getSharedPreferences(SHARE_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static Preferences getInstance(Context ctx)
    {
        if (null == sf)
        {
            sf = new Preferences(ctx);
        }
        return sf;
    }

    // /////////////////////////UserID///////////////////////////////////////
    public void setUserID(String userID)
    {
        if (sp.contains("userID"))
        {
            editor.remove("userID");
        }
        editor.putString("userID", userID);
        editor.commit();
    }

    public String getUserID()
    {
        return sp.getString("userID", "");
    }
    
    public void setOpenID(String openID)
    {
        if (sp.contains("openID"))
        {
            editor.remove("openID");
        }
        editor.putString("openID", openID);
        editor.commit();
    }

    public String getOpenID()
    {
        return sp.getString("openID", "");
    }
    
    public void setUserTelNum(String telNum)
    {
        if (sp.contains("telnum"))
        {
            editor.remove("telnum");
        }
        editor.putString("telnum", telNum);
        editor.commit();
    }

    public String getUserTelNum()
    {
        return sp.getString("telnum", "");
    }

    // /////////////////////////是否老师///////////////////////////////////////
    public void setIsTeacher(boolean isTeacher)
    {
        editor.putBoolean("isTeacher", isTeacher);
        editor.commit();
    }

    public boolean getIsTeacher()
    {
        return sp.getBoolean("isTeacher", false);
    }

    /** 获取上一次的软件版本code */
    public int getLastVersionCode()
    {
        return sp.getInt("lastVersionCode", 0);
    }

    public void setLastVersionCode(int lastVersionCode)
    {
        if (sp.contains("lastVersionCode"))
        {
            editor.remove("lastVersionCode");
        }

        editor.putInt("lastVersionCode", lastVersionCode);
        editor.commit();
    }

    // ////////////////////////屏幕数据//////////////////////////////////
    public void setScreenH(int screenH)
    {
        editor.putInt("screenH", screenH);
        editor.commit();
    }

    public int getScreenH()
    {
        return sp.getInt("screenH", 0);
    }

    public void setScreenW(int screenW)
    {
        editor.putInt("screenW", screenW);
        editor.commit();
    }

    public int getScreenW()
    {
        return sp.getInt("screenW", 0);
    }

    public void setScreenDen(float density)
    {
        editor.putFloat("density", density);
        editor.commit();
    }

    public float getScreenDen()
    {
        return sp.getFloat("density", 0);
    }

    // ////////////////////////////iemi和imsi/////////////////////////////////////

    public void setUserIMEI(String imei)
    {
        editor.putString("iemi", imei);
        editor.commit();
    }

    public String getUserIMEI()
    {
        return sp.getString("iemi", "");
    }

    public void setUserIMSI(String imsi)
    {
        editor.putString("imsi", imsi);
        editor.commit();
    }

    public String getUserIMSI()
    {
        return sp.getString("imsi", "");
    }

    public void setPhoneIMEIAndIMSI(String imei, String imsi)
    {
        setUserIMEI(imei);
        setUserIMSI(imsi);
    }

    /** 添加一个 新的大小进到缓存总数里面 */
    public void addSDsize(long addSize)
    {
        editor.putLong("SD_CARD_SIZE", getSDcardSize() + addSize);
        editor.commit();
    }

    /** 设置SD卡的已经存储的大小 */
    public void setSDcardSize(long size)
    {
        editor.putLong("SD_CARD_SIZE", size);
        editor.commit();
    }

    /** 获取SD卡的大小 */
    public long getSDcardSize()
    {
        return sp.getLong("SD_CARD_SIZE", 0L);
    }

    // ///////////////////////nickname///////////////////////////////////
    public void setNickName(String nickname)
    {
        editor.putString("nickname", nickname);
        editor.commit();
    }

    public String getNickName()
    {
        return sp.getString("nickname", "noname");
    }

    // ///////////////////////nickname///////////////////////////////////

    // ///////////////////////password///////////////////////////////////
    public void setPassWord(String password)
    {
        editor.putString("password", password);
        editor.commit();
    }

    public String getPassWord()
    {
        return sp.getString("password", "");
    }

    // ///////////////////////password///////////////////////////////////

    // ///////////////////////username///////////////////////////////////
    public void setUserName(String username)
    {
        editor.putString("username", username);
        editor.commit();
    }

    public String getUserName()
    {
        return sp.getString("username", "");
    }

    // ///////////////////////username///////////////////////////////////

    // ///////////////////////email///////////////////////////////////
    public void setEmail(String email)
    {
        editor.putString("email", email);
        editor.commit();
    }

    public String getEmail()
    {
        return sp.getString("email", "");
    }

    // ///////////////////////email///////////////////////////////////
    // ///////////////////////timeline day///////////////////////////////////
    public void setTimeLineDay(String day)
    {
        editor.putString("timelineday", day);
        editor.commit();
    }

    public String getTimeLineDay()
    {
        return sp.getString("timelineday", "");
    }

    // ///////////////////////timeline day ///////////////////////////////////
    
 // ///////////////////////timeline index///////////////////////////////////
    public void setTimeLineIndex(String deviceid,int  index)
    {
        editor.putInt(deviceid + "timelineindex", index);
        editor.commit();
    }

    public int getTimeLineIndex(String deviceid)
    {
        return sp.getInt(deviceid + "timelineindex", 0);
    }

    // ///////////////////////timeline index ///////////////////////////////////
    
 // ///////////////////////watch device info///////////////////////////////////
    public void setWatchDeviceInfo(String deviceid, String  deviceinfo)
    {
        editor.putString(deviceid, deviceinfo);
        editor.commit();
    }

    public String getWatchDeviceInfo(String deviceid)
    {
        return sp.getString(deviceid, "");
    }

    // ///////////////////////user loin info ///////////////////////////////////
    public void setUserNameBaseUrl(String userName, String baseUrl){
        editor.putString(userName, baseUrl);
        editor.commit();
    }
    
    public String getUserBaseUrl(String userName)
    {
        return sp.getString(userName, "");
    }
    
    
    public void setWatchBindID(String deviceid)
    {
        editor.putString("watchbindid", deviceid);
        editor.commit();
    }

    public String getWatchBindID( )
    {
        return sp.getString("watchbindid", "");
    }

    public void setWatchTelNum(String watchTelNum)
    {
        editor.putString("watchtelnum", watchTelNum);
        editor.commit();
    }

    public String getWatchTelNum( )
    {
        return sp.getString("watchtelnum", "");
    }
    
    public void setWatchBindAkey(String akey)
    {
        editor.putString("watchbindakey", akey);
        editor.commit();
    }

    public String getWatchBindAkey( )
    {
        return sp.getString("watchbindakey", "");
    }

    public void setOldPhoneTelNum(String oldPhoneTelNum)
    {
        editor.putString("oldphonetelnum", oldPhoneTelNum);
        editor.commit();
    }

    public String getOldPhoneTelNum( )
    {
        return sp.getString("oldphonetelnum", "");
    }

    public void setOldPhoneImei(String oldPhoneImei)
    {
        editor.putString("oldphoneimei", oldPhoneImei);
        editor.commit();
    }

    public String getOldPhoneImei( )
    {
        return sp.getString("oldphoneimei", "");
    }

    public void setStudentPhoneImei(String studentPhoneImei)
    {
        editor.putString("studentphoneimei", studentPhoneImei);
        editor.commit();
    }

    public String getStudentPhoneImei( )
    {
        return sp.getString("studentphoneimei", "");
    }

    // ///////////////////////email password///////////////////////////////////
    public void setEmailPassWord(String emailPassWord)
    {
        editor.putString("emailPassWord", emailPassWord);
        editor.commit();
    }

    public String getEmailPassWord()
    {
        return sp.getString("emailPassWord", "");
    }

    // ///////////////////////email///////////////////////////////////

    // ///////////////////////gender///////////////////////////////////
    public void setGender(int gender)
    {
        editor.putInt("gender", gender);
        editor.commit();
    }

    public int getGender()
    {
        return sp.getInt("gender", 1);
    }

    // ///////////////////////gender///////////////////////////////////

    // ///////////////////////year///////////////////////////////////
    public void setYear(int year)
    {
        editor.putInt("year", year);
        editor.commit();
    }

    public int getYear()
    {
        return sp.getInt("year", 1987);
    }

    // ///////////////////////year///////////////////////////////////

    // ///////////////////////weight///////////////////////////////////
    public void setWeight(float weight)
    {
        editor.putFloat("weight", weight);
        editor.commit();
    }

    public float getWeight()
    {
        return sp.getFloat("weight", 70F);
    }

    // ///////////////////////weight///////////////////////////////////

    // ///////////////////////height///////////////////////////////////
    public void setHeight(int height)
    {
        editor.putInt("height", height);
        editor.commit();
    }

    public int getHeight()
    {
        return sp.getInt("height", 170);
    }

    // ///////////////////////height///////////////////////////////////

    // ///////////////////////sleeptime///////////////////////////////////
    public void setSleeptime(int sleeptime)
    {
        editor.putInt("sleeptime", sleeptime);
        editor.commit();
    }

    public int getSleeptime()
    {
        return sp.getInt("sleeptime", 480);
    }

    // ///////////////////////sleeptime///////////////////////////////////

    // ///////////////////////cal///////////////////////////////////
    public void setCal(int cal)
    {
        if (cal < 0)
        {
            cal = 0;
        }
        editor.putInt("cal", cal);
        editor.commit();
    }

    public int getCal()
    {
        return sp.getInt("cal", 10000);
    }

    // ///////////////////////cal///////////////////////////////////

    // ///////////////////////setAllGoal///////////////////////////////////
    public void setAllGoal(String allGoal)
    {
        editor.putString("allGoal", allGoal);
        editor.commit();
    }

    public String getAllGoal()
    {
        return sp.getString("allGoal", "");
    }

    // ///////////////////////setAllGoal///////////////////////////////////

    // ///////////////////////setAllBluetoothDevice///////////////////////////////////
    public void setAllBluetoothDevice(String allBluetoothDevice)
    {
        editor.putString("allBluetoothDevice", allBluetoothDevice);
        editor.commit();
    }

    public String getAllBluetoothDevice()
    {
        return sp.getString("allBluetoothDevice", "");
    }

    // ///////////////////////setAllBluetoothDevice///////////////////////////////////

    // ////////////////////////固件版本/////////////////////////////
    public void setDeviceInfo(long deviceInfo)
    {
        editor.putLong("DeviceInfo", deviceInfo);
        editor.commit();
    }

    public Long getDeviceInfo()
    {
        return sp.getLong("DeviceInfo", 0);
    }

    // ////////////////////////固件版本/////////////////////////////

    // ////////////////////////是否注销/////////////////////////////
    public void setIsLogout(boolean isLogout)
    {
        editor.putBoolean("isLogout", isLogout);
        editor.commit();
    }

    public boolean getIsLogout()
    {
        return sp.getBoolean("isLogout", true);
    }

    // ////////////////////////是否注销/////////////////////////////

    // ////////////////////////autologin/////////////////////////////
    public void setAutoLogin(boolean autologin)
    {
        editor.putBoolean("autologin", autologin);
        editor.commit();
    }

    public boolean getAutoLogin()
    {
        return sp.getBoolean("autologin", false);
    }

    // ////////////////////////autologin/////////////////////////////

    // ////////////////////////设置上一次同步时间///////////////////////////////////
    public long getLastSyncTime()
    {
        return sp.getLong("lastSyncTime", 0);
    }

    public void setLastSyncTime(long lastSyncTime)
    {
        editor.putLong("lastSyncTime", lastSyncTime);
        editor.commit();
    }

    // ////////////////////////设置上一次同步时间///////////////////////////////////

    // ////////////////////////设置上一次运动向导显示版本///////////////////////////////////
    public long getLastSportsIntroduceVersionCode()
    {
        return sp.getLong("lastSportsIntroduceVersionCode", 0);
    }

    public void setLastSportsIntroduceVersionCode(
            long lastSportsIntroduceVersionCode)
    {
        editor.putLong("lastSportsIntroduceVersionCode",
                lastSportsIntroduceVersionCode);
        editor.commit();
    }

    // ////////////////////////设置上一次运动向导显示版本///////////////////////////////////

    // ////////////////////////设置上一次睡眠向导显示版本///////////////////////////////////
    public long getLastSleepIntroduceVersionCode()
    {
        return sp.getLong("lastSleepIntroduceVersionCode", 0);
    }

    public void setLastSleepIntroduceVersionCode(
            long lastSleepIntroduceVersionCode)
    {
        editor.putLong("lastSleepIntroduceVersionCode",
                lastSleepIntroduceVersionCode);
        editor.commit();
    }

    // ////////////////////////设置上一次睡眠向导显示版本///////////////////////////////////

    // ///////////////////////frienduserId///////////////////////////////////
    public void setFriendUserId(String friendUserId)
    {
        editor.putString("friendUserId", friendUserId);
        editor.commit();
    }

    public String getFriendUserId()
    {
        return sp.getString("friendUserId", "");
    }

    // ///////////////////////userId///////////////////////////////////

    // ////////////////////////isThirdPartLogin/////////////////////////////
    public void setLoginState(int login_state)
    {
        editor.putInt("login_state", login_state);
        editor.commit();
    }

    public int getLoginState()
    {
        // 1不是第三方登录,2表示QQ，3表示微博
        return sp.getInt("login_state", Constants.LOGIN_LOCAL_STATE);
    }

    // ////////////////////////isThirdPartLogin/////////////////////////////

    // ////////////////////////qq openid/////////////////////////////
    public void setQQ_Openid(String qq_Openid)
    {
        editor.putString("qq_Openid", qq_Openid);
        editor.commit();
    }

    public String getQQ_Openid()
    {
        // 1不是第三方登录,2表示QQ，3表示微博
        return sp.getString("qq_Openid", "");
    }

    // ////////////////////////widget中第几个家庭成员///////////////////////////////////
    public int getIndexOfFamliyInWiget()
    {
        return sp.getInt("indexOfFamliyInWiget", 0);
    }

    public void setIndexOfFamliyInWiget(int indexOfFamliyInWiget)
    {
        editor.putInt("indexOfFamliyInWiget", indexOfFamliyInWiget);
        editor.commit();
    }

    // ////////////////////////////////亲情号码绑定/////////////////////////////////////////////
    public String getRelPhoneNumberBinder()
    {
        return sp.getString("relPhoneNumberBinder", "");
    }

    public void setRelPhoneNumberBinder(String code)
    {
        editor.putString("relPhoneNumberBinder", code);
        editor.commit();
    }

    // ////////////////////////////////亲情号码绑定/////////////////////////////////////////////

    // ////////////////////////////////SOS号码绑定////////////////////////////////////////
    public String getSOSPhoneNumberBinder()
    {
        return sp.getString("sosPhoneNumberBinder", "");
    }

    public void setSOSPhoneNumberBinder(String code)
    {
        editor.putString("sosPhoneNumberBinder", code);
        editor.commit();
    }

    // ////////////////////////////////SOS号码绑定////////////////////////////////////////

    // ////////////////////////获取手环地址///////////////////////////////////
    public String getWristAddress()
    {
        return sp.getString("wristAddress", "");
    }

    public void setWristAddress(String wristAddress)
    {
        editor.putString("wristAddress", wristAddress);
        editor.commit();
    }

    public String getWristAddressList()
    {
        return sp.getString("wristAddressList", "");
    }

    public void setWristAddressList(String wristAddressList)
    {
        editor.putString("wristAddressList", wristAddressList);
        editor.commit();
    }

    public String getWristLastSync()
    {
        return sp.getString("wristLastSync", "");
    }

    public void setWristLastSync(String wristLastSync)
    {
        editor.putString("wristLastSync", wristLastSync);
        editor.commit();
    }

    // ////////////////////////获取手环地址///////////////////////////////////

    // ////////////////////////获取入睡时间///////////////////////////////////
    public String getFallSleepTime()
    {
        return sp.getString("fallSleepTime", "22:30");
    }

    public void setFallSleepTime(String fallSleepTime)
    {
        editor.putString("fallSleepTime", fallSleepTime);
        editor.commit();
    }

    public String getFallSleepDateTime()
    {
        return sp.getString("fallSleepDateTime", sdf.format(new Date())
                + " 22:30");
    }

    public void setFallSleepDateTime(String fallSleepDateTime)
    {
        editor.putString("fallSleepDateTime", fallSleepDateTime);
        editor.commit();
    }

    // ////////////////////////获取入睡时间///////////////////////////////////

    // ////////////////////////////////记住密码/////////////////////////////////////////////
    public String getRememberKeyStatus()
    {
        return sp.getString("remberkeystatus", "");
    }

    public void setRememberKeyStatus(String status)
    {
        editor.putString("remberkeystatus", status);
        editor.commit();
    }

    // ////////////////////////////////记住密码/////////////////////////////////////////////

    // ////////////////////////////////免打扰时段/////////////////////////////////////////////
    public String getDisturbTime()
    {
        return sp.getString("disturbTime", "");
    }

    public void setDisturbTime(String time)
    {
        editor.putString("disturbTime", time);
        editor.commit();
    }

    // ////////////////////////////////免打扰时段/////////////////////////////////////////////

    // ////////////////////////////////免打扰时段/////////////////////////////////////////////
    public String getAlarmTime()
    {
        return sp.getString("alarmTime", "");
    }

    public void setAlarmTime(String time)
    {
        editor.putString("alarmTime", time);
        editor.commit();
    }

    // ////////////////////////////////免打扰时段/////////////////////////////////////////////
    // ////////////////////////////////家庭成员/////////////////////////////////////////////
    public String getFamilyUsers()
    {
        return sp.getString("familyUsers", "");
    }

    public void setFamilyUsers(String familyUsers)
    {
        editor.putString("familyUsers", familyUsers);
        editor.commit();
    }

    // ////////////////////////////////家庭成员/////////////////////////////////////////////

    // ////////////////////////////////低电量/////////////////////////////////////////////
    public boolean getLowPower()
    {
        return sp.getBoolean("lowPower", false);
    }

    public void setLowPower(boolean lowPower)
    {
        editor.putBoolean("lowPower", lowPower);
        editor.commit();
    }

    // ////////////////////////////////低电量/////////////////////////////////////////////

    // ////////////////////////////////deviceId////////////////////////////////////
    public int getDeviceId()
    {
        return sp.getInt("deviceId", 0);
    }

    public void setDeviceId(int deviceId)
    {
        editor.putInt("deviceId", deviceId);
        editor.commit();
    }

    // ////////////////////////////////deviceId////////////////////////////////////

    // ////////////////////////////////familyUserId////////////////////////////////
    public int getFamilyUserId()
    {
        return sp.getInt("familyUserId", 0);
    }

    public void setFamilyUserId(int familyUserId)
    {
        editor.putInt("familyUserId", familyUserId);
        editor.commit();
    }

    // ////////////////////////////////familyUserId////////////////////////////////

    // ////////////////////////////////contact////////////////////////////////
    public String getContact()
    {
        return sp.getString("contact", "");
    }

    public void setContact(String contact)
    {
        editor.putString("contact", contact);
        editor.commit();
    }

    // ////////////////////////////////contact////////////////////////////////
    // ////////////////////////////////geofence////////////////////////////////
    public String getGeofence()
    {
        return sp.getString("geofence", "");
    }

    public void setGeofence(String geofence)
    {
        editor.putString("geofence", geofence);
        editor.commit();
    }

    // ////////////////////////////////geofence////////////////////////////////

    // ////////////////////////////////lastLocation////////////////////////////////
    public String getLastLocation()
    {
        return sp.getString("lastLocation", "");
    }

    public void setLastLocation(String lastLocation)
    {
        editor.putString("lastLocation", lastLocation);
        editor.commit();
    }

    // ////////////////////////////////lastLocation////////////////////////////////

    // ////////////////////////////////interval////////////////////////////////
    public int getInterval()
    {
        return sp.getInt("interval", 0);
    }

    public void setInterval(int interval)
    {
        editor.putInt("interval", interval);
        editor.commit();
    }

    // ////////////////////////////////interval////////////////////////////////

    // ////////////////////////////////silenceStatus////////////////////////////////
    public int getSilenceStatus()
    {
        return sp.getInt("silenceStatus", 0);
    }

    public void setSilenceStatus(int silenceStatus)
    {
        editor.putInt("silenceStatus", silenceStatus);
        editor.commit();
    }

    // ////////////////////////////////silenceStatus////////////////////////////////

    // ////////////////////////////////whiteType////////////////////////////////
    public int getWhiteType()
    {
        return sp.getInt("whiteType", 0);
    }

    public void setWhiteType(int whiteType)
    {
        editor.putInt("whiteType", whiteType);
        editor.commit();
    }

    // ////////////////////////////////whiteType////////////////////////////////

    // ////////////////////////////////alarmClockStatus////////////////////////////////
    public int getAlarmClockStatus()
    {
        return sp.getInt("alarmClockStatus", 0);
    }

    public void setAlarmClockStatus(int alarmClockStatus)
    {
        editor.putInt("alarmClockStatus", alarmClockStatus);
        editor.commit();
    }

    // ////////////////////////////////alarmClockStatus////////////////////////////////

    // ////////////////////////////////currPower////////////////////////////////
    public int getCurrPower()
    {
        return sp.getInt("currPower", 0);
    }

    public void setCurrPower(int currPower)
    {
        editor.putInt("currPower", currPower);
        editor.commit();
    }

    // ////////////////////////////////currPower////////////////////////////////

    // ////////////////////////////////fenceStatus////////////////////////////////
    public int getFenceStatus()
    {
        return sp.getInt("fenceStatus", 0);
    }

    public void setFenceStatus(int fenceStatus)
    {
        editor.putInt("fenceStatus", fenceStatus);
        editor.commit();
    }

    // ////////////////////////////////fenceStatus////////////////////////////////

    // ////////////////////////////////OCAlarm////////////////////////////////
    public int getOCAlarm()
    {
        return sp.getInt("OCAlarm", 0);
    }

    public void setOCAlarm(int OCAlarm)
    {
        editor.putInt("OCAlarm", OCAlarm);
        editor.commit();
    }

    // ////////////////////////////////OCAlarm////////////////////////////////

    // ////////////////////////////////deviceType////////////////////////////////
    public String getDeviceType()
    {
        return sp.getString("deviceType", "");
    }

    public void setDeviceType(String deviceType)
    {
        editor.putString("deviceType", deviceType);
        editor.commit();
    }

    // ////////////////////////////////deviceType////////////////////////////////

    // ////////////////////////////////localDeviceRelation////////////////////////////////
    //userId-deviceAddr,userId-deviceAddr
    public String getLocalDeviceRelation()
    {
        return sp.getString("localDeviceRelation", "");
    }

    public void setLocalDeviceRelation(String localDeviceRelation)
    {
        editor.putString("localDeviceRelation", localDeviceRelation);
        editor.commit();
    }

    // ////////////////////////////////localDeviceRelation////////////////////////////////

    // ////////////////////////set teacher id//////////////////////////////////
    public void setTeacherId(String teacherId)
    {
        editor.putString("teacherId", teacherId);
        editor.commit();
    }

    public String getTeacherId()
    {
        return sp.getString("teacherId", "0");
    }

    public void setHeadPath(String headPath)
    {
        editor.putString("headPath", headPath);
        editor.commit();
    }

    public String getHeadPath()
    {
        return sp.getString("headPath", "");
    }

    public void setHeadName(String headName)
    {
        editor.putString("headName", headName);
        editor.commit();
    }

    public String getHeadName()
    {
        return sp.getString("headName", "");
    }

    public void setToken(String token)
    {
        editor.putString("token", token);
        editor.commit();
    }

    public String getToken()
    {
        return sp.getString("token", "");
    }

    public void setHasNewMessage(boolean hasMessage)
    {
        editor.putBoolean("hasMessage", hasMessage);
        editor.commit();
    }

    public boolean getHasNewMessage()
    {
        return sp.getBoolean("hasMessage", false);
    }

    public void setRealName(String name)
    {
        editor.putString("realName", name);
        editor.commit();
    }

    public String getRealName()
    {
        return sp.getString("realName", "");
    }

    public void setSosNum(int sosNum)
    {
        editor.putInt("sosNum", sosNum);
        editor.commit();
    }

    public int getSosNum()
    {
        return sp.getInt("sosNum", 1);
    }

    public void setGPRSNum(String gprsNum)
    {
        editor.putString("gprsNum", gprsNum);
        editor.commit();
    }

    public String getGPRSNum()
    {
        return sp.getString("gprsNum", "");
    }

    public void setDisruptNum(String disruptNum)
    {
        editor.putString("disruptNum", disruptNum);
        editor.commit();
    }

    public String getDisruptNum()
    {
        return sp.getString("disruptNum", "");
    }

    /**
     * 记录登录账户的完整头像地址
     * 
     * @param url
     */
    public void setAccountHeadUrl(String url)
    {
        editor.putString("accountHead", url);
        editor.commit();
    }

    /**
     * 获取登录账户的完整头像地址
     * 
     * @return
     */
    public String getAccountHeadUrl()
    {
        return sp.getString("accountHead", "");
    }

    public void setHasFence(boolean hasFence)
    {
        editor.putBoolean("hasFence", hasFence);
        editor.commit();
    }

    public boolean getHasFence()
    {
        return sp.getBoolean("hasFence", false);
    }

    //    public void setRefreshFamilyDetail(boolean refreshFamilyDetail)
    //    {
    //        editor.putBoolean("refreshFamilyDetail", refreshFamilyDetail);
    //        editor.commit();
    //    }
    //
    //    public boolean getRefreshFamilyDetail()
    //    {
    //        return sp.getBoolean("refreshFamilyDetail", false);
    //    }

    public void setHasTimeCard(boolean timeCard)
    {
        editor.putBoolean("timeCard", timeCard);
        editor.commit();
    }

    public boolean getHasTimeCard()
    {
        return sp.getBoolean("timeCard", false);
    }

    public String getUserTelPhone()
    {
        return sp.getString("userTelPhone", "");
    }

    public void setUserTelPhone(String userTelPhone)
    {
        editor.putString("userTelPhone", userTelPhone);
        editor.commit();
    }

    public void setUserHeadPath(String userHeadPath)
    {
        editor.putString("userHeadPath", userHeadPath);
        editor.commit();
    }

    public String getUserHeadPath()
    {
        return sp.getString("userHeadPath", "");
    }

    public void setUserHeadName(String userHeadName)
    {
        editor.putString("userHeadName", userHeadName);
        editor.commit();
    }

    public String getUserHeadName()
    {
        return sp.getString("userHeadName", "");
    }

    public void setCameraPicPath(String jpgPath)
    {
        editor.putString("cameraJpgPath", jpgPath);
        editor.commit();
    }

    public String getCameraPicPath()
    {
        return sp.getString("cameraJpgPath", "");
    }

    public void setTimeCardCode(String timeCardCode)
    {
        editor.putString("timeCardCode", timeCardCode);
        editor.commit();
    }

    public String getTimeCardCode()
    {
        return sp.getString("timeCardCode", "");
    }

    public void setStudentCode(String studentCode)
    {
        editor.putString("studentCode", studentCode);
        editor.commit();
    }

    public String getStudentCode()
    {
        return sp.getString("studentCode", "");
    }

    public void setOldCode(String oldCode)
    {
        editor.putString("oldCode", oldCode);
        editor.commit();
    }

    public String getOldCode()
    {
        return sp.getString("oldCode", "");
    }

    public int getNongpsIntervalStatus()
    {
        return sp.getInt("nongpsInterval", 0);
    }

    public void setNongpsIntervalStatus(int nongpsInterval)
    {
        editor.putInt("nongpsInterval", nongpsInterval);
        editor.commit();
    }

    public int getGpsStatus()
    {
        return sp.getInt("gpsStatus", 0);
    }

    public void setGpsStatus(int gpsStatus)
    {
        editor.putInt("gpsStatus", gpsStatus);
        editor.commit();
    }

    public int getGpsInterval()
    {
        return sp.getInt("gpsInterval", 0);
    }

    public void setGpsInterval(int gpsInterval)
    {
        editor.putInt("gpsInterval", gpsInterval);
        editor.commit();
    }

    public int getLbsInterval()
    {
        return sp.getInt("lbsInterval", 0);
    }

    public void setLbsInterval(int lbsInterval)
    {
        editor.putInt("lbsInterval", lbsInterval);
        editor.commit();
    }

    public void setDeviceModel(String deviceModel)
    {
        editor.putString("deviceModel", deviceModel);
        editor.commit();
    }

    public String getDeviceModel()
    {
        return sp.getString("deviceModel", "");
    }

    public String getGPSTime()
    {
        return sp.getString("gpsTime", "");
    }

    public void setGPSTime(String gpsTime)
    {
        editor.putString("gpsTime", gpsTime);
        editor.commit();
    }

    public void setJessionId(String jessionId)
    {
        editor.putString("JSESSIONID", jessionId);
        editor.commit();
    }

    public String getJessionId()
    {
        return sp.getString("JSESSIONID", null);
    }

    public int getRoleType()
    {
        return sp.getInt("roleType", 0);
    }

    public void setRoleType(int roleType)
    {
        editor.putInt("roleType", roleType);
        editor.commit();
    }

    public void setSetUpGpsTimeOpen(boolean setupGpsTimeOpen)
    {
        editor.putBoolean("setupGpsTimeOpen", setupGpsTimeOpen);
        editor.commit();
    }

    public boolean getSetUpGpsTimeOpen()
    {
        return sp.getBoolean("setupGpsTimeOpen", false);
    }

    public void setSetUpLbsInterval(boolean setUpLbsInterval)
    {
        editor.putBoolean("setUpLbsInterval", setUpLbsInterval);
        editor.commit();
    }

    public boolean getSetUpLbsInterval()
    {
        return sp.getBoolean("setUpLbsInterval", false);
    }

    public void setHasStudentPhone(boolean hasStudentPhone)
    {
        editor.putBoolean("hasStudentPhone", hasStudentPhone);
        editor.commit();
    }

    public boolean getHasStudentPhone()
    {
        return sp.getBoolean("hasStudentPhone", false);
    }

    public void setIsSettingGoal(boolean isSettingGoal)
    {
        editor.putBoolean("isSettingGoal", isSettingGoal);
        editor.commit();
    }

    public boolean getIsSettingGoal()
    {
        return sp.getBoolean("isSettingGoal", false);
    }

    /**
     * 教师是否发布了通知、家庭作业、留言、成绩
     * @param publishNew
     */
    public void setPublishNew(boolean publishNew)
    {
        editor.putBoolean("publishNew", publishNew);
        editor.commit();
    }

    /**
     * 教师是否发布了通知、家庭作业、留言、成绩
     * @return
     */
    public boolean getPublishNew()
    {
        return sp.getBoolean("publishNew", false);
    }

    /**
     * [记录当前用户GroupId]<BR>
     * @param groupId 当前用户组别
     */
    public void setGroupId(String groupId)
    {
        editor.putString("groupId", groupId);
        editor.commit();
    }

    /**
     * [获取当前用户GroupId]<BR>
     * @return 当前用户GroupId
     */
    public String getGroupId()
    {
        return sp.getString("groupId", null);
    }

    /**
     * [清除所有储存数据]<BR>
     */
    public void clearData()
    {
//        editor.clear().apply(); 
        setAutoLogin(false);
        setUserName("");
        setPassWord("");
        setIsLogout(true);
        setLoginState(0);
  
    }

}
