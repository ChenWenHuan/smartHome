package com.smarthome.client2.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.StartActivity;
import com.smarthome.client2.bean.ChatMessage;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.familySchool.adapter.WatchMessageAdapter;
import com.smarthome.client2.familySchool.utils.FsConstants;
import com.smarthome.client2.familySchool.utils.HttpJson;
import com.smarthome.client2.familySchool.utils.MyHttpUtil;
import com.smarthome.client2.unit.dao.MessageDB;
import com.smarthome.client2.util.DateUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.SoundMeter;
import com.smarthome.client2.widget.xlistview.MsgListView;
import com.smarthome.client2.widget.xlistview.MsgListView.IXListViewListener;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;

public class WatchChatActivity_SM extends Activity implements
		OnTouchListener, OnClickListener,IXListViewListener{
	
    public final static String CMD_VOICE = "voice";// 语音
    public final static String CMD_RECORD = "record";// 远程监听
    public final static String CMD_HELLO_RECORD = "hello_record";// 普通录音
    public final static String CMD_SOS_RECORD = "sos_record";// sos紧急录音
    public final static int MSG_LIST_REFRESH = 0x900001;
	private SmartHomeApplication mApplication;
	private static int MsgPagerNum;
	private MessageDB mMsgDB;
	private Button mBtnSend;
	private TextView mBtnRcd;


	private ImageView mImgBack;
	private EditText mEditTextContent;
	private RelativeLayout mBottom;
	private InputMethodManager imm;
	private WatchMessageAdapter mMsgAdapter;
	private MsgListView mMsgListView;
	private List<ChatMessage> mMsgListData;
	private boolean isShosrt = false;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
			voice_rcd_hint_tooshort;
	private ImageView img1, sc_img1;
	private SoundMeter mSensor;
	private View rcChat_popup;
	private LinearLayout del_re;
	private ImageView chatting_mode_btn, volume;
	private boolean btn_vocie = false;
	private int flag = 1;
	private TextView   tv_head_title;
	private Handler mHandler = new Handler();
	private String voiceName;
	private long startVoiceT, endVoiceT;
	private String mStrDevId;
	private String mStrDevAccount;
	 private LinkTopSDKUtil linkInstance = null;
	private String mTitle;
	private int deviceType = 0;
	private String deviceTelNum = "";
	private String familyID = "";
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private String fromImgUrl; 
	private String headImgUrl;
	private Preferences preferences;
	private String startAction = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.watch_chat_main);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		preferences = Preferences.getInstance(this.getApplicationContext());
		startAction = this.getIntent().getAction();
		Log.e("---WatchChatActivity_SM---", "onCreate startAction=" + startAction);
		if (startAction.equals("")){
			headImgUrl = preferences.getHeadPath();
			mStrDevId= this.getIntent().getStringExtra("deviceid");
			mStrDevAccount= this.getIntent().getStringExtra("deviceaccount");
			mTitle = this.getIntent().getStringExtra("title");
			int msgSendTarget = this.getIntent().getIntExtra(FsConstants.TYPE_ADD_FLAG, 0);
			if(msgSendTarget == FsConstants.TYPE_SEND_PERSONAL_MSG){
				initLinkSDK(mStrDevAccount);
				fromImgUrl = this.getIntent().getStringExtra("headimg");
				deviceTelNum= this.getIntent().getStringExtra("deviceNum");
				deviceType = 6;
			}else{
				familyID = this.getIntent().getStringExtra("ids");
				deviceType = 4;
			}
			saveDeviceInfo(mStrDevId);
		}else{
			mStrDevId= this.getIntent().getStringExtra("deviceid");
			getParserDeviceInfo(mStrDevId);
			initLinkSDK(mStrDevAccount);
			deviceType = 6;
		}
		
		initData();
		initView();
	}
	
	private void saveDeviceInfo(String deviceID){
		//设置登录请求参数
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("headImgUrl", headImgUrl);
            obj.put("account", mStrDevAccount);
            obj.put("title", mTitle);
            obj.put("fromImgUrl", fromImgUrl);
            obj.put("watchTelNum", deviceTelNum);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }	
        preferences.setWatchDeviceInfo(deviceID, obj.toString());
	}
	
	
	private void getParserDeviceInfo(String deviceID){
		
		String deviceInfo = preferences.getWatchDeviceInfo(deviceID);
		if (!TextUtils.isEmpty(deviceInfo)){
			try {
				JSONObject jsonObject = new JSONObject(deviceInfo);
				headImgUrl = jsonObject.getString("headImgUrl");
				mStrDevAccount = jsonObject.getString("account");
				mTitle = jsonObject.getString("title");
				fromImgUrl = jsonObject.getString("fromImgUrl");
				deviceTelNum = jsonObject.getString("watchTelNum");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    private void initLinkSDK(String linkaccount){
    	
    	linkInstance = LinkTopSDKUtil.getInstance();
    	linkInstance.initSDK(this, mLinkTopHandler);
    	linkInstance.setupAccount(linkaccount, "888888");

    }
    
    private void getTimeLineData(String deviceid){
    	String currentDay = "";
    	String savedDay = "";
    	int startIndex = 0;
    	currentDay = DateUtil.getInstance().getCurrentYearMonthDay();
    	savedDay = preferences.getTimeLineDay();
    	if (currentDay.equals(savedDay)){
    		startIndex = preferences.getTimeLineIndex(mStrDevId);
    	}else{
    		preferences.setTimeLineDay(currentDay);
    	}
    	Log.e("--getTimeLineData--", "currentDay=" + currentDay + "--savedDay=" +savedDay + "--startIndex=" + startIndex);
    	linkInstance.getTimeLineData(mStrDevId, startIndex);
    }
    
    private Handler mLinkTopHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case LinkTopSDKUtil.LINK_SDK_COMMU_TXT_ACTION:
                	Log.e("-COMMU_TXT ACTION-", "-arg1=" + msg.arg1 + "--obj=" + msg.obj);
                	if(msg.arg1 == 200){

                	}else{

                	}
                	break;
                case MSG_LIST_REFRESH:
                	mMsgAdapter.notifyDataSetChanged();
                	break;
                case LinkTopSDKUtil.LINK_SDK_UPLOAD_AUDIO:
                	linkInstance.sendRecordSMSToken(mStrDevId, deviceTelNum, 10);
                	break;
                case LinkTopSDKUtil.LINK_SDK_GET_TIMELINE:
                	if(msg.arg1 == 200){
	                	handleTimeLineDataRespone(msg.obj.toString());
	                	mLinkTopHandler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								getTimeLineData(mStrDevId);
							}
						}, 10000);
                	}
                	break;
                default:
                    break;
            }
        }
    };
    
    private void handleTimeLineDataRespone(String response){
    	try {
            JSONObject jsonObj = new JSONObject(response);
            /**
             * "end" : 与start对应，本次获取的end值作为下次调用接口时传入的start值
             * "list" : timeline 列表
             * */
            int end = jsonObj.optInt("end");
            String list = jsonObj.optString("list");
            // 将end保存起来
            preferences.setTimeLineIndex(mStrDevId,end);
            JSONArray jsonArr = new JSONArray(list);
            int length = jsonArr.length();
            /***
             *  by  对应用户的account，即该条消息是哪个用户发的；若by为空，则为设备端发来的
             *  token  每条信息都有自己的token作为唯一标识
             *  cmd  信息的类型，多种，但可总体分为两种种：文本，语音
             *       对于文本，可分为文字信息和各式命令（远程关机，普通定位，紧急定位等..）
             *       对于语音，分为语音信息（手表主动录音，手表SOS录音，用户发的语音）和命令录音（即远程监听）
             *  ts  时间戳，即该条信息的发送时间
             * */
            if (length != 0) {

                for (int i = 0; i < length; i++) {
                    JSONObject jo = jsonArr.optJSONObject(i);
                    String by = jo.optString("by");
                    String token = jo.optString("token");
                    String cmd = jo.optString("cmd");
                    String text = jo.optString("text");
                    long ts = jo.optLong("ts");
                    if (cmd.equals(CMD_HELLO_RECORD)){
                    	// 加入当前消息列表
                    	// 写入本地数据库 
                    	ChatMessage msg = new ChatMessage();
                		mMsgDB = SmartHomeApplication.getInstance().getMessageDB();
                		msg.setIsNew("1");//未从网络获取音频文件
                		msg.setMsgFlag("1"); // 推送过来的消息
                		msg.setMsgContent(token);
                		msg.setMsgDeviceid(mStrDevId);
                		msg.setMsgTime(Long.toString(ts*1000));
                		msg.setMsgType("2");//声音文件
                		mMsgAdapter.upDateMsg(msg);
                		mMsgListView.setSelection(mMsgAdapter.getCount() - 1);
                		mMsgDB.saveMsg(msg);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
    

    
    private void sendMMSToDevice(String deviceTelNum, String msgContent){
    	
    	linkInstance.sentSMSMsg(deviceTelNum, msgContent);
    }

	public void initView() {
		
		tv_head_title = (TextView)this.findViewById(R.id.tv_head_bar_title);
		tv_head_title.setText(mTitle);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mMsgListView = (MsgListView) findViewById(R.id.listview);
		mMsgListView.setOnTouchListener(this);
		mMsgListView.setPullLoadEnable(false);
		mMsgListView.setXListViewListener(this);
		mMsgListView.setAdapter(mMsgAdapter);
		if (mMsgAdapter.getCount() >= 1) {
			mMsgListView.setSelection(mMsgAdapter.getCount() - 1);
		}
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
		mBtnSend.setOnClickListener(this);
		mImgBack = (ImageView) findViewById(R.id.img_head_bar_icon);
		mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
		mImgBack.setOnClickListener(this);
		chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
		volume = (ImageView) this.findViewById(R.id.volume);
		rcChat_popup = this.findViewById(R.id.rcChat_popup);
		img1 = (ImageView) this.findViewById(R.id.img1);
		sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
		del_re = (LinearLayout) this.findViewById(R.id.del_re);
		voice_rcd_hint_rcding = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_tooshort);
		mSensor = new SoundMeter();
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		
		
		chatting_mode_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				if(deviceType == 4){
					Toast.makeText(WatchChatActivity_SM.this,
							"智能机暂不支持语音功能！",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (btn_vocie) {
					mBtnRcd.setVisibility(View.GONE);
					mBottom.setVisibility(View.VISIBLE);
					btn_vocie = false;
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_msg_btn);

				} else {
					mBtnRcd.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.GONE);
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_voice_btn);
					btn_vocie = true;
				}
			}
		});
		mBtnRcd.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				//��������¼�ư�ťʱ����falseִ�и���OnTouch
				return false;
			}
		});
	}
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
		}
	};


	public void initData() {
		MsgPagerNum = 0;
		mApplication = SmartHomeApplication.getInstance();
		mMsgDB = mApplication.getMessageDB();
		mMsgListData = initMsgData();
		mMsgAdapter = new WatchMessageAdapter(this, handler, mMsgListData,fromImgUrl,headImgUrl);
		if (deviceType == 6){
			getTimeLineData(mStrDevId);
		}

	}
	
	/**
	 * 加载消息历史，从数据库中读出
	 */
	private List<ChatMessage> initMsgData() {
		List<ChatMessage> list = null;
		List<ChatMessage> msgList = new ArrayList<ChatMessage>();// 消息对象数组
		if (deviceType == 6){
			list = mMsgDB.getMsg(mStrDevId, MsgPagerNum);
		}else{
			list = mMsgDB.getMsg(familyID, MsgPagerNum);
		}
		if (list.size() > 0) {
			for (ChatMessage entity : list) {
				msgList.add(entity);
			}
		}
		return msgList;

	}
	
	
	
	
	

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		case R.id.img_head_bar_icon:
			finish();
			break;
		}
	}

	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMessage msg = new ChatMessage();
			msg.setIsNew("0");//已读
			msg.setMsgFlag("0"); // 发送消息
			msg.setMsgContent(contString);
			
			msg.setMsgTime(Long.toString(System.currentTimeMillis()));
			msg.setMsgType("1");			
			if(deviceType == 6){
				msg.setMsgDeviceid(mStrDevId);
//				sendMsgToDevice(mStrDevId, contString);
				sendMMSToDevice(deviceTelNum, contString);
			}else{
				msg.setMsgDeviceid(familyID);
				addFamilyMsg(familyID, contString);
			}
			mMsgDB.saveMsg(msg);
			mMsgAdapter.upDateMsg(msg);
			mMsgListView.setSelection(mMsgAdapter.getCount() - 1);
			mEditTextContent.setText("");
		}
	}
	
private void addFamilyMsg(String familyid, String content){
    	
    	HttpJson params = new HttpJson();
        params.put("familyId", familyid);
        params.put("title","");
        params.put("content",content);
        MyHttpUtil.post("/family/addFamilyLeavMsgTxt.action", params, mHandler);
    	
//          new AddFamilyMsgTask().execute();
    	
    }

	private String getDate() {
		Calendar c = Calendar.getInstance();

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);

		return sbBuffer.toString();
	}

	//��������¼�ư�ťʱ
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!Environment.getExternalStorageDirectory().exists()) {
			Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
			return false;
		}

		if (btn_vocie) {
			System.out.println("1");
			int[] location = new int[2];
			mBtnRcd.getLocationInWindow(location); // ��ȡ�ڵ�ǰ�����ڵľ�������
			int btn_rc_Y = location[1];
			int btn_rc_X = location[0];
			int[] del_location = new int[2];
			del_re.getLocationInWindow(del_location);
			int del_Y = del_location[1];
			int del_x = del_location[0];
			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
				if (!Environment.getExternalStorageDirectory().exists()) {
					Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
					return false;
				}
				System.out.println("2");
				if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {//�ж����ư��µ�λ���Ƿ�������¼�ư�ť�ķ�Χ��
					System.out.println("3");
					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
					rcChat_popup.setVisibility(View.VISIBLE);
					voice_rcd_hint_loading.setVisibility(View.VISIBLE);
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.GONE);
					mHandler.postDelayed(new Runnable() {
						public void run() {
							if (!isShosrt) {
								voice_rcd_hint_loading.setVisibility(View.GONE);
								voice_rcd_hint_rcding
										.setVisibility(View.VISIBLE);
							}
						}
					}, 300);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					startVoiceT = System.currentTimeMillis();
					voiceName = startVoiceT + ".amr";
					start(voiceName);
					flag = 2;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {//�ɿ�����ʱִ��¼�����
				System.out.println("4");
				mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
				if (event.getY() >= del_Y
						&& event.getY() <= del_Y + del_re.getHeight()
						&& event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					rcChat_popup.setVisibility(View.GONE);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					stop();
					flag = 1;
					File file = new File(Constants.WATCH_REACORD_AUDIO_PATH
									+ voiceName);
					if (file.exists()) {
						file.delete();
					}
				} else {

					voice_rcd_hint_rcding.setVisibility(View.GONE);
					stop();
					endVoiceT = System.currentTimeMillis();
					flag = 1;
					int time = (int) ((endVoiceT - startVoiceT) / 1000);
					if (time < 1) {
						isShosrt = true;
						voice_rcd_hint_loading.setVisibility(View.GONE);
						voice_rcd_hint_rcding.setVisibility(View.GONE);
						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								voice_rcd_hint_tooshort
										.setVisibility(View.GONE);
								rcChat_popup.setVisibility(View.GONE);
								isShosrt = false;
							}
						}, 500);
						return false;
					}
					ChatMessage msg = new ChatMessage();
					msg.setIsNew("0");//已读
					msg.setMsgFlag("0"); // 发送消息
					msg.setMsgContent(voiceName);
					if (deviceType == 6){
						msg.setMsgDeviceid(mStrDevId);
					}else{
						msg.setMsgDeviceid(familyID);
					}
					msg.setMsgTime(Long.toString(System.currentTimeMillis()));
					msg.setMsgType("2");
					mMsgDB.saveMsg(msg);	
					//将文件发送给手表
					linkInstance.uploadAudioFile(mStrDevId, Long.toString(startVoiceT));
//					mLinkTopHandler.sendEmptyMessageDelayed(LinkTopSDKUtil.LINK_SDK_UPLOAD_AUDIO,1000);
					
					mMsgAdapter.upDateMsg(msg);
					mMsgListView.setSelection(mMsgAdapter.getCount() - 1);
					rcChat_popup.setVisibility(View.GONE);

				}
			}
			if (event.getY() < btn_rc_Y) {//���ư��µ�λ�ò�������¼�ư�ť�ķ�Χ��
				System.out.println("5");
				Animation mLitteAnimation = AnimationUtils.loadAnimation(this,
						R.anim.cancel_rc);
				Animation mBigAnimation = AnimationUtils.loadAnimation(this,
						R.anim.cancel_rc2);
				img1.setVisibility(View.GONE);
				del_re.setVisibility(View.VISIBLE);
				del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
				if (event.getY() >= del_Y
						&& event.getY() <= del_Y + del_re.getHeight()
						&& event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
					sc_img1.startAnimation(mLitteAnimation);
					sc_img1.startAnimation(mBigAnimation);
				}
			} else {

				img1.setVisibility(View.VISIBLE);
				del_re.setVisibility(View.GONE);
				del_re.setBackgroundResource(0);
			}
		}
		return super.onTouchEvent(event);
	}

	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	private void start(String name) {
		mSensor.start(name);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	private void updateDisplay(double signalEMA) {
		
		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);
			
			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		MsgPagerNum++;
		// mMsgListData.removeAll(mMsgListData);
		mMsgListData.clear();
		mMsgListData = initMsgData();
		int position = mMsgAdapter.getCount();
		mMsgAdapter.setMessageList(mMsgListData);
		mMsgListView.stopRefresh();
		mMsgListView.setSelection(mMsgAdapter.getCount() - position - 1);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.listview:
			imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			break;
			
		case R.id.et_sendmessage:
			imm.showSoftInput(mEditTextContent, 0);
			break;

		default:
			break;
		}
		return false;
	}
	
	public void playRecordMusic(final ChatMessage msg){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] audioData = linkInstance.getFileNameOfAudio(msg.getMsgDeviceid(), msg.getMsgContent());
				if(audioData != null){
					writeAudioToLocalStorage(msg.getMsgContent()  + ".amr", audioData);
					playMusic(getAudioRecordPath() + msg.getMsgContent()  + ".amr");
					mMsgDB.updateNewFlag(msg);
					msg.setIsNew("0");
					msg.setMsgContent(msg.getMsgContent() + ".amr");
					mLinkTopHandler.sendEmptyMessage(MSG_LIST_REFRESH);
				}
			}
		}).start();        
	}
	
	
	public String getAudioRecordPath(){ 

		return Constants.WATCH_REACORD_AUDIO_PATH;
        
    }
    
    public void writeAudioToLocalStorage(String fileName, byte[] data)
    {
        FileOutputStream outStream = null;
        try {
            File createTempFile = new File(getAudioRecordPath() + fileName);
            if (!createTempFile.getParentFile().exists())
                createTempFile.getParentFile().mkdirs();
            if (!createTempFile.exists())
                createTempFile.createNewFile();

            outStream = new FileOutputStream(createTempFile);
            int len1 = data.length;
            outStream.write(data, 0, len1);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    } 
    
	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (!startAction.equals("")){
			Intent intent = new Intent();
			intent.setClass(this, StartActivity.class);
        	intent.setAction(Intent.ACTION_MAIN);
        	intent.addCategory(Intent.CATEGORY_LAUNCHER);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        	this.startActivity(intent);
		}
		mLinkTopHandler.removeCallbacksAndMessages(null);
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
		super.onDestroy();
		
	}

}
