package com.smarthome.client2.unit.dao;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.smarthome.client2.bean.ChatMessage;

public class MessageDB {
	public static final String MSG_DBNAME = "message.db";
	private static final String WATCH_MESSAGE_TABLE_NAME = "watchmsg";
	private SQLiteDatabase db;

	public MessageDB(Context context) {
		db = context.openOrCreateDatabase(MSG_DBNAME, Context.MODE_PRIVATE,
				null);
	}

	public void saveMsg(ChatMessage entity) {
		db.execSQL("CREATE table IF NOT EXISTS _"
				+ WATCH_MESSAGE_TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "device_id TEXT, "			
				+ "time TEXT,"
				+ "isCome TEXT,"
				+ "content TEXT,"
				+ "isNew TEXT,"
				+ "type TEXT)");
		
		db.execSQL(
				"insert into _"
						+ WATCH_MESSAGE_TABLE_NAME
						+ " (device_id,time,isCome,content,isNew,type) "
						+ "values(?,?,?,?,?,?)",
				new Object[] { entity.getMsgDeviceid(),
						entity.getMsgTime(), entity.getMsgFlag(), entity.getMsgContent(),
						entity.getIsNew(),entity.getMsgType()});
	}

	public List<ChatMessage> getMsg(String device_id, int pager) {
		List<ChatMessage> list = new LinkedList<ChatMessage>();
		int num = 10 * (pager + 1);// 本来是准备做滚动到顶端自动加载数据
		db.execSQL("CREATE table IF NOT EXISTS _"
				+ WATCH_MESSAGE_TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "device_id TEXT, "			
				+ "time TEXT,"
				+ "isCome TEXT,"
				+ "content TEXT,"
				+ "isNew TEXT,"
				+ "type TEXT)");
		Cursor c = db.rawQuery("SELECT * from _" + WATCH_MESSAGE_TABLE_NAME
				+ " WHERE device_id = ? " 
				+ " ORDER BY _id DESC LIMIT " + num, new String[] { device_id });
		while (c.moveToNext()) {			
			ChatMessage entity = new ChatMessage();
			entity.setMsgDeviceid(c.getString(c.getColumnIndex("device_id")));
			entity.setMsgContent(c.getString(c.getColumnIndex("content")));
			entity.setMsgFlag(c.getString(c.getColumnIndex("isCome")));
			entity.setIsNew(c.getString(c.getColumnIndex("isNew")));
			entity.setMsgTime(c.getString(c.getColumnIndex("time")));
			entity.setMsgType(c.getString(c.getColumnIndex("type")));
			list.add(entity);
		}
		c.close();
		Collections.reverse(list);// 前后反转一下消息记录
		return list;
	}



//	public void clearNewCount(String ownerId, String customerId) {
//		db.execSQL("CREATE table IF NOT EXISTS _"
//				+ ownerId +"_"+ customerId
//				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
//				+ "name TEXT, "			
//				+ "date TEXT,"
//				+ "isCome TEXT,"
//				+ "message TEXT,"
//				+ "isNew TEXT,"
//				+ "from_reader_id TEXT,"
//				+ "from_bd_user_id TEXT,"
//				+ "to_reader_id TEXT,"
//				+ "tag TEXT,"
//				+ "type TEXT)");
//		db.execSQL("update _" + ownerId +"_"+ customerId + " set isNew=0 where isNew=1");
//	}
	
	public void updateNewFlag(ChatMessage entity){
		
		ContentValues cv = new ContentValues();
		cv.put("device_id", entity.getMsgDeviceid());
		cv.put("time", entity.getMsgTime());
		cv.put("isCome", entity.getMsgFlag());
		cv.put("content", entity.getMsgContent() + ".amr");
		cv.put("isNew", "0");
		cv.put("type", entity.getMsgType());

		db.update("_" + WATCH_MESSAGE_TABLE_NAME, cv, "time=?",
				new String[] { entity.getMsgTime() });
		
	}

	public void close() {
		if (db != null)
			db.close();
	}
}
