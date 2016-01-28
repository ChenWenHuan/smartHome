package com.smarthome.client2.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smarthome.client2.bean.DeviceLocalHourDataBean;
import com.smarthome.client2.bean.DeviceLocalHourDataListBean;
import com.smarthome.client2.bean.DeviceLocalMinuteDataBean;
import com.smarthome.client2.bean.DeviceLocalMinuteDataListBean;
import com.smarthome.client2.bean.UserBean;
import com.smarthome.client2.bean.UserListBean;
import com.smarthome.client2.common.DatabaseConstants;
import com.smarthome.client2.common.DatabaseConstants.historyCache;

/**
 * 
 * @author xingang.sun.com
 *
 */

public class DBManager {

    private static final int VERSION = 2;
    private static final String DATABASE = "client.db";
	private final Context context;
	private static DatabaseHelper DBHelper = null;
	private static SQLiteDatabase db = null;

	private static DBManager dbManager = null;

	private DBManager(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE, null, VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DatabaseConstants.historyCache.Create_table);
			db.execSQL(DatabaseConstants.TableDataPerMinute.Create_table);
			db.execSQL(DatabaseConstants.TableDataPerHour.Create_table);
			db.execSQL(DatabaseConstants.TableUser.Create_table);
//			db.execSQL(DatabaseConstants.TableDataInfo.Create_table);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.historyCache.TableName);
			db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TableDataPerMinute.tableName);
			db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TableDataPerHour.tableName);
			db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TableUser.tableName);
			db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TableDataInfo.tableName);
			
			onCreate(db);
			
		}
	}// DatabaseHelper

	/**
	 * 打开数据库
	 */
	public synchronized static DBManager open(Context ctx) throws SQLException {
		if (dbManager != null) {
		} else {
			dbManager = new DBManager(ctx);
			db = DBHelper.getWritableDatabase();
		}
		return dbManager;
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		if (db != null) {
			db.close();
		}
		if (DBHelper != null) {
			DBHelper.close();
		}
		if (dbManager != null) {
			dbManager = null;
		}
	}

	// //////////////////////////////////////////////////
	/**
	 * 向缓存表中插入一条数据
	 */
	public void insertToCache(String IdKey, String content, long date) {
		ContentValues cv = new ContentValues();
		cv.put(historyCache.KEY_IdKey, IdKey);
		cv.put(historyCache.KEY_content, content);
		cv.put(historyCache.KEY_date, date);
		db.insert(historyCache.TableName, null, cv);
	}

	public void insertOrUpdateToCache(String IdKey, String content, long date) {
		ContentValues cv = new ContentValues();
		cv.put(historyCache.KEY_IdKey, IdKey);
		cv.put(historyCache.KEY_content, content);
		cv.put(historyCache.KEY_date, date);
		int result = db.update(historyCache.TableName, cv, historyCache.KEY_IdKey + " LIKE ? ", new String[] { IdKey });
		if (result <= 0) {
			db.insert(historyCache.TableName, null, cv);
		}
	}

	/**
	 * 更新缓存表中的一条数据
	 */
	public void updateToCache(String IdKey, String content, long date) {
		ContentValues cv = new ContentValues();
		cv.put(historyCache.KEY_IdKey, IdKey);
		cv.put(historyCache.KEY_content, content);
		cv.put(historyCache.KEY_date, date);
		db.update(historyCache.TableName, cv, historyCache.KEY_IdKey + " LIKE ? ", new String[] { IdKey });

	}

	/**
	 * 根据key获取一条缓存
	 */
	public synchronized Cursor getCache(String IdKey) {
		Cursor cur = null;
		cur = db.query(historyCache.TableName, historyCache.columns, historyCache.KEY_IdKey + " LIKE ? ", new String[] { IdKey }, null, null, null);
		return cur;
	}

	/**
	 * 根据key删除一条缓存
	 */
	public void deleteCache(String IdKey) {
		db.delete(historyCache.TableName, historyCache.KEY_IdKey + " LIKE ? ", new String[] { IdKey });
	}

	/**
	 * 清楚所有缓存记录
	 */
	public void deleteAllCache() {
		db.delete(historyCache.TableName, null, null);
	}
	
	/////////////////////////////
	/**
	 * ClientContentProvider操作
	 */
	public Cursor queryByClientProvider(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
		return db.query(table, columns, selection, selectionArgs, null, null, orderBy);
	}
	
	public void insertByClientProvider(String table, String nullColumnHack, ContentValues values){
		db.insert(table, nullColumnHack, values);
	}
	
	////////////////////////////
	/**
	 * 插入数据到小时表
	 * @param dataListBean
	 */
	public void insertToPerHourTable(DeviceLocalHourDataListBean dataListBean){
		for(DeviceLocalHourDataBean bean : dataListBean.list){
			ContentValues cv = new ContentValues();
			cv.put(DatabaseConstants.TableDataPerHour.KEY_DEVICE_ID, bean.device_id);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_DATA_ID, bean.data_id);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_DATE, bean.date);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_CALORIE, bean.calorie);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_METS, bean.mets);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_SLEEP_TIME, bean.sleep_time);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_LIGHT_SLEEP_TIME, bean.light_sleep_time);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_HEAVY_SLEEP_TIME, bean.heavy_sleep_time);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_SLEEP_MODE_TIME, bean.sleep_mode_time);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_ACTIVITY_MODE_TIME, bean.activity_mode_time);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_ACTIVE_TIME, bean.active_time);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_STEPS, bean.steps);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_DISTANCE, bean.distance);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_USER_NAME, bean.user_name);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_USER_ID, bean.user_id);
			cv.put(DatabaseConstants.TableDataPerHour.KEY_INSERT_DATE, bean.insert_date);
			db.insert(DatabaseConstants.TableDataPerHour.tableName, null, cv);
		}
	}
	
	/**
	 * 根据userId查询小时表的数据
	 * @param userId
	 * @return
	 */
	public DeviceLocalHourDataListBean getFromPerHourDataTable (String userId){
		DeviceLocalHourDataListBean listBean = new DeviceLocalHourDataListBean();
		listBean.list.clear();
		
		Cursor cur = db.query(
				DatabaseConstants.TableDataPerHour.tableName, 
				DatabaseConstants.TableDataPerHour.columns, 
				DatabaseConstants.TableDataPerHour.KEY_USER_ID + " = ? ", 
				new String[]{userId}, 
				null, 
				null, 
				DatabaseConstants.TableDataPerHour.KEY_DATE+" desc ");
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					DeviceLocalHourDataBean bean = new DeviceLocalHourDataBean();
					
					bean.device_id = cur.getLong(1);
					bean.data_id = cur.getLong(2);
					bean.date = cur.getLong(3);
					bean.calorie = cur.getInt(4);
					bean.mets = cur.getFloat(5);
					bean.sleep_time = cur.getInt(6);
					bean.light_sleep_time = cur.getInt(7);
					bean.heavy_sleep_time = cur.getInt(8);
					bean.sleep_mode_time = cur.getInt(9);
					bean.activity_mode_time = cur.getInt(10);
					bean.active_time = cur.getInt(11);
					bean.steps = cur.getInt(12);
					bean.distance = cur.getInt(13);
					bean.user_name = cur.getString(14);
					bean.user_id = cur.getString(15);
					bean.insert_date = cur.getLong(16);
					
					listBean.list.add(bean);
				} while (cur.moveToNext());
			}

			cur.close();
		}
		
		return listBean;
	}
	
	/**
	 * 删除小时表中userId的数据
	 * @param userId
	 */
	public void deleteFromPerHourTable(String userId){
		db.delete(
				DatabaseConstants.TableDataPerHour.tableName, 
				DatabaseConstants.TableDataPerHour.KEY_USER_ID + " = ? ", 
				new String[]{userId});
	}
	
	/**
	 * 插入数据到分钟表
	 * @param dataListBean
	 */
	public void insertToPerMinuteTable(DeviceLocalMinuteDataListBean dataListBean){
		for(DeviceLocalMinuteDataBean bean : dataListBean.list){
			ContentValues cv = new ContentValues();
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_DEVICE_ID, bean.device_id);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_DATA_ID, bean.data_id);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_DATE, bean.date);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_CALORIE, bean.calorie);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_DEVICE_MODE, bean.device_mode);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_METS, bean.mets);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_EXERCISE, bean.exercise);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_HUMAN_STATUS, bean.human_status);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_STEPS, bean.steps);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_DISTANCE, bean.distance);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_USER_NAME, bean.user_name);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_USER_ID, bean.user_id);
			cv.put(DatabaseConstants.TableDataPerMinute.KEY_INSERT_DATE, bean.insert_date);
			db.insert(DatabaseConstants.TableDataPerMinute.tableName, null, cv);
		}
	}
	
	/**
	 * 根据userId查询分钟表的数据
	 * @param userId
	 * @return
	 */
	public DeviceLocalMinuteDataListBean getFromPerMinuteDataTable (String userId){
		DeviceLocalMinuteDataListBean listBean = new DeviceLocalMinuteDataListBean();
		listBean.list.clear();
		
		Cursor cur = db.query(
				DatabaseConstants.TableDataPerMinute.tableName, 
				DatabaseConstants.TableDataPerMinute.columns, 
				DatabaseConstants.TableDataPerMinute.KEY_USER_ID + " = ? ", 
				new String[]{userId}, 
				null, 
				null, 
				DatabaseConstants.TableDataPerMinute.KEY_DATE+" desc ");
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					DeviceLocalMinuteDataBean bean = new DeviceLocalMinuteDataBean();
					
					bean.device_id = cur.getLong(1);
					bean.data_id = cur.getLong(2);
					bean.date = cur.getLong(3);
					bean.calorie = cur.getInt(4);
					bean.device_mode = cur.getInt(5);
					bean.mets = cur.getInt(6);
					bean.exercise = cur.getInt(7);
					bean.human_status = cur.getInt(8);
					bean.steps = cur.getInt(9);
					bean.distance = cur.getInt(10);
					bean.user_name = cur.getString(11);
					bean.user_id = cur.getString(12);
					bean.insert_date = cur.getLong(13);
					
					listBean.list.add(bean);
				} while (cur.moveToNext());
			}

			cur.close();
		}
		
		return listBean;
	}
	
	/**
	 * 删除分钟表中userId的数据
	 * @param userId
	 */
	public void deleteFromPerMinuteTable(String userId){
		db.delete(
				DatabaseConstants.TableDataPerMinute.tableName, 
				DatabaseConstants.TableDataPerMinute.KEY_USER_ID + " = ? ", 
				new String[]{userId});
	}
	
	/////////////////////////////////
	
	/**
	 * 插入用户表
	 * @param bean
	 */
	public void insertToUserTable(UserBean bean){
		ContentValues cv = new ContentValues();
		cv.put(DatabaseConstants.TableUser.KEY_USER_ID, bean.id);
		cv.put(DatabaseConstants.TableUser.KEY_USER_NAME, bean.acctno);
		cv.put(DatabaseConstants.TableUser.KEY_PASS_WORD, bean.pwd);
		
		cv.put(DatabaseConstants.TableUser.KEY_ACCT_TYPE, bean.accttype);
		cv.put(DatabaseConstants.TableUser.KEY_TEL_NUM, bean.telnum);
		cv.put(DatabaseConstants.TableUser.KEY_REAL_NAME, bean.userrealname);
		cv.put(DatabaseConstants.TableUser.KEY_NICK_NAME, bean.nickname);
		cv.put(DatabaseConstants.TableUser.KEY_USER_TYPE, bean.usertype);
		cv.put(DatabaseConstants.TableUser.KEY_SCHOOL_NAME, bean.schoolname);
		cv.put(DatabaseConstants.TableUser.KEY_USER_STATUS, bean.userstatus);
		cv.put(DatabaseConstants.TableUser.KEY_USER_ACTIVE, bean.useractive);
		cv.put(DatabaseConstants.TableUser.KEY_ROLE_ID, bean.roleId);
		cv.put(DatabaseConstants.TableUser.KEY_PERSONALITY_SIGNATURE, bean.personalitysignature);
		cv.put(DatabaseConstants.TableUser.KEY_HEAD_PICPATH, bean.headpicpath);
		cv.put(DatabaseConstants.TableUser.KEY_GENDER, bean.gender);
		cv.put(DatabaseConstants.TableUser.KEY_YEAR, bean.year);
		cv.put(DatabaseConstants.TableUser.KEY_HEIGHT, bean.height);
		cv.put(DatabaseConstants.TableUser.KEY_WEIGHT, bean.weight);
		
		cv.put(DatabaseConstants.TableUser.KEY_INSERT_DATE, bean.insert_time);
		db.insert(DatabaseConstants.TableUser.tableName, null, cv);
	}
	
	/**
	 * 更新用户表
	 * @param bean
	 */
	public void updateToUserTable(UserBean bean){
		ContentValues cv = new ContentValues();
		cv.put(DatabaseConstants.TableUser.KEY_USER_ID, bean.id);
		cv.put(DatabaseConstants.TableUser.KEY_USER_NAME, bean.acctno);
		cv.put(DatabaseConstants.TableUser.KEY_PASS_WORD, bean.pwd);
		
		cv.put(DatabaseConstants.TableUser.KEY_ACCT_TYPE, bean.accttype);
		cv.put(DatabaseConstants.TableUser.KEY_TEL_NUM, bean.telnum);
		cv.put(DatabaseConstants.TableUser.KEY_REAL_NAME, bean.userrealname);
		cv.put(DatabaseConstants.TableUser.KEY_NICK_NAME, bean.nickname);
		cv.put(DatabaseConstants.TableUser.KEY_USER_TYPE, bean.usertype);
		cv.put(DatabaseConstants.TableUser.KEY_SCHOOL_NAME, bean.schoolname);
		cv.put(DatabaseConstants.TableUser.KEY_USER_STATUS, bean.userstatus);
		cv.put(DatabaseConstants.TableUser.KEY_USER_ACTIVE, bean.useractive);
		cv.put(DatabaseConstants.TableUser.KEY_ROLE_ID, bean.roleId);
		cv.put(DatabaseConstants.TableUser.KEY_PERSONALITY_SIGNATURE, bean.personalitysignature);
		cv.put(DatabaseConstants.TableUser.KEY_HEAD_PICPATH, bean.headpicpath);
		cv.put(DatabaseConstants.TableUser.KEY_GENDER, bean.gender);
		cv.put(DatabaseConstants.TableUser.KEY_YEAR, bean.year);
		cv.put(DatabaseConstants.TableUser.KEY_HEIGHT, bean.height);
		cv.put(DatabaseConstants.TableUser.KEY_WEIGHT, bean.weight);
		
		cv.put(DatabaseConstants.TableUser.KEY_INSERT_DATE, bean.insert_time);
		
		
		db.update(
				DatabaseConstants.TableUser.tableName, 
				cv,
				DatabaseConstants.TableUser.KEY_USER_ID + " = ? ", 
				new String[]{bean.id});
	}
	
	/**
	 * 如果用户表存在该用户，则更新用户信息，否则插入用户信息
	 * @param bean
	 */
	public void insertOrUpdateUserTable(UserBean bean){
		Cursor mCursor = db.query(
				DatabaseConstants.TableUser.tableName,
				DatabaseConstants.TableUser.columns,
				DatabaseConstants.TableUser.KEY_USER_ID + " = ? ", 
				new String[]{bean.id}, 
				null, 
				null, 
				null);
		if(mCursor.moveToFirst()){
			updateToUserTable(bean);
		}else{
			insertToUserTable(bean);
		}
	}
	
	/**
	 * 根据userId查询用户信息
	 * @param userId
	 * @return
	 */
	public UserBean queryToUserTable(String userId){
		UserBean bean = new UserBean();
		
		Cursor mCursor = db.query(
				DatabaseConstants.TableUser.tableName,
				DatabaseConstants.TableUser.columns, 
				DatabaseConstants.TableUser.KEY_USER_ID + " = ? ", 
				new String[]{userId}, 
				null, 
				null, 
				null);
		
		if(mCursor.moveToFirst()){
			bean.id = mCursor.getString(1);
			bean.acctno = mCursor.getString(2);
			bean.pwd = mCursor.getString(3);
			
			bean.accttype = mCursor.getInt(4);
			bean.telnum = mCursor.getString(5);
			bean.userrealname = mCursor.getString(6);
			bean.nickname = mCursor.getString(7);
			bean.usertype = mCursor.getInt(8);
			bean.schoolname = mCursor.getString(9);
			bean.userstatus = mCursor.getInt(10);
			bean.useractive = mCursor.getInt(11);
			bean.roleId = mCursor.getString(12);
			bean.personalitysignature = mCursor.getString(13);
			bean.headpicpath = mCursor.getString(14);
			bean.gender = mCursor.getInt(15);
			bean.year = mCursor.getInt(16);
			bean.height = mCursor.getInt(17);
			bean.weight = mCursor.getInt(18);
			
			bean.insert_time = mCursor.getLong(19);
		}
		
		return bean;
	}
	
	/**
	 * 查询所有用户信息
	 * @param userId
	 * @return
	 */
	public UserListBean queryToUserTable(){
		UserListBean listBean = new UserListBean();
		
		Cursor mCursor = db.query(
				DatabaseConstants.TableUser.tableName,
				DatabaseConstants.TableUser.columns, 
				null, 
				null, 
				null, 
				null, 
				null);
		
		if(mCursor != null && mCursor.moveToFirst()){
			do{
				UserBean bean = new UserBean();
				bean.id = mCursor.getString(1);
				bean.acctno = mCursor.getString(2);
				bean.pwd = mCursor.getString(3);
				
				bean.accttype = mCursor.getInt(4);
				bean.telnum = mCursor.getString(5);
				bean.userrealname = mCursor.getString(6);
				bean.nickname = mCursor.getString(7);
				bean.usertype = mCursor.getInt(8);
				bean.schoolname = mCursor.getString(9);
				bean.userstatus = mCursor.getInt(10);
				bean.useractive = mCursor.getInt(11);
				bean.roleId = mCursor.getString(12);
				bean.personalitysignature = mCursor.getString(13);
				bean.headpicpath = mCursor.getString(14);
				bean.gender = mCursor.getInt(15);
				bean.year = mCursor.getInt(16);
				bean.height = mCursor.getInt(17);
				bean.weight = mCursor.getInt(18);
				
				bean.insert_time = mCursor.getLong(19);
				
				listBean.list.add(bean);
			}while(mCursor.moveToNext());
		}
		
		return listBean;
	}
	
	/**
	 * 删除用户表所有数据
	 */
	public void deleteFromUserTable(){
		db.delete(
				DatabaseConstants.TableUser.tableName,
				null, 
				null);
	}
	
	
	// //////////////////////////////////////////////////
    /**
     * 家庭成员用户信息表
     */
    public void insertToInfo(String id, String gender, String height, String weight, String birthday) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConstants.TableDataInfo.KEY_USER_ID, id);
        cv.put(DatabaseConstants.TableDataInfo.KEY_GENDER, gender);
        cv.put(DatabaseConstants.TableDataInfo.KEY_HEIGHT, height);
        cv.put(DatabaseConstants.TableDataInfo.KEY_WEIGHT, height);
        cv.put(DatabaseConstants.TableDataInfo.KEY_BIRTHDAY, birthday);
        db.insert(DatabaseConstants.TableDataInfo.tableName, null, cv);
    }

    public void insertOrUpdateToInfo(String id, String gender, String height, String weight, String birthday) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConstants.TableDataInfo.KEY_USER_ID, id);
        cv.put(DatabaseConstants.TableDataInfo.KEY_GENDER, gender);
        cv.put(DatabaseConstants.TableDataInfo.KEY_HEIGHT, height);
        cv.put(DatabaseConstants.TableDataInfo.KEY_WEIGHT, height);
        cv.put(DatabaseConstants.TableDataInfo.KEY_BIRTHDAY, birthday);
        int result = db.update(DatabaseConstants.TableDataInfo.tableName, cv, 
            DatabaseConstants.TableDataInfo.KEY_USER_ID + " LIKE ? ", new String[] { id });
        if (result <= 0) {
            db.insert(DatabaseConstants.TableDataInfo.tableName, null, cv);
        }
    }

    /**
     * 更新缓存表中的一条数据
     */
    public void updateToInfo(String id, String gender, String height, String weight, String birthday) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConstants.TableDataInfo.KEY_USER_ID, id);
        cv.put(DatabaseConstants.TableDataInfo.KEY_GENDER, gender);
        cv.put(DatabaseConstants.TableDataInfo.KEY_HEIGHT, height);
        cv.put(DatabaseConstants.TableDataInfo.KEY_WEIGHT, height);
        cv.put(DatabaseConstants.TableDataInfo.KEY_BIRTHDAY, birthday);
        db.update(DatabaseConstants.TableDataInfo.tableName, cv, 
            DatabaseConstants.TableDataInfo.KEY_USER_ID + " LIKE ? ", new String[] { id });

    }

    /**
     * 根据key获取一条缓存
     */
    public synchronized Cursor getInfo(String id) {
        Cursor cur = null;
        cur = db.query(DatabaseConstants.TableDataInfo.tableName, 
            DatabaseConstants.TableDataInfo.columns, 
            DatabaseConstants.TableDataInfo.KEY_USER_ID + " LIKE ? ", 
            new String[] { id }, null, null, null);
        return cur;
    }

    /**
     * 根据key删除一条缓存
     */
    public void deleteInfo(String id) {
        db.delete(DatabaseConstants.TableDataInfo.tableName
            , DatabaseConstants.TableDataInfo.KEY_USER_ID + " LIKE ? ", new String[] { id });
    }

    /**
     * 清楚所有缓存记录
     */
    public void deleteAllInfo() {
        db.delete(DatabaseConstants.TableDataInfo.tableName, null, null);
    }
	
	
}
