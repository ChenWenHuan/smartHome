package com.smarthome.client2.familySchool.utils;

public class FsConstants
{
    public final static String TYPE_ADD_FLAG = "type_add_flag";

    public final static int TYPE_ADD_MSG = 1;

    public final static int TYPE_ADD_NOTICE = 2;

    public final static int TYPE_ADD_HOMEWORK = 3;

    public final static int TYPE_ADD_FAMILY_MSG = 4;
    
    public final static int TYPE_SEND_PERSONAL_MSG = 5; 
    
    public final static int WIDGET_IMAGE = 0x5000;

    public final static int LEAVE_MESSAGE_IMAGE = 0x5001;

    public final static int HOMEWORK_IMAGE = 0x5002;

    public final static int SCORE_IMAGE = 0x5003;

    public final static int NOTICE_IMAGE = 0x5004;

    public final static int ZOOM_IMAGE = 0x5005;

    public final static int TARGET_IMAGE = 0x5006;
    
    public final static int HOME_IMAGE = 0x5007;
    
    public final static int HANDLE_IMAGE = 0x5008;
    
    public final static int HEAD_IMAGE = 0x5009;

    public final static int HTTP_START = 0x6001;

    public final static int HTTP_SUCCESS = 0x6002;

    public final static int HTTP_FAILURE = 0x6003;

    public final static int HTTP_FINISH = 0x6004;

    /**
     * 账户变动（注销、登录、用户调用系统清除数据）
     */
    public final static String WIDGET_ACCOUNT_CHANGGE = "widget_account_change";

    /**
     * 与应用内部观察到的数据要同步（主要在应用内部查看过健康运动、位置后，通知widget立即刷新）
     */
    public final static String WIDGET_REFRESH_SYNC = "widget_refresh_sync";

    /**
     * 家庭成员增减变动、更换头像后
     */
    public final static String WIDGET_MEMBERS_CHANGE = "widget_members_change";

    /**
     * 点击4×1widget跳转到Activity
     */
    public final static String WIDGET_SINGLE_JUMP = "widget_single_jump";

    /**
     * 点击4×2widget上方跳转到Activity
     */
    public final static String WIDGET_DOUBLE_JUMP_UP = "widget_double_jump_up";

    /**
     * 点击wether and time widget上方跳转到Activity
     */
    public final static String WIDGET_JUMP_MAIN1 = "widget_weather_and_time_jump_up1";
    public final static String WIDGET_JUMP_MAIN2 = "widget_weather_and_time_jump_up2";
    public final static String WIDGET_JUMP_MAIN3 = "widget_weather_and_time_jump_up3";
    public final static String WIDGET_JUMP_MAIN4 = "widget_weather_and_time_jump_up4";

    /**
     * 点击4×2widget下方跳转到Activity
     */
    public final static String WIDGET_DOUBLE_JUMP_DOWN = "widget_double_jump_down";

    /**
     * widget中数据已经从服务器获取，可以刷新数据了
     */
    public final static String WIDGET_DATA_OK = "widget_data_ok";

    /**
     * 4×1widget点击切换成员，向上
     */
    public final static String WIDGET_SINGLE_SWITCH_UP = "widget_single_switch_up";

    /**
     * 4×1widget点击切换成员，向下
     */
    public final static String WIDGET_SINGLE_SWITCH_DOWN = "widget_single_switch_down";

    /**
     * 4×2widget点击切换成员，向上
     */
    public final static String WIDGET_DOUBLE_SWITCH_UP = "widget_double_switch_up";

    /**
     * 4×2widget点击切换成员，向下
     */
    public final static String WIDGET_DOUBLE_SWITCH_DOWN = "widget_double_switch_down";

    public static final String UPDATE_WIDGET_WEATHER_ACTION = "com.way.action.update_weather";
    public static final String WEATHER_SIMPLE_URL = "http://www.weather.com.cn/data/sk/";// 简要天气信息
    public static final String WEATHER_BASE_URL = "http://m.weather.com.cn/data/";// 详细天气
    public static final String PM2D5_BASE_URL = "http://www.pm25.in/api/querys/pm2_5.json?city=SHENZHEN&token=5j1znBVAsnSf5xQyNQyq&stations=no";
    private static final String WEATHER_INFO_FILENAME = "_weather.json";
    private static final String SIMPLE_WEATHER_INFO_FILENAME = "_simple_weather.json";
    private static final String PM2D5_INFO_FILENAME = "_pm2d5.json";
}
