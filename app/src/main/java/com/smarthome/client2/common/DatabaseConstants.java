package com.smarthome.client2.common;

import com.smarthome.client2.content.ClientContentProvider;

import android.net.Uri;

/**
 * 
 * @author xingang.sun.com
 *
 */
public interface DatabaseConstants {
    Uri CONTENT_URI = ClientContentProvider.CONTENT_URI;
    String T_MESSAGE = "message";
    String T_MESSAGE_ID = "_id";
    
	interface historyCache {
		String TableName = "historyCache";

		String KEY_ROWId = "id";
		String KEY_IdKey = "IdKey";// 名字
		String KEY_content = "content";//
		String KEY_date = "date";

		String[] columns = new String[] { KEY_ROWId, KEY_IdKey, KEY_content, KEY_date };
		String Create_table = "create table " + TableName + "(id integer primary key autoincrement, " + KEY_IdKey + " text not null , " + KEY_content + " text not null," + KEY_date + " long not null " + ");";
	}
	
	/**
	 * 手环分钟表结构
	 * @author archermind
	 *
	 */
	interface TableDataPerMinute{
		String tableName = "t_data_per_minute";
		
		String KEY_ROWID = "_id";
		String KEY_DEVICE_ID = "device_id";
		String KEY_DATA_ID = "data_id";
		String KEY_DATE = "date";
		String KEY_CALORIE = "calorie";
		String KEY_DEVICE_MODE = "device_mode";
		String KEY_METS = "mets";
		String KEY_EXERCISE = "exercise";
		String KEY_HUMAN_STATUS = "human_status";
		String KEY_STEPS = "steps";
		String KEY_DISTANCE = "distance";
		String KEY_USER_NAME = "user_name";
		String KEY_USER_ID = "user_id";
		String KEY_INSERT_DATE = "insert_date";
		
		String[] columns = new String[]{
				KEY_ROWID,
				KEY_DEVICE_ID,
				KEY_DATA_ID,
				KEY_DATE,
				KEY_CALORIE,
				KEY_DEVICE_MODE,
				KEY_METS,
				KEY_EXERCISE,
				KEY_HUMAN_STATUS,
				KEY_STEPS,
				KEY_DISTANCE,
				KEY_USER_NAME,
				KEY_USER_ID,
				KEY_INSERT_DATE
		};
		
		String Create_table =
				"create table " + tableName + "( " + KEY_ROWID + " integer primary key autoincrement, " + 
						KEY_DEVICE_ID + " long , " + 
						KEY_DATA_ID + " long , " + 
						KEY_DATE + " long , " +
						KEY_CALORIE + " integer , " +
						KEY_DEVICE_MODE + " integer , " +
						KEY_METS + " float , " +
						KEY_EXERCISE + " integer , " +
						KEY_HUMAN_STATUS + " integer , " +
						KEY_STEPS + " integer , " + 
						KEY_DISTANCE + " integer , " +
						KEY_USER_NAME + " txt , " +
						KEY_USER_ID + " txt , " + 
						KEY_INSERT_DATE + " long " +
						");";
	}
	
	/**
	 * 手环小时表结构
	 * @author archermind
	 *
	 */
	interface TableDataPerHour{
		String tableName = "t_data_per_hour";
		
		String KEY_ROWID = "_id";
		String KEY_DEVICE_ID = "device_id";
		String KEY_DATA_ID = "data_id";
		String KEY_DATE = "date";
		String KEY_CALORIE = "calorie";
		String KEY_METS = "mets";
		String KEY_SLEEP_TIME = "sleep_time";//不计算唤醒时间的，睡眠总时长
		String KEY_LIGHT_SLEEP_TIME = "light_sleep_time";
		String KEY_HEAVY_SLEEP_TIME = "heavy_sleep_time";
		String KEY_SLEEP_MODE_TIME = "sleep_mode_time";//睡眠模式的总时长
		String KEY_ACTIVE_TIME = "active_time";//不算静止消耗的，运动模式时长
		String KEY_ACTIVITY_MODE_TIME = "activity_mode_time";//运动模式的总时长
		String KEY_STEPS = "steps";
		String KEY_DISTANCE = "distance";
		String KEY_USER_NAME = "user_name";
		String KEY_USER_ID = "user_id";
		String KEY_INSERT_DATE = "insert_date";
		
		String[] columns = new String[]{
				KEY_ROWID,
				KEY_DEVICE_ID,
				KEY_DATA_ID,
				KEY_DATE,
				KEY_CALORIE,
				KEY_METS,
				KEY_SLEEP_TIME,
				KEY_LIGHT_SLEEP_TIME,
				KEY_HEAVY_SLEEP_TIME,
				KEY_SLEEP_MODE_TIME,
				KEY_ACTIVITY_MODE_TIME,
				KEY_ACTIVE_TIME,
				KEY_STEPS,
				KEY_DISTANCE,
				KEY_USER_NAME,
				KEY_USER_ID,
				KEY_INSERT_DATE
		};
		
		String Create_table =
				"create table " + tableName + "( " + KEY_ROWID + " integer primary key autoincrement, " + 
						KEY_DEVICE_ID + " long , " + 
						KEY_DATA_ID + " long , " + 
						KEY_DATE + " long , " +
						KEY_CALORIE + " integer , " +
						KEY_METS + " float , " +
						KEY_SLEEP_TIME + " integer , " +
						KEY_LIGHT_SLEEP_TIME + " integer , " +
						KEY_HEAVY_SLEEP_TIME + " integer , " +
						KEY_SLEEP_MODE_TIME + " integer , " +
						KEY_ACTIVITY_MODE_TIME + " integer , " +
						KEY_ACTIVE_TIME + " integer , " +
						KEY_STEPS + " integer , " + 
						KEY_DISTANCE + " integer , " +
						KEY_USER_NAME + " txt , " +
						KEY_USER_ID + " txt , " + 
						KEY_INSERT_DATE + " long " +
						");";
	}
	
	/**
	 * 登录的用户表结构
	 * @author archermind
	 *
	 */
	interface TableUser{
		String tableName = "t_user";
		
		String KEY_ROWID = "_id";
		String KEY_USER_ID = "user_id";
		// 登录账号
		String KEY_USER_NAME = "user_name";
		// 密码
		String KEY_PASS_WORD = "pass_word";
		
		// 账号登录方式 01:账号登录02:手机号登录03:QQ登录04:sina登录
		String KEY_ACCT_TYPE = "accttype";
		// 手机号
		String KEY_TEL_NUM = "telnum";
		// 用户真实姓名
		String KEY_REAL_NAME = "userrealname";
		// 昵称
		String KEY_NICK_NAME = "nickname";
		// 用户类型 01:家长 02:老师
		String KEY_USER_TYPE = "usertype";
		// 学校名称 用户类型是老师时，必填
		String KEY_SCHOOL_NAME = "schoolname";
		// 用户状态 01:有效 00:无效
		String KEY_USER_STATUS = "userstatus";
		// 1:开启 0:关闭
		String KEY_USER_ACTIVE = "useractive";
		String KEY_ROLE_ID = "roleId";
		String KEY_PERSONALITY_SIGNATURE = "personalitysignature";
		String KEY_HEAD_PICPATH = "headpicpath";
		// 性别
		String KEY_GENDER = "gender";
		String KEY_YEAR = "year";
		String KEY_HEIGHT = "height";
		String KEY_WEIGHT = "weight";
		
		String KEY_INSERT_DATE = "insert_date";
		
		String[] columns = new String[]{
				KEY_ROWID,
				KEY_USER_ID,
				KEY_USER_NAME,
				KEY_PASS_WORD,
				
				KEY_ACCT_TYPE,
				KEY_TEL_NUM,
				KEY_REAL_NAME,
				KEY_NICK_NAME,
				KEY_USER_TYPE,
				KEY_SCHOOL_NAME,
				KEY_USER_STATUS,
				KEY_USER_ACTIVE,
				KEY_ROLE_ID,
				KEY_PERSONALITY_SIGNATURE,
				KEY_HEAD_PICPATH,
				KEY_GENDER,
				KEY_YEAR,
				KEY_HEIGHT,
				KEY_WEIGHT,
				
				KEY_INSERT_DATE
		};
		
		String Create_table =
				"create table " + tableName + "( " + KEY_ROWID + " integer primary key autoincrement, " + 
						KEY_USER_ID + " txt , " + 
						KEY_USER_NAME + " txt , " +
						KEY_PASS_WORD + " txt , " + 
						
						KEY_ACCT_TYPE + " integer , " +
						KEY_TEL_NUM + " txt , " +
						KEY_REAL_NAME + " txt , " +
						KEY_NICK_NAME + " txt , " +
						KEY_USER_TYPE + " integer , " +
						KEY_SCHOOL_NAME + " txt , " +
						KEY_USER_STATUS + " integer , " +
						KEY_USER_ACTIVE + " integer , " +
						KEY_ROLE_ID + " txt , " +
						KEY_PERSONALITY_SIGNATURE + " txt , " +
						KEY_HEAD_PICPATH + " txt , " +
						KEY_GENDER + " integer , " +
						KEY_YEAR + " integer , " +
						KEY_HEIGHT + " integer , " +
						KEY_WEIGHT + " float , " +
						
						KEY_INSERT_DATE + " long " +
						");";
	}
	
	/**
     * 上报个人信息
     * @author archermind
     *
     */
    interface TableDataInfo{
        String tableName = "t_data_info";
        
        String KEY_ROWID = "_id";
        String KEY_USER_ID = "user_id";
        String KEY_GENDER = "gender";
        String KEY_HEIGHT = "height";
        String KEY_BIRTHDAY = "birthday";
        String KEY_WEIGHT = "weight";
        
        String[] columns = new String[]{
                KEY_ROWID,
                KEY_USER_ID,
                KEY_GENDER,
                KEY_HEIGHT,
                KEY_WEIGHT,
                KEY_BIRTHDAY
        };
        
        String Create_table =
                "create table " + tableName + "( " + KEY_ROWID + " integer primary key autoincrement, " + 
                        KEY_USER_ID + " integer , " + 
                        KEY_GENDER + " integer , " + 
                        KEY_HEIGHT + " integer , " +
                        KEY_WEIGHT + " float , " +
                        KEY_BIRTHDAY + " integer , " +
                        ");";
    }
}





