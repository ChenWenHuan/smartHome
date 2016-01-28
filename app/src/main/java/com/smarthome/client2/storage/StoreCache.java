package com.smarthome.client2.storage;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;

import com.smarthome.client2.bean.BaseBean;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.manager.DBManager;

/**
 * 
 * @author xiaolong.zhang
 * 从服务器获取的数据，将其缓存，在解析器解析完成时存入
 */
public class StoreCache {

	private BaseBean data;
	private final static byte[] LOCK = new byte[0];
	private  DBManager db;

	private static StoreCache ins = null;

	private  Context ctx;

	private StoreCache(Context c) {
		ctx = c;
		db = DBManager.open(ctx);
	}

	public static final StoreCache getInstance(Context c) {
		return ins == null ? (ins = new StoreCache(c.getApplicationContext())) : ins;
	}

	public boolean store2CacheDB(final BaseBean dataObject) {

		synchronized (LOCK) {
			this.data = dataObject;
			new Thread() {
				public void run() {
					doCache(data);
				}
			}.start();
		}
		return true;

	}

	private boolean doCache(BaseBean data) {
		try {
			/*
			 * 提供调用的方法列表
			 */

			// 获取存储Bean到数据库的缓存键值
			// data.getCacheKey() ;
			// bean转化为String之后的值
			// data.beanToString();

			// 缓存键值：
			String cacheKey = data.getKey();
			// TLog.Log("cacheKey:"+cacheKey);
			if (isEmpty(cacheKey)) {
				cacheKey = "-1";
			}
			// 缓存内容
			String content = data.beanToString();
			if (isEmpty(content)) {
				content = "-1";
			}
			//

			// 存储逻辑
			// 1 . 还没有这个Key对应的行，插入之
			// 2 . 已经有了这样的行，更新之
			if (db == null) {
				db = DBManager.open(ctx);
			}

			if (!cacheKey.equals("-1")) {
				cacheKey = cacheKey + "#" + Preferences.getInstance(ctx).getUserID();
			}

			Cursor c = db.getCache(cacheKey);

			if (c != null && c.moveToFirst()) {
				String beanString = c.getString(2);
				c.close();
				if (isEmpty(beanString)) {
					doInsert(cacheKey, content);
				} else {
					doUpdate(cacheKey, content);
				}
			} else {
				c.close();
				doInsert(cacheKey, content);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void doUpdate(String cacheKey, String content) {
		if (db == null) {
			db = DBManager.open(ctx);
		}
		db.updateToCache(cacheKey, content, new Date().getTime());
	}

	private void doInsert(String cacheKey, String content) {
		if (db == null) {
			db = DBManager.open(ctx);
		}
		db.insertToCache(cacheKey, content, new Date().getTime());
	}

	public boolean isEmpty(String s) {
		if (s == null) {
			return true;
		} else {
			s = s.trim();
			return "".equals(s) || s.length() == 0;
		}
	}
}
