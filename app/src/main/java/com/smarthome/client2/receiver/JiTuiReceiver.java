package com.smarthome.client2.receiver;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.StartActivity;
import com.smarthome.client2.activity.WatchChatActivity_SM;
import com.smarthome.client2.bean.ChatMessage;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.unit.dao.MessageDB;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JiTuiReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	
	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();
	
	private Context ctx;
	
	private Notification mNotification;

    private NotificationManager mManager;

    private boolean isShow = false;
    
    public interface EventHandler {
		
		void onMessage(String message);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        
        this.ctx = context;
        
        initNotifiManager();
        
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            SmartHomeApplication.getInstance().jiTuiRegisterID = regId;
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	
        	handleReceivedMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 用户点击打开了通知");

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.e(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} 
			else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	private void handleReceivedMessage(Context ctx, Bundle data){
		
		String msgContent = "";
		int msgFunciton;
		String msgDeviceID = "";
		String msgDescription = "";
		String msgCb = "";
		
		msgContent = data.getString("cn.jpush.android.EXTRA");
		try {
			JSONObject json = new JSONObject(msgContent);
			msgFunciton = json.getInt("f");
			msgDeviceID = json.getString("id");
			msgDescription = json.getString("description");
			msgCb = json.getString("cb");
			
			switch(msgFunciton){
			case 2:
				handleRecordSafeZoneMessage(ctx, msgDeviceID,msgCb,msgDescription);
				break;
			case 0:
				handleBindMessage(ctx,msgDeviceID,msgCb);
			default:
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void handleRecordSafeZoneMessage(Context ctx, String deviceid, String cb, String descrip){
		
		if (!cb.split(":")[1].equals("hello_record")
				&& !cb.split(":")[1].equals("arrive")
				&& !cb.split(":")[1].equals("leave")){
			return;
		}
        if (isShow)
        {
            cancelNotification();
        }
        showNotification(deviceid, descrip);
        
        if(cb.split(":")[1].equals("arrive") || cb.split(":")[1].equals("leave")){
          MessageDB mMsgDB;
          ChatMessage msg = new ChatMessage();
	      mMsgDB = SmartHomeApplication.getInstance().getMessageDB();
	      msg.setIsNew("0");//未从网络获取音频文件
	      msg.setMsgFlag("1"); // 推送过来的消息
	      msg.setMsgContent(descrip);
	      msg.setMsgDeviceid(deviceid);
	      msg.setMsgTime(Long.toString(System.currentTimeMillis()));
	      msg.setMsgType("1");//文字信息
	      mMsgDB.saveMsg(msg);
        }
	}
	
	private void  handleBindMessage(Context ctx, String deviceid, String cb){
		
		if (!cb.split(":")[1].equals("bind")){
			return;
		}
		if(!cb.split(":")[0].equals("0")){
			return;
		}		

	}
	
	//初始化通知栏配置
    private void initNotifiManager()
    {
        mManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                ctx).setSmallIcon(R.drawable.ico_new_logo)
                .setContentTitle(ctx.getResources().getString(R.string.app_name))
                .setContentText(ctx.getResources().getString(R.string.message_alert))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setOngoing(false)
                .setAutoCancel(true);
        mNotification = builder.build();

    }

    /**
     * [弹出Notification]<BR>
     * 弹出Notification
     */
    private void showNotification(String deviceid, String msgContent)
    {
        isShow = true;
        Preferences preferences = Preferences.getInstance(ctx.getApplicationContext());

        mNotification.when = System.currentTimeMillis();
        Intent intent = new Intent();
        if (preferences.getWatchDeviceInfo(deviceid).equals("")){
        	intent.setClass(ctx, StartActivity.class);
        	intent.setAction(Intent.ACTION_MAIN);
        	intent.addCategory(Intent.CATEGORY_LAUNCHER);
        }else{
        	intent.setClass(ctx, WatchChatActivity_SM.class);
        	intent.setAction("LAUNCH_FROM_JITUI");
            intent.putExtra("deviceid", deviceid);
            
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,
        		(new Random().nextInt(100)),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.tickerText = msgContent;
		mNotification = new Notification.Builder(ctx)
				.setAutoCancel(true)
				.setContentTitle(ctx.getResources().getString(R.string.app_name))
				.setContentText(msgContent)
				.setContentIntent(pendingIntent)
				.build();

//        mNotification.setLatestEventInfo(ctx,
//                ctx.getResources().getString(R.string.app_name),
//                msgContent,
//                pendingIntent);

        mManager.notify(0, mNotification);
    }
    
    private void cancelNotification()
    {
        isShow = false;
        mManager.cancel(0);
    }

}

