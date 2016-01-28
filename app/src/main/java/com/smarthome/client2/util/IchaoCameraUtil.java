package com.smarthome.client2.util;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.smarthome.client2.SmartHomeApplication;
import com.ichano.rvs.viewer.Command;
import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.callback.ViewerCallback;
import com.ichano.rvs.viewer.constant.LoginError;
import com.ichano.rvs.viewer.constant.LoginState;
import com.ichano.rvs.viewer.constant.RvsSessionState;

public class IchaoCameraUtil implements ViewerCallback{
	

	public static final String TAG = "IchaoCameraUtil";
	public static final int MSG_CAMERA_CONNECTED_ID     = 0x0080;
	public static final int MSG_CAMERA_UPDATED_ID       = 0x0081;
	public static final int MSG_CAMERA_DISCONNECTED_ID     = 0x0082;
	
	private static IchaoCameraUtil cameraInstance;
	private Viewer viewer;
	private Media media;
	private boolean isLogin = false;
	private Handler mHandler = null;
	
	public IchaoCameraUtil(){
		
	}
	
	public static IchaoCameraUtil getInstance(){
		if (cameraInstance == null){
			cameraInstance = new IchaoCameraUtil();
		}
		return cameraInstance;
	}
	
	public void initSDK(){
		viewer = Viewer.getViewer();
		viewer.init(SmartHomeApplication.getInstance(), "app v1.0", Environment.getExternalStorageDirectory().getAbsolutePath() + "/viewer",
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/viewer");// 初始化sdk
		viewer.setLoginInfo("4f5d55dad8634282a8fa8c96d4d92998", 1434611161814L, "44a369a97b764bbeb51822b59676ffb8", "");
		viewer.setCallBack(this);
		viewer.login();
	}
	
	public void login(){
		if(!isLogin){
			viewer.login();
		}
		
	}
	
	public void setHandler(Handler handle){
		this.mHandler = handle;
	}
	
	public void logout(){
		isLogin = false;
		viewer.logout();		
	}
	
	public void destroy(){
		viewer.destroy();
	}
	
	public boolean getLoginState(){
		return isLogin;
	}
	
	public Viewer getViewer(){
		return viewer;
	}

	public Media getMedia(){
		return viewer.getMedia();
	}
	
	public Command getCommand(){
		return viewer.getCommand();
	}

	@Override
	public void onLoginResult(LoginState state, int arg1, LoginError arg2) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onLoginResult state:" + state);
		if (state == LoginState.CONNECTED) {
			isLogin = true;
		}
		
	}

	@Override
	public void onSessionStateChange(long streamerCID, RvsSessionState state) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onSessionStateChange  cid:" + streamerCID +"  state:" + state);
		if (state == RvsSessionState.CONNECTED) {
			sendMessage(MSG_CAMERA_CONNECTED_ID, 0, streamerCID);
		}else if(state == RvsSessionState.DISCONNECTED){
			sendMessage(MSG_CAMERA_DISCONNECTED_ID, 0, streamerCID);
		}
	}

	@Override
	public void onUpdateCID(long cid) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onUpdateCID  cid:" + cid);
		if(mHandler != null){
			sendMessage(MSG_CAMERA_UPDATED_ID, 0, cid);
		}
	}
	
	private void sendMessage(int msgID, int arg1, long agr2){
		 
		Message msg = mHandler.obtainMessage();
         
		msg.what = msgID;
		msg.arg1 = arg1;
        msg.obj = agr2;
        msg.sendToTarget();
	}
}
