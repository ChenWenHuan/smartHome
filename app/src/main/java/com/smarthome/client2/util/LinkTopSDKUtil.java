package com.smarthome.client2.util;


import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.common.Constants;
import com.linktop.API.CSSResult;
import com.linktop.csslibrary.CssHttpUtils;
import com.linktop.csslibrary.RegisterInteface;
import com.linktop.requestParam.FileEnum;
import com.linktop.requestParam.UploadParam;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class LinkTopSDKUtil {
	
	public static final String ACCOUNT_HEAD_STRING = "zy_";
	
	public static final int LINK_SDK_REGISTER_ACCOUNT_ACTION  	= 0x80000;
	public static final int LINK_SDK_ACTIVE_ACCOUNT_ACTION  	= 0x80001;
	public static final int LINK_SDK_BIND_DEVICE_ACTION 		= 0x80002;
	public static final int LINK_SDK_LATEST_POS_ACTION 			= 0x80003;
	public static final int LINK_SDK_COMMU_TXT_ACTION 			= 0x80004;
	public static final int LINK_SDK_UNBIND_DEVICE_ACTION     	= 0x80005;
	public static final int LINK_SDK_START_LOC_ACTION 			= 0x80006;
	public static final int LINK_SDK_LOGIN_TOKEN 				= 0x80007;
	public static final int LINK_SDK_REGISTER_JITUI_PUSH 		= 0x80008;
	public static final int LINK_SDK_UPLOAD_AUDIO       		= 0x80009;
	public static final int LINK_SDK_EDIT_WITE_LIST             = 0x80010;
	public static final int LINK_SDK_NOR_LOCATION_TOKEN         = 0x80011;
	public static final int LINK_SDK_GET_LOCATION_BY_TOKEN      = 0x80012;
	public static final int LINK_SDK_GET_TIMELINE               = 0x80013;
	public static final int LINK_SDK_GET_HISTORY_LOCATION       = 0x80014;
	public static final int LINK_SDK_ADD_SAFE_ZONE              = 0x80015;
	public static final int LINK_SDK_GET_SAFE_ZONE              = 0x80016;
	public static final int LINK_SDK_DEL_SAFE_ZONE              = 0x80017;
	public static final int LINK_SDK_EDT_SAFE_ZONE              = 0x80018;
	public static final int LINK_SDK_GET_HEALTH_STEPS           = 0x80019;
	public static final int LINK_SDK_LIST_DEVICES               = 0x80020;
	public static final int LINK_SDK_SEND_ALIAS                 = 0x80021;
	
	
	
	private static LinkTopSDKUtil instance;
	public static String key = "902fe5ead37a4e81ad192b924d8faad2";
	public static String secrect = "f88cd4fa661b4d2f95e1ae568d903a89";
	private Handler mHandle = null;
	private Context mContext = null;
	private CssHttpUtils cssHttpUtils;
	
	
	public LinkTopSDKUtil(){
		
	}
	
	public static LinkTopSDKUtil getInstance(){
		
		if (instance == null){
			instance = new LinkTopSDKUtil();
		}
		return instance;
	}
	
	public void initSDK(Context ctx, Handler handle){
		this.mContext = ctx;
		this.mHandle = handle;
		CssHttpUtils.getInstance(ctx).appKey = key;
		CssHttpUtils.getInstance(ctx).appSecret = secrect;
	}
	
	public void setHandler(Handler handler){
		
		this.mHandle = handler;
		
	}
	
	public void setupAccount(String accout, String pwd){
		
		if (mContext != null){
			cssHttpUtils = CssHttpUtils.getInstance(mContext).setupCSSApiWithUnamePsw(ACCOUNT_HEAD_STRING + accout, pwd);
		}else{
			Log.e("LinkTopSDKUtil", " setupAccount mContext == null ");
		}
	}
	
	public void loginToken()
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				
				CSSResult<Integer,String> loginToken = CssHttpUtils.getInstance(mContext).getLoginToken();
				Log.e("loginToken "+loginToken.getStatus(), ""+loginToken.getResp());
				sendResultByHandle(LINK_SDK_LOGIN_TOKEN, loginToken.getStatus(),
						loginToken.getResp());
			}
			
		}).start();
	}
	
	public void bindDevice(final String qrCode, final String watchNum){
		new Thread(new Runnable(){
			@Override
			public void run() {
				CSSResult<Integer,String> judgeResult = CssHttpUtils.getInstance(mContext).judgeSettingIOT(qrCode);
				Log.e("judgeResult "+judgeResult.getStatus(), ""+judgeResult.getResp());
				CSSResult<Integer,String> sendWatchResult = CssHttpUtils.getInstance(mContext).sendWatchNum(watchNum,qrCode);
				Log.e("sendWatchResult "+sendWatchResult.getStatus(), ""+sendWatchResult.getResp());
				// TODO Auto-generated method stub
				CSSResult<Integer,String> bindResult = CssHttpUtils.getInstance(mContext).bindDevice(qrCode);
				Log.e("--bindResult--- "+bindResult.getStatus(), ""+ bindResult.getResp());
				sendResultByHandle(LINK_SDK_BIND_DEVICE_ACTION, bindResult.getStatus(),
																bindResult.getResp());
			}		
		}).start();
	}
	
	public void sendRecordSMSToken(final String deviceid,  final String telNum, final int recordduration){
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				String smsTk = "";
				CSSResult<Integer,String> getSMSTokenResult = CssHttpUtils.getInstance(mContext).getSmsTk(deviceid, true);
				Log.e("getSmsTk "+getSMSTokenResult.getStatus(), ""+getSMSTokenResult.getResp());
				if (getSMSTokenResult.getStatus() == 200){
					String resp = getSMSTokenResult.getResp();
					if (!resp.equals("")){
						try {
							JSONObject jsonObject = new JSONObject(resp);						
							smsTk = jsonObject.getString("tk_sn");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				Log.e("-sendRecordSMSToken-", "--smsTk=" + smsTk);
				new PhoneUtil(mContext).sendMSGNoReg(telNum, "record:record:" + smsTk);
			}		
		}).start();
		
	}
	
public void sendNorLocationSMSToken(final String deviceid,  final String telNum){
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				String smsTk = "";
				CSSResult<Integer,String> getSMSTokenResult = CssHttpUtils.getInstance(mContext).getSmsTk(deviceid, true);
				Log.e("getSmsTk "+getSMSTokenResult.getStatus(), ""+getSMSTokenResult.getResp());
				if (getSMSTokenResult.getStatus() == 200){
					String resp = getSMSTokenResult.getResp();
					if (!resp.equals("")){
						try {
							JSONObject jsonObject = new JSONObject(resp);						
							smsTk = jsonObject.getString("tk_sn");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				//"urgent_loc:urgent_loc:"
				Log.e("-sendNorLocationSMSToken-", "--smsTk=" + smsTk);
				new PhoneUtil(mContext).sendMSGNoReg(telNum, "normal_loc:normal_loc:" + smsTk);
				sendResultByHandle(LINK_SDK_NOR_LOCATION_TOKEN, 200, smsTk);
			}		
		}).start();
		
	}
	
	public void bindDeviceByID(final String deviceid, final String watchNum, final String akey){
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				CSSResult<Integer,String> judgeResult = CssHttpUtils.getInstance(mContext).judgeSettingIOTById(deviceid, akey);
				Log.e("judgeSettingIOTById "+judgeResult.getStatus(), ""+judgeResult.getResp());
				CSSResult<Integer,String> sendWatchResult = CssHttpUtils.getInstance(mContext).sendWatchNumById(watchNum,deviceid, akey);
				Log.e("sendWatchNumById "+sendWatchResult.getStatus(), ""+sendWatchResult.getResp());
				CSSResult<Integer,String> bindResult = CssHttpUtils.getInstance(mContext).bindDeviceByid(deviceid, akey);
				Log.e("bindDeviceByid "+bindResult.getStatus(), ""+bindResult.getResp());
				sendResultByHandle(LINK_SDK_BIND_DEVICE_ACTION, bindResult.getStatus(),
																bindResult.getResp());
			}		
		}).start();
		
	}
	
	public void registerPushParam(final String registID){
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				CSSResult<Integer,String> pushResult = CssHttpUtils.getInstance(mContext).sendPushPara(registID);
				Log.e("---registerPushParam---pushResult "+pushResult.getStatus(), ""+pushResult.getResp());

				sendResultByHandle(LINK_SDK_REGISTER_JITUI_PUSH, pushResult.getStatus(),
						pushResult.getResp());
			}		
		}).start();
		
	}
	
	public void unBindDevice(final String deviceID){
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int unbindResult = CssHttpUtils.getInstance(mContext).unbindDevice(deviceID);
				sendResultByHandle(LINK_SDK_UNBIND_DEVICE_ACTION, unbindResult, "");
			}		
		}).start();
	}
	
	public void getLatestPosition(final String deviceID){
		new Thread(new Runnable(){

			@Override
			public void run() {
				CSSResult<Integer, String> gpsLoc = CssHttpUtils.getInstance(mContext).getGPSLoc(deviceID, true);
				Log.e("gpsLoc "+gpsLoc.getStatus(), ""+gpsLoc.getResp());
				sendResultByHandle(LINK_SDK_LATEST_POS_ACTION, gpsLoc.getStatus(),
						gpsLoc.getResp());
			}
			
		}).start();
	}
	
	public void getLocationByToken(final String deviceid, final String token){
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				String ipuntToken = null;
				ipuntToken = Integer.toString(Integer.parseInt(token, 16));
				CSSResult<Integer, String> gpsLoc = CssHttpUtils.getInstance(mContext).getGPSLoc(deviceid, ipuntToken,true);
				Log.e("gpsLocByToken "+gpsLoc.getStatus(), ""+gpsLoc.getResp());
				String strResult = "";
				if (!gpsLoc.getResp().equals("{}") && !TextUtils.isEmpty(gpsLoc.getResp())){
					strResult = gpsLoc.getResp();
				}
				sendResultByHandle(LINK_SDK_LATEST_POS_ACTION, gpsLoc.getStatus(),strResult);
			}
			
		}).start();
		
	}
	
	public void starLocationWatch(final String deviceID){
		new Thread(new Runnable(){

			@Override
			public void run() {
				//��ȡGPS��
				CSSResult<Integer, String> startLoc = CssHttpUtils.getInstance(mContext).startLocWatch(deviceID, true);
				Log.e("gpsLoc "+startLoc.getStatus(), ""+startLoc.getResp());
				sendResultByHandle(LINK_SDK_START_LOC_ACTION, startLoc.getStatus(),
						startLoc.getResp());
			}
			
		}).start();
	}
	
	public void sentTxtMsg(final String deviceID, final String txtMsg){
		new Thread(new Runnable(){

			@Override
			public void run() {
				CSSResult<Integer,String> babyInteraction = CssHttpUtils.getInstance(mContext).babyInteraction(deviceID, txtMsg);
				Log.e(" babyInteraction", babyInteraction.getStatus() + " " + babyInteraction.getResp());
				sendResultByHandle(LINK_SDK_COMMU_TXT_ACTION, babyInteraction.getStatus(),
						babyInteraction.getResp());
			}
		}).start();
	}
	
	public void sentSMSMsg(final String deviceTelNum, final String txtMsg){
		new Thread(new Runnable(){

			@Override
			public void run() {
				new PhoneUtil(mContext).sendMSGNoReg(deviceTelNum, txtMsg);
			}
		}).start();
	}
	
	public void getTimeLineData(final String deviceID, final int startIndex){
		new Thread(new Runnable(){

			@Override
			public void run() {
				CSSResult<Integer,String> timeline = CssHttpUtils.getInstance(mContext).timeline(deviceID, startIndex, true);
				Log.e(" get timeline", timeline.getStatus() + " " + timeline.getResp());
				sendResultByHandle(LINK_SDK_GET_TIMELINE, timeline.getStatus(),
						timeline.getResp());
			}
		}).start();
	}
	
	
	
	public void registerAccount(final String account, final String pwd){
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				int state = 10;
				String[] resp = RegisterInteface.register(key, secrect, ACCOUNT_HEAD_STRING + account, account,pwd);
				/*
				 *  0	注册成功
					1	解密出错
					2	帐号参数缺失
					3	手机号参数缺失
					4	密码参数缺失
					5	手机号需短信验证（该状态仅出现于直接使用凌拓帐号的情况）
					6	帐号重复
					7	帐号格式错误
					8	帐号来源错误，非oem帐号
					9	帐号格式禁止为email
				*/
				if (resp[0].equals("200") && resp[1] != null)
				{
					try {
						JSONObject jsonObject = new JSONObject(
								resp[1]);						
						state = jsonObject.getInt("state");
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
				sendResultByHandle(LINK_SDK_REGISTER_ACCOUNT_ACTION, state, resp[1]);
			}
		}).start();
		
	}
	
	public void activeAccount(final String account, final String pwd, final String valcode){
		new Thread(new Runnable(){

			@Override
			public void run() {
				int state = 10;
			    // 输入注册时的验证码
				String[] valResult = RegisterInteface.activeAccount(key, secrect,account, pwd, valcode);
				/*
				 * 	0	帐号激活成功
					1	开发者key无需验证激活
					2	code不正确
					3	帐号格式错误
					4	解密错误
					5	参数缺失或key错误
					*/
				Log.e("---activeAccount----", "-valResult[0]=" + valResult[0] + "-valResult[1]=" + valResult[1]);
				if (valResult[0].equals("200") && valResult[1] != null)
				{
					try {
						JSONObject jsonObject = new JSONObject(
								valResult[1]);						
						state = jsonObject.getInt("state");
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
				sendResultByHandle(LINK_SDK_ACTIVE_ACCOUNT_ACTION, state, valResult[1]);
			}
		}).start();
	}
	
	
	public byte[] getFileNameOfAudio(String deviceId, String tk) {
        CSSResult<Integer, String> audioResource = CssHttpUtils.getInstance(mContext).getAudioResource(deviceId, tk, true);
        String fileName = "";
        String audioReceipt = "";
        if(audioResource.getStatus() == 200) {
            try {
                JSONObject jsonObject = new JSONObject(audioResource.getResp());
                fileName = jsonObject.getString("fn");
                audioReceipt = jsonObject.getString("r");
                Log.e("--getFileNameOfAudio---", "--filename=" + fileName + "--receipt=" + audioReceipt);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /**
             * 根据文件名和凭证下载录音文件
             */
            return downloadAudioFile(deviceId, fileName, audioReceipt);
        }else{
            //根据token下载录音所需的文件名和凭证失败
            return null;
        }
    }
    
    private byte[] downloadAudioFile(String deviceId, String fileName, String audioReceipt) {
        byte[] audioFile = null;
        audioFile = CssHttpUtils.getInstance(mContext).downloadFile(deviceId, audioReceipt, fileName, 1 + "");
        return audioFile;
    }
    
    //此处的filename 无需包括文件路径和文件后缀名，文件必须是amr的格式
    public void uploadAudioFile(final String deviceid, final String filename){
    	
    	new Thread(new Runnable(){
			
			@Override
			public void run() {
				upLoadAudioFile(deviceid,filename);
			}
		}).start();
    }
    
    public void editWiteNameList(final String deviceid, final String nameslist){
    	new Thread(new Runnable(){
			
			// 命令字   1：删除  2: 添加
			@Override
			public void run() {
				CSSResult<Integer,String> editWhiteListResult = CssHttpUtils.getInstance(mContext).editWhiteListMulti(deviceid, nameslist);
				Log.e("editWhiteListResult "+editWhiteListResult.getStatus(), ""+editWhiteListResult.getResp());
				sendResultByHandle(LINK_SDK_EDIT_WITE_LIST, editWhiteListResult.getStatus(),
						editWhiteListResult.getResp());
			}
		}).start();
    }
    
    
    
    private  void upLoadAudioFile(String deviceID, String filename){
    	
    	byte[] buffer = null;
    	UploadParam params = new UploadParam();
    	params.deviId = deviceID;
    	params.fn = filename;
    	params.share = FileEnum.SHAREDFILE;
    	params.src = "4";
    	params.usage = "voice";
    	
    	try{
    		File file = new File(Constants.WATCH_REACORD_AUDIO_PATH + filename + ".amr");
            FileInputStream fin = new FileInputStream(file);
            int length = fin.available();
            buffer = new byte[length];
            fin.read(buffer);    
            fin.close();    
        }
        catch(Exception e){
            e.printStackTrace();
        }
    	if (buffer != null){
	    	String[] uploadfileResult = CssHttpUtils.getInstance(mContext).upload(params, buffer);
	    	Log.e("--upLoadAudioFile-", " -upfileresult[0]=" + uploadfileResult[0] + "--upfileresult[1]=" + uploadfileResult[1]);
    	}
    }
    
    public void getHistoryLocation(final String deviceid, final String date){
    	
    	final String beginDate = date + "T" + "06:00:00";
    	final String endDate = date + "T" + "22:00:00";
    	Log.e("getHistoryLocation", "date=" + date);
    	new Thread(new Runnable(){			
			
			@Override
			public void run() {
				CSSResult<Integer,String> getHistoryResult = CssHttpUtils.getInstance(mContext).getGPSHistory(deviceid, beginDate,endDate, true);
				Log.e("getHistoryResult "+getHistoryResult.getStatus(), ""+getHistoryResult.getResp());
				sendResultByHandle(LINK_SDK_GET_HISTORY_LOCATION, getHistoryResult.getStatus(),
						getHistoryResult.getResp());
			}
		}).start();
    }
    
    public void addSafeZone(final String  deviceID, final HashMap<String, String> hashMap){
    	
    	if(hashMap != null){
    		hashMap.put("action", "0");
    	}else{
    		return;
    	}
    	new Thread(new Runnable(){			
			
			@Override
			public void run() {
				CSSResult<Integer,String> addSafeZone = CssHttpUtils.getInstance(mContext).setSafeZone(deviceID, hashMap);
				Log.e("addSafeZone "+addSafeZone.getStatus(), ""+addSafeZone.getResp());
				sendResultByHandle(LINK_SDK_ADD_SAFE_ZONE, addSafeZone.getStatus(),
						addSafeZone.getResp());
			}
		}).start();
    	
    }
    
    public void delSafeZone(final String  deviceID, String safeZoneid){
    	
    	
    	final HashMap<String, String> hashMap = new HashMap<String, String>();
    	hashMap.put("action", "1");
    	hashMap.put("id", safeZoneid);

    	new Thread(new Runnable(){			
			
			@Override
			public void run() {
				CSSResult<Integer,String> delSafeZone = CssHttpUtils.getInstance(mContext).setSafeZone(deviceID, hashMap);
				Log.e("delSafeZone "+delSafeZone.getStatus(), ""+delSafeZone.getResp());
				sendResultByHandle(LINK_SDK_DEL_SAFE_ZONE, delSafeZone.getStatus(),
						delSafeZone.getResp());
			}
		}).start();
    	
    }
    
    public void updateSafeZone(final String  deviceID, final HashMap<String, String> hashMap){
    	
    	if(hashMap != null){
    		hashMap.put("action", "2");
    	}else{
    		return;
    	}
    	new Thread(new Runnable(){			
			
			@Override
			public void run() {
				CSSResult<Integer,String> delSafeZone = CssHttpUtils.getInstance(mContext).setSafeZone(deviceID, hashMap);
				Log.e("delSafeZone "+delSafeZone.getStatus(), ""+delSafeZone.getResp());
				sendResultByHandle(LINK_SDK_EDT_SAFE_ZONE, delSafeZone.getStatus(),
						delSafeZone.getResp());
			}
		}).start();
    	
    }
    
    public void getSafeZone(final String  deviceID){    	

    	new Thread(new Runnable(){			
			
			@Override
			public void run() {
				CSSResult<Integer,String> getSafeZone = CssHttpUtils.getInstance(mContext).getSafeZone(deviceID);
				Log.e("getSafeZone "+getSafeZone.getStatus(), ""+getSafeZone.getResp());
				sendResultByHandle(LINK_SDK_GET_SAFE_ZONE, getSafeZone.getStatus(),
						getSafeZone.getResp());
			}
		}).start();
    	
    }
	
    public void getHealthStep(final String deviceid){
    	
    	new Thread(new Runnable(){			
			
			@Override
			public void run() {
				CSSResult<Integer,String> getHealthSteps = CssHttpUtils.getInstance(mContext).getStepStateNew(deviceid,DateUtil.dateToWeek());
				Log.e("getHealthSteps "+getHealthSteps.getStatus(), ""+getHealthSteps.getResp());
				sendResultByHandle(LINK_SDK_GET_HEALTH_STEPS, getHealthSteps.getStatus(),
						getHealthSteps.getResp());
			}
		}).start();
    }
    
    public void getDevices(){
    	
    	new Thread(new Runnable(){			
			
			@Override
			public void run() {
				CSSResult<Integer,String> deviceListSyncResult = CssHttpUtils.getInstance(mContext).deviceListSync();
				Log.e("deviceListSyncResult "+deviceListSyncResult.getStatus(), ""+deviceListSyncResult.getResp());
				sendResultByHandle(LINK_SDK_LIST_DEVICES, deviceListSyncResult.getStatus(),
						deviceListSyncResult.getResp());
			}
		}).start();
    }
    
    public void sendAlias(String deviceid, String alias){
    	final HashMap<String, String> hashMap = new HashMap<String, String>();
    	hashMap.put("alias", alias);
    	hashMap.put("id", deviceid);
    	
    	new Thread(new Runnable(){			
			
			@Override
			public void run() {
				CSSResult<Integer,String> sendAliasResult = CssHttpUtils.getInstance(mContext).sendAlias(hashMap);
				Log.e("sendAliasResult "+sendAliasResult.getStatus(), ""+sendAliasResult.getResp());
				sendResultByHandle(LINK_SDK_SEND_ALIAS, sendAliasResult.getStatus(),
						sendAliasResult.getResp());
			}
		}).start();
    }
    

    
    
	private void sendResultByHandle(int actionID, int resultState, String resultResp){
		 
		Message msg = mHandle.obtainMessage();
         
		msg.what = actionID;
		msg.arg1 = resultState;
        msg.obj = resultResp;
        msg.sendToTarget();
	}

}
