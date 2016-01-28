package com.smarthome.client2.widget;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	/**
	 * 数据库名
	 */
	private static final String DB_NAME = "members.db";
	/**
	 * 数据库版本号
	 */
	private static final int DB_VERSION = 2;

	/**
	 * 数据库的创建是在构造方法里
	 * 
	 * @param context
	 *            上下文
	 * @param name
	 *            数据库名字
	 * @param factory
	 *            建数据库工厂类
	 * @param version
	 *            版本号
	 */
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * 数据表的创建
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 通过SQLiteDatabase对数据表进行创建
		// 直接通过db执行sqlite语句
		String sql = "create table if not exists memberinfo(id bigint,headPath varchar(50),headName varchar(50),groupId bigint,name varchar(50),address varchar(50))";
		// 执行sql语句
		db.execSQL(sql);
	}

	/**
	 * 数据库的更新 调用onUpgrade要判断版本号newVersion>oldVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			db.execSQL("drop table if exists infos");
			onCreate(db);
		}
	}

}
