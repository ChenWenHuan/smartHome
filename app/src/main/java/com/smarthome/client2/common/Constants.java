package com.smarthome.client2.common;

import android.content.Context;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.view.Display;
import android.view.WindowManager;

import com.smarthome.client2.R;

public class Constants
{

    public static final String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath()
            + "/SmartHome/ImageCache/";
    
    public static final String WATCH_REACORD_AUDIO_PATH = Environment.getExternalStorageDirectory().getPath()
    		+"/SmartHome/WatchRecord/";
    public static final String CAMERA_VIDEO_RECORD_PATH = Environment.getExternalStorageDirectory().getPath()
    		+"/SmartHome/CameraRecord/";
    

    public static final String IMAGE_HEAD_FILE_NAME = "head/";

    public static final String IMAGE_HEAD_FILE_PATH = IMAGE_FILE_PATH
            + IMAGE_HEAD_FILE_NAME;

    public final static int REQUEST_CODE_FOR_PHOTO_ALBUM = 1;//调用相册

    public final static int REQUEST_CODE_FOR_TAKE_CAMERA = 2;//拍照

    public final static int REQUEST_CODE_START_PHOTO_ZOOM = 3;//裁剪

    public static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

    public static final int PHONES_DISPLAY_NAME_INDEX = 0;

    public static final int PHONES_NUMBER_INDEX = 1;

    public static final int PHONES_PHOTO_ID_INDEX = 2;

    public static final int PHONES_CONTACT_ID_INDEX = 3;

    //  APP ID:1101738110
    //  APP KEY:DhZYp0rhSEaakLdC
    //new
    //  APP ID：101096476
    //  APP KEY：7e2ab3371e0a51cb057e9f9a256b236a
    public static final String APP_KEY_QQ = "101096476";

    //test
    //  App key：161949682
    //  App secret：d1ae0aa31710e8b91a81f8cf0c9d442d
    //release
    // App key：1267042139
    // App secret：14543e2c91b0a40e05a9cd107f8e889b
    public static final String APP_KEY_WEIBO = "1267042139";

    /**
     * 使用新浪微博账号登录时的授权默认回调页
     */
    public static final String REDIRECT_URL_WEIBO = "http://open.weibo.com/apps/1267042139/info/advanced";

    /**
     * 用于新浪微博登录的授权参数
     */
    public static final String SCOPE_WEIBO = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

    public static final String SEARCH_NEW_MEMBER_DATA = "com.wist.receiver.search.new.member";

//    public static final String LOGIN_ACTION = "/account/login.action";


    public static final String GET_NEW_SERVER_LIST= "/httpintfs/appSys/getBussServers.do";

    public static final String GET_NEW_SERVER_ADDR = "/httpintfs/appSys/getBussServerIntfsAddr.do";
    //public static final String GET_NEW_SERVER_ADDR = "AccountMS/httpintfs/appSys/getBussServerIntfsAddr.do";
    public static final String LOGIN_V11_ACTION = "/account/login2.action";
    
    public static final String UPDATE_FAMILY_NAME_ACTION = "/family/updFamilyInfo.action";
    
    public static final String UPDATE_FAMILY_MEM_NAME_ACTION = "/family/updFamMembRemark.action";

    public static final String LOGOUT_ACTION = "/account/logout.action";

    public static final String VALIDCODE = "/account/validCode.action"; //旧接口，
    public static final String GETVALIDCODE = "/account/getValidCode.action"; //新接口，验证码在服务器验证

    public static final String LISTDEVICES = "/device/find.action";  // 获取设备列表
    public static final String ADDDEVICES = "/device/addDevice.action";
    public static final String DELDEVICES = "/device/deleteDevice.action";  // 删除设备列表

    public static final String GETDEVFUNCTION ="/device/getDevFun.action";

    public static final String ADD_NEM_FAMILY_MEM_V11 = "/family/addNewFamilyMember.action";
    
    public static final String ADD_FAMILY_MEM_V11 = "/family/addFamilyMember.action";
    
    public static final String ADD_FAMILY_DEVICE = "/family/bandFamDevice.action";
    public static final String DEL_FAMILY_DEVICE = "/family/delFamDevice.action";
    public static final String UPDATE_FAMILY_DEVICE = "/family/updFamDev.action";
    public static final String GET_FAMILY_DEVICES = "/family/getFamDevs.action";

    public static final String MODIFY_PASSWORD = "/account/modifyPwd.action";

    public static final String FIND_PASSWORD = "/account/findPwd.action";

    public static final String REGISTER_ACTION = "/account/register.action";

    public static final String GET_SCORE_JZ = "/homeandschool/queryScore.action";

    public static final String GET_SCORE_JS = "/homeandschool/detailScore.action";

    public static final String SET_USER_INFO = "/account/setUserInfo.action";

    public static final String GET_USER_INFO = "/account/getUserInfo.action";

    public static final String GET_USER_SYNOPSIS = "/account/getUserSynopsis.action";

    public static final String GET_VALID_CODE_FOR_SM = "/account/getValidCodeOfSmartPhones.action";

    public static final String SET_AVATAR = "/account/setPhoto.action";

    public static final String GET_AVATAR = "/app/getAvatar.action";

    public static final String SET_PLAN = "/app/setPlanning.action";

    public static final String GET_PLAN = "/app/getPlanning.action";

    public static final String SYNC = "/app/sync.action";

    public static final String INVERSE_SYNC = "/app/inverseSync.action";

    public static final String GET_MAX_DATA_DATE = "/app/getMaxDataDate.action";

    public static final String GET_SPORTS_BY_DATE_ACTION = "/app/getSportByDate.action";

    public static final String GET_SLEEP_BY_DATE_ACTION = "/app/getSleepByDate.action";

    public static final String GET_DATE_BY_DATE_ACTION = "/app/getDateByDate.action";

    public static final String GET_FRIEND_ACTION = "/app/getFriend.action";

    public static final String ADD_FRIEND_ACTION = "/app/addFriend.action";

    public static final String DELETE_FRIEND_ACTION = "/app/delFriend.action";

    public static final String SEARCH_FRIEND_ACTION = "/app/searchUser.action";

    public static final String GET_MESSAGE_BY_ID = "/app/getMsgById.action";

    public static final String AUDIT_USER_MESSAGE = "/contactgroup/auditMsg.action";
    
    public static final String AUDIT_USER_MESSAGE_V11 = "/family/auditingMsg.action";

    public static final String WIDGET_ACTION = "/app/widget.action";

    public static final String GET_SPORT_BY_CURR_DATE = "/app/getSportByCurrDate.action";

    public static final String GET_SLEEP_BY_CURR_DATE = "/app/getSleepByCurrDate.action";

    public static final String DELETE_USER_MESSAGE_ACTION = "/message/deleteMsg.action";

    public static final String EDIT_PASSWORD = "/app/updatePassword.action";

    public static final String FEED_BACK = "/app/feedback.action";

    public static final String GET_MINUTE_DATA = "/app/getMinuteData.action";

    public static final String GET_TRACK = "/location/getTrack.action";

    public static final String GET_FENCE = "/location/getFence.action";

    public static final String GET_OFTEN_POS = "/location/getOftenPos.action";

    public static final String ADD_GEO_FENCE = "/location/addElecFence.action";

    public static final String EDIT_GEO_FENCE = "/location/modifyElecFence.action";

    public static final String DELETE_GEO_FENCE = "/location/delElecFence.action";

    public static final String SET_FENCE = "/location/setFence.action";

    public static final String GET_NOW_POS = "/location/getNowPos.action";

    //利用userid 立刻定位
    public static final String GET_NOW_POS2 = "/location/getNowPos2.action";

    public static final String GET_LAST_POS = "/location/getPos.action";

    //取最新在服务器的位置
    public static final String GET_LAST_LOC = "/location/getLatestLoc.action";

    public static final String UPLOAD = "/location/upload.action";

    public static final String GET_COMMAND_STATUS = "/device/getCommandStatus.action";

    public static final String DEL_HAND_CIRCLE = "/device/deleteDevice.action";

    public static final String ADD_HAND_CIRCLE = "/device/addHandCircle.action";

    public static final String SET_SILENCE = "/device/setSilence.action";

    public static final String ADD_SILENCE = "/device/addSilence.action";

    public static final String GET_SILENCE = "/device/getSilence.action";

    public static final String DEL_SILENCE = "/device/delSilence.action";

    public static final String CHANGE_PHONE = "/device/changePhone.action";

    public static final String SET_FAMILY_PHONE = "/device/setFamilyPhone.action";

    public static final String GET_FAMILY_PHONE = "/device/getFamilyPhone.action";

    public static final String SET_SOS_PHONE = "/device/setSOSPhone.action";

    public static final String GET_SOS_PHONE = "/device/getSOSPhone.action";

    public static final String MONITOR = "/device/monitor.action";

    public static final String SET_LOCATION_INTERVAL = "/device/setLocationInterval.action";

    public static final String SET_WHITE_SPACE = "/device/setWhiteSpace.action";

    public static final String GET_WHITE_SPACE = "/device/getWhiteSpace.action";

    public static final String SET_POWER_ALARM = "/device/setPowerAlarm.action";

    public static final String SET_ALARM = "/device/setAlarm.action";

    public static final String UP_LBS_INTERVAL = "/device/upLbsInterval.action";

    public static final String SET_GPS_OPEN_INTERVAL = "/device/setGPSOpenInterval.action";

    public static final String SET_GPS_UP_INTERVAL = "/device/setGPSUpInterval.action";

    public static final String SET_CLOCK = "/device/setClock.action";

    public static final String ADD_CLOCK = "/device/addClock.action";

    public static final String GET_CLOCK = "/device/getClock.action";

    public static final String DEL_CLOCK = "/device/delClock.action";

    public static final String SYN = "/device/syn.action";
    
    public static final String DEL_FAMILY_MEM =  "/family/delFamMember.action";

    public static final String DEL_FAMILY_MEM_WITH_MAIN_GUARDER = "/family/delFamMemberWithMainGuarder.action";

    public static final String DEL_FAMILY_MEM_V20 = "/family/delFamMemberBySmart.action";

    public static final String DEVICE_STATUS = "/device/deviceStatus.action";

    public static final String DEL_WHITE_SPACE_ITEM = "/device/delWhiteSpaceItem.action";

    public static final String DEL_SOS_FAMILY_PHONE = "/device/delSOSFamilyPhone.action";

    public static final String SET_FENCE_STATUS = "/device/setFenceStatus.action";

    public static final String SET_OC_ALARM = "/device/setOCAlarm.action";

    public static final String CALL = "/device/call.action";

    public static final String SET_GPRS = "/device/setGPSTime.action";

    public static final String GET_GPRS = "/device/getGPSTime.action";

    public static final String ADD_GPRS = "/device/addGPRSTime.action";

    public static final String DEL_GPRS = "/device/delGPRSTime.action";
    
    public static final String GET_FAMILY_NUM = "/device/getDevDefaultFamTelNum.action";

    public static final String GET_FAMILY = "/contactgroup/getFamily.action";
    
    public static final String GET_FAMILY_V11 = "/account/getMain.action";

    public static final String GET_MESSAGE = "/message/getMsg.action";

    public static final String GET_UNREAD_MESSAGE = "/message/getUnReadMsg.action";

    public static final String SEE_SCORE_JS = "/homeandschool/seeScore.action";

    public static final String SEE_HOMEWORK = "/homeandschool/seeHomeWork.action";

    public static final String GET_NOTICES = "/homeandschool/getNotices.action";

    public static final String GET_USERINOF = "/account/getUserInfo.action";

    public static final String SET_USERINOF = "/account/setUserInfo.action";

    public static final String GET_USERFLAG = "/message/getFlag.action";
    
    public static final String GET_LATEST_HOME_MSG = "/family/getLatestFamLeavMsg.action";

    public static final String SEND_NOTICE = "/homeandschool/sendNotice.action";

    public static final String GET_SUBJECTNAME = "/homeandschool/getSubjects.action";

    public static final String SEND_HOMEWORK = "/homeandschool/assignHomeWork.action";

    public static final String GET_STUDENTINFO_BY_CLASSID = "/homeandschool/getStudentInfoByClass.action";

    public static final String ISSUE_SCORE = "/homeandschool/issueScore.action";

    public static final String ADD_FAMILY_MEMBER = "/contactgroup/addFamily.action";

    public static final String DELETE_FAMILY_MEMBER = "/contactgroup/delFamily.action";

    public static final String Add_OLD_PERSON = "/contactgroup/addOldPerson.action";

    public static final String VALID_OLD_PERSON = "/contactgroup/validOldPerson.action";

    public static final String QUERY_FAMILY_MEMBER = "/account/query.action";

    public static final String CHANG_DEVICE = "/device/changeDevice.action";

    public static final String s_url_1 = "http://t.cn/8sIdsz8";

    public static final String s_url_2 = "http://s.org/download/4";

    public static final String APP_FILE_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath()
            + "/com.wrist/";

    public static final String IMAGE_SHORT_FILE_NAME = "ShortCut.png";

    public static final String IMAGE_SHORT_FILE_PATH = IMAGE_FILE_PATH
            + IMAGE_SHORT_FILE_NAME;

    public static int DEFAULT_HEIGHT = 170;

    public static float DEFAULT_WEIGHT = 70F;

    public static int GOAL_DEFAULT_CAL = 1000;

    public static int GOAL_DEFAULT_SLEEP_TIME = 480;

    public static final int SC_OK = 200;

    public static final int DEVICE_ALREADY_EXIT = 3;

    public static final int FAIL_SEARCH_CONTENT = 204;

    public static final int INVALID_PARAMETER = 406;

    public static final int UNKNOW_RESULT = 1000;

    public static final int INVALID_PARAM = 1001;

    public static final int EXIST_ID = 1002;

    public static final int UNEXIST_ID = 1003;

    public static final int ERROR_PASSWORD = 1004;

    public static final int LOGIN_TIMEOUT = 1005;

    public static final int LOGIN_THIRD_FIRST_SUCCESS = 1006;

    public static final int DEVICE_OPEN_OR_NOT = 1007;

    public static final int SESSION_TIME_OUT = 1008;

    public static final int SERVER_OFFLINE = 500;

    public static final int SERVER_TIMEOUT = 0;

    public static final int NO_NETWORK = -3;

    public static final int NAME_OR_PASSWORD_ERROR = 103;

    public static final int NAME_PASSWORD_EMPTY = -10;
    public static final int PASSWORD_NOT_ENLVEN = -11;

    public static final int JSON_ERROR = -5;

    public static final int REQUEST_TIMEOUT = -2;

    public static final int LOGIN_LOCAL_STATE = 1;//本地登录

    public static final int LOGIN_QQ_STATE = 2;//QQ登录

    public static final int LOGIN_WEIBO_STATE = 3;//微博登录

    public static final int ACUITY_SPORTS_STATE_1 = 2;

    public static final int ACUITY_SPORTS_STATE_2 = 5;

    public static final int MILD_SPORTS_STATE_1 = 1;

    public static final int MILD_SPORTS_STATE_2 = 4;

    public static final int STATIC_SPORTS_STATE_1 = 0;

    public static final int STATIC_SPORTS_STATE_2 = 3;

    public static final int TIMEOUT_CONNECTION = 3*60*1000; //10000;

    public static final int TIMEOUT_SOCKET = 3*60*1000; //10000;

    public static final int TIMEOUT_SOCKET_GK309 = 3*60*1000; //1000 * 100;

    public static final int READ_TIMEOUT = 3*60*1000; //30 * 1000;

    // public static final String[] colors = new String[]{"#ff0000"};

    public static final int FLAG_NO_PHOTO = -1;

    public static int SCREEN_WIDTH = 720;

    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();

    }

    public static String getWeekFromInt(int index, Context context)
    {

        switch (index)
        {
            case 1:
                return context.getResources().getString(R.string.sunday);
            case 2:
                return context.getResources().getString(R.string.monday);
            case 3:
                return context.getResources().getString(R.string.tuesday);
            case 4:
                return context.getResources().getString(R.string.wednesday);
            case 5:
                return context.getResources().getString(R.string.thursday);
            case 6:
                return context.getResources().getString(R.string.friday);
            case 7:
                return context.getResources().getString(R.string.saturday);
            default:
                return "";
        }
    }

    public static final int ISFROM_BATTERY = 1;

    public static final int ISFROM_TOADY = 2;

    public final static int KUQI_STATUS_HEAVY_SLEEP = 2;

    public final static int KUQI_STATUS_LIGHT_SLEEP = 1;

    public final static int KUQI_STATUS_AWAKE = 0;

    public final static int GET_DATA_START = 0;

    public final static int GET_DATA_SUCCESS = 1;

    public final static int GET_DATA_FAIL = 2;
    
    public final static int GET_LINK_ACCOUNT = 3;
    
    public final static int WATCH_DEVICE_BINDED = 4;
    
    public final static int WATCH_DEVICE_BINDED_AGAIN = 5;
    
    public final static int WATCH_DEVICES_LIST = 6;

    public final static int GET_NEW_SERVER_ADDR_SUCESS = 7;

    public final static int GET_NEW_SERVER_ADDR_FAIL  = 8;

    public final static int SET_NETLISENER_DATA_START = 10;

    public final static int GET_USERINFO_DATA_FAIL = 3;

    public final static int GET_DEVICE_DATA_FAIL = 4;

    public final static int TYPE_GPS_ACTION_1 = 1;

    public final static int TYPE_GPS_ACTION_2 = 2;

    public final static int TYPE_GPS_ACTION_3 = 3;

    public final static int TYPE_GPS_ACTION_4 = 4;

    public final static int TYPE_GPS_ACTION_5 = 5;

    public final static int TYPE_GPS_ACTION_6 = 6;

    public final static int TYPE_IS_STUDENT = 1;

    public final static int TYPE_IS_OLD = 2;

    public final static int TYPE_IS_PARENT = 3;

    public final static int GET_DATA_DOUBLE_SENT = 3;  //重复邀请
    public final static int GET_DATA_RELATION_EXIST = 4;  //关系已经存在

    public final static String SOCKET_FLITER_EXCEPTION = "com.smarthome.client2.sockettimeout";

    public final static String CONNECT_FLITER_EXCEPTION = "com.smarthome.client2.connectexception";
}
