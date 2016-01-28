package com.smarthome.client2.manager;

import java.util.Stack;

import com.smarthome.client2.activity.MainActivity;
import com.smarthome.client2.common.TLog;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * 应用程序Activity管理类
 * 
 * @author liux
 */
public class AppManager {

	private  Stack<Activity> activityStack;
	private static AppManager instance;

	private AppManager() {
		activityStack = new Stack<Activity>();
	}

	/**
	 * 单一实例
	 */
	public static AppManager getAppManager() {
		if (instance == null) {
			instance = new AppManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		TLog.Log("############add"+activityStack.size());
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		if (activity != null) {
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				TLog.Log("zxl---appmanager---finish--->"+activityStack.get(i));
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
		} catch (Exception e) {
		}
	}
	
	/**
	 * 移除索引
	 * 
	 * @param activity
	 */
	public void removeActivity(Activity activity) {
		activityStack.remove(activity);
	}
	
	/**
	 * 仅仅保留MainActivity，移除其他Activity索引，并finish掉
	 */
	public void removeOthers(){
		TLog.Log("############"+activityStack.size());
		for (int i = 1, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
	}
	
	/**
	 * 栈底是否是MainActivity
	 * 
	 * @return
	 */
	public boolean rootIsMain(){
		return !activityStack.isEmpty() && (activityStack.get(0) instanceof MainActivity);
	}
	
	public MainActivity getMainActivity(){
		return (MainActivity) activityStack.get(0);
	}
}
