package com.smarthome.client2.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.smarthome.client2.bean.MyShareAppBean;

public class MyShareUtil {
	
	//////////////////////////////获取系统内置分享///////////////////////////////
	
	public static List<ResolveInfo> getShareTargets(Context ctx){  
        List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();  
        Intent intent=new Intent(Intent.ACTION_SEND,null);  
        intent.addCategory(Intent.CATEGORY_DEFAULT);  
        intent.setType("text/plain");  
        PackageManager pm= ctx.getPackageManager();  
        mApps=pm.queryIntentActivities(intent,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);  
        return mApps;  
    }  
    //////////////////////////////获取系统内置分享///////////////////////////////
    
	/**
	 * 分享功能
	 * 
	 * @param context
	 *            上下文
	 * @param activityTitle
	 *            Activity的名字
	 * @param msgTitle
	 *            消息标题
	 * @param msgText
	 *            消息内容
	 * @param imgPath
	 *            图片路径，不分享图片则传null
	 */
	public static void shareMsg(Context context, MyShareAppBean shareAppBean) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setComponent(new ComponentName(shareAppBean.activity_package_name,shareAppBean.activity_name));

		if (shareAppBean.imgPath == null || shareAppBean.imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(shareAppBean.imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/png");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_TITLE, shareAppBean.msgTitle);
		intent.putExtra(Intent.EXTRA_SUBJECT, shareAppBean.msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, shareAppBean.msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(Intent.createChooser(intent, activityTitle));
		context.startActivity(intent);
	}
}
