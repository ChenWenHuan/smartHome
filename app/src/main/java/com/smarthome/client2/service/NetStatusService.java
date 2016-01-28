package com.smarthome.client2.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.smarthome.client2.common.Constants;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.RequestResult;

public class NetStatusService extends Service {

	private final static int SUM_MINUTE = 3 * 1000 * 60;
	private final static int PEIROD_MINUTE = 1000 * 30;

	private int taskId = 0;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(taskId!=intent.getExtras().getInt("dataId")){
			taskId = intent.getExtras().getInt("dataId");
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean isExecute = false;
					int newId = taskId;
					long t1 = System.currentTimeMillis();
					long t2 = t1;
					while(t2-t1<SUM_MINUTE){
						isExecute = false;
						t2 = System.currentTimeMillis();
						if((t2-t1)%PEIROD_MINUTE==0){
							if(!isExecute){
								try {
									Thread.sleep(100);
									isExecute = true;
//									getCommandStatusFromServer(newId);
									Log.d("", "daitm---------taskId:"+newId+"-------running");
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
					Log.d("", "daitm---------taskId:"+newId+"-------stoped");
				}
			}).start();
			
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void getCommandStatusFromServer(int dataId) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("seriaNum", dataId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestResult result = new RequestResult();
		HttpUtil.postRequest(obj, Constants.GET_COMMAND_STATUS, result, getApplicationContext());
//		HttpUtil.postRequest(obj, Constants.GET_COMMAND_STATUS, mHandler,
//				Constants.GET_DATA_SUCCESS, Constants.GET_DATA_FAIL);
	}
}
