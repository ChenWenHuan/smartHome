package com.smarthome.client2.activity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.receiver.JiTuiReceiver;
import com.smarthome.client2.receiver.JiTuiReceiver.EventHandler;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.zxing.CaptureActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNewNextSteps extends Activity implements EventHandler{
	
	private static final int CHANGE_BUTTON_SEND_MESSAGE = 100;
	
	public static final int COUNT_GET_INDENTIFY_CODE = 120;
	
	private FrameLayout fl_head;

    private CustomActionBar actionBar;
    
    private ProgressDialog mProgressBar;
    
    private RelativeLayout rv_imei;
    
    private RelativeLayout rv_val_code;
    
    private RelativeLayout rv_qr_code;
    
    private RelativeLayout rv_watch_bind_id;
    
    private TextView tv_watch_bind_id;
    
    private TextView tv_qr_code;
    private TextView tv_imei_qr_code;
    
    private TextView tv_get_val_code;
    private EditText et_val_code;
    private EditText et_imei_code;
    private EditText et_watch_id;
    private EditText et_watch_akey;
    private Button bt_submit;
    private boolean isGetingValCode = false;//是否正在获取验证码
    
    private String strTelNum = "";
    private String strShowName = "";
    private String strRelativeCode = "";
    private String strDeviceTypeCode = "";
    private String strFamilyId = "";
    private String strValCode = "";
    private String strImei = "";
    private boolean isLinkAccount = false;
    
    
    private int count_get_identify_code = 120;
    private boolean isInterrupt = false;
    
    private String mszQrcode = "";
    
    private String userTelNum = "";
    
    private LinkTopSDKUtil instance = null;
    
    private String mstrInputWatchID = "";
    private String mstrInputWatchAkey = "";
    
    private String watchDeviceID = "";
    
    private String mstrFamilyNumCommand = "";    
    
    private int mTryBindCount = 2;
    
    private Preferences tmpPreferences;
    private boolean isWatchBinded = false;
    
   
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.e_create_person_next);
        tmpPreferences = Preferences.getInstance(this.getApplicationContext());
        userTelNum = tmpPreferences.getUserTelNum();
        initView();
        Intent intent = this.getIntent();		
		strTelNum = intent.getStringExtra("telnum");
		strShowName = intent.getStringExtra("showname");
		strRelativeCode = intent.getStringExtra("relativecode");
		strDeviceTypeCode = intent.getStringExtra("devicetypecode");
		strFamilyId = intent.getStringExtra("groupid");			
		
		// 学生机
		if (strDeviceTypeCode.equals("1") || strDeviceTypeCode.equals("7")){
			rv_val_code.setVisibility(View.GONE);
			rv_imei.setVisibility(View.VISIBLE);
		}else if (strDeviceTypeCode.equals("2")){
		// 老人机，需要输入验证码，不输入IMEI号
			rv_imei.setVisibility(View.VISIBLE);
		}else if (strDeviceTypeCode.equals("6")){
			//儿童手表
			instance = LinkTopSDKUtil.getInstance();
			instance.initSDK(CreateNewNextSteps.this, handlerWatchData);
			getFamilyNum();
			rv_val_code.setVisibility(View.GONE);
			if (SmartHomeApplication.getInstance().isLinkTopAccout){
				instance.setupAccount(userTelNum, "888888");				
				instance.loginToken();
				isLinkAccount = true;
			}
			rv_imei.setVisibility(View.GONE);
			rv_qr_code.setVisibility(View.VISIBLE);
			JiTuiReceiver.ehList.add(this);
		}

    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (strDeviceTypeCode.equals("6")){
			JiTuiReceiver.ehList.remove(this);
		}
		handlerWatchData.removeCallbacksAndMessages(null);
		handlerAddNewMem.removeCallbacksAndMessages(null);
		handler_val_code.removeCallbacksAndMessages(null);
	}

	private void initView(){
    	
    	addTopBarToHead();
    	rv_watch_bind_id = (RelativeLayout)this.findViewById(R.id.rl_watch_bind_id);
    	rv_val_code = (RelativeLayout)this.findViewById(R.id.rl_code_register);
    	rv_qr_code = (RelativeLayout)this.findViewById(R.id.rl_qr_code);
    	tv_qr_code = (TextView)this.findViewById(R.id.tv_qr_code);
    	et_watch_id = (EditText)this.findViewById(R.id.et_watch_id);
    	et_watch_akey = (EditText)this.findViewById(R.id.et_watch_akey);
    	tv_qr_code.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CreateNewNextSteps.this, CaptureActivity.class);
				CreateNewNextSteps.this.startActivityForResult(intent, 0);
			}
		});
    	tv_imei_qr_code = (TextView)this.findViewById(R.id.tv_imei_qr_code);
    	tv_imei_qr_code.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CreateNewNextSteps.this, CaptureActivity.class);
				CreateNewNextSteps.this.startActivityForResult(intent, 1);
				
			}
		});
    	tv_get_val_code = (TextView)this.findViewById(R.id.tv_get_val_code);
    	tv_get_val_code.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isLinkAccount  && strDeviceTypeCode.equals("6")){
					// 注册凌拓账号
					registerWatchAccount();
					
				}else{
					getValCode();
				}
			}
		});
    	
    	tv_watch_bind_id = (TextView)this.findViewById(R.id.tv_watch_id);
    	tv_watch_bind_id.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rv_watch_bind_id.setVisibility(View.VISIBLE);
				et_watch_id.setText(tmpPreferences.getWatchBindID());
				et_watch_akey.setText(tmpPreferences.getWatchBindAkey());
			}
		});
    	
    	rv_imei = (RelativeLayout)this.findViewById(R.id.rv_device_imei);
    	
    	et_val_code = (EditText)this.findViewById(R.id.et_val_code);
    	
    	et_imei_code = (EditText)this.findViewById(R.id.et_imei_num);
    	
    	bt_submit = (Button)this.findViewById(R.id.btn_commit);
    	
    	bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(strDeviceTypeCode.equals("6")){
					doCreateBindWatchMem();
				}else{
					doCreatNewMem();
				}
			}
		});
    	
        mProgressBar = new ProgressDialog(CreateNewNextSteps.this);
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                    KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                	tv_get_val_code.setEnabled(true);
                	tv_get_val_code.setText(R.string.validecode_get);
                    count_get_identify_code = COUNT_GET_INDENTIFY_CODE;
                    isInterrupt = true;
                    handler_val_code.removeMessages(CHANGE_BUTTON_SEND_MESSAGE);
                    isGetingValCode = false;

                }
                return false;
            }
        });
    }
    
    //添加actionbar
    private void addTopBarToHead()
    {
    	fl_head = (FrameLayout) findViewById(R.id.fl_head_home);
        if (actionBar != null)
        {
        	fl_head.removeView(actionBar);
        }
        actionBar = TopBarUtils.createCustomActionBar(SmartHomeApplication.getInstance(),
                R.drawable.btn_back_selector,
                new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                },
                "信息输入",
                "",
                null);
        fl_head.addView(actionBar);
    }
    
    protected void registerWatchAccount(){
    	
    	 if (isGetingValCode)
         {
             return;
         }
         isGetingValCode = true;
         
         handlerWatchData.sendEmptyMessage(Constants.GET_LINK_ACCOUNT);

         instance.registerAccount(userTelNum, "888888");

         tv_get_val_code.setEnabled(false);
    	
    }
    
    protected void getValCode()
    {
        if (isGetingValCode)
        {
            return;
        }
        isGetingValCode = true;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //设置登录请求参数
                JSONObject obj = new JSONObject();
                try
                {
                	obj.put("phone", strTelNum);
                    obj.put("reg", true);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

//                handler_val_code.sendEmptyMessage(Constants.GET_DATA_START);
                HttpUtil.postRequest(obj,
                        Constants.VALIDCODE,
                        handler_val_code,
                        Constants.GET_DATA_SUCCESS,
                        Constants.GET_DATA_FAIL);
            }
        }).start();

        tv_get_val_code.setEnabled(false);
    }
    
    protected void doCreatNewMem(){
    	
    	String strInputValCode = et_val_code.getEditableText().toString().trim();
    	if (strDeviceTypeCode.equals("4")){
	    	if (strInputValCode.equals("")){
	    		Toast.makeText(CreateNewNextSteps.this,
	                    "请输入验证码！",
	                    Toast.LENGTH_SHORT).show();
	    		return;
	    	}
	    	
	    	if (!strInputValCode.equals(strValCode)){
	    		Toast.makeText(CreateNewNextSteps.this,
	                    "验证码不正确，请重新输入！",
	                    Toast.LENGTH_SHORT).show();
	    		return;
	    	}
    	}
    	
    	if (!strDeviceTypeCode.equals("4") && !strDeviceTypeCode.equals("6")){
    		strImei = et_imei_code.getEditableText().toString().trim();
    		if(strImei.equals("")){
    			Toast.makeText(CreateNewNextSteps.this,
                        "请输入正确的IMEI号！",
                        Toast.LENGTH_SHORT).show();
        		return;
    		}
    	}
    	
    	handlerAddNewMem.sendEmptyMessage(Constants.GET_DATA_START);
    	
    	new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //设置登录请求参数
                JSONObject obj = new JSONObject();
                try
                {
                    obj.put("telNum", strTelNum);
                    
                    obj.put("acctType", "02");
                    obj.put("groupId", strFamilyId);
                    obj.put("appellationCode", strRelativeCode);
                    obj.put("deviceType", strDeviceTypeCode);
                    obj.put("deviceCode", strImei);
                    obj.put("maintotarAlias", strShowName);
                    obj.put("acctCode", "");
                    obj.put("email", "");
                    obj.put("devTelNum", strTelNum);
                  
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
               
                HttpUtil.postRequest(obj,
                        Constants.ADD_NEM_FAMILY_MEM_V11,
                        handlerAddNewMem,
                        Constants.GET_DATA_SUCCESS,
                        Constants.GET_DATA_FAIL);
            }
        }).start();
    	
    }
    
    
    
    private void doCreateBindWatchMem(){

    	mstrInputWatchAkey = et_watch_akey.getEditableText().toString().trim();
    	mstrInputWatchID = et_watch_id.getEditableText().toString().trim();
    	if(mszQrcode.equals("") && mstrInputWatchAkey.equals("") && mstrInputWatchID.equals("")){
    		Toast.makeText(CreateNewNextSteps.this,
                    "请扫描手表二维码或输入id,akey，进行绑定！",
                    Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	
    	if (isLinkAccount){
    		//进行绑定
    		if (!mszQrcode.equals("")){
    			instance.bindDevice(mszQrcode, strTelNum);
    		}else{
    			if (!mstrInputWatchAkey.equals("") && !mstrInputWatchID.equals("")){
    				instance.bindDeviceByID(mstrInputWatchID, strTelNum, mstrInputWatchAkey);
    				tmpPreferences.setWatchBindID(mstrInputWatchID);
    				tmpPreferences.setWatchBindAkey(mstrInputWatchAkey);
    			}else{
    				Toast.makeText(CreateNewNextSteps.this,
    	                    "手表id和akey不能为空！",
    	                    Toast.LENGTH_SHORT).show();
    				return;
    			}
    		}
    	}else{
    		//激活账号
    		instance.registerAccount(userTelNum, "888888");
    	}    	
    	handlerWatchData.sendEmptyMessage(Constants.GET_DATA_START);
    	
    }
    
    private Handler handler_val_code = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                	mProgressBar.setMessage(getResources().getString(R.string.validecode_getting));
                    mProgressBar.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
//                	mProgressBar.dismiss();
                	isGetingValCode = false;
                    JSONObject JsonObj;
                    try
                    {
                        JsonObj = new JSONObject(msg.obj.toString());
                        strValCode = JsonObj.getString("data");
                        Log.e("--wzl---get val code-", "---jsondata=" + strValCode);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    
                    Message msg1 = handler_val_code.obtainMessage();
                    msg1.what = CHANGE_BUTTON_SEND_MESSAGE;
                    msg1.arg1 = count_get_identify_code;
                    handler_val_code.sendMessage(msg1);
                    break;
                case 201:
                case Constants.GET_DATA_FAIL:
                	isGetingValCode = false;
                	isInterrupt = true;
                	tv_get_val_code.setText("获取验证码");
                	tv_get_val_code.setEnabled(true);
//                	 if (mProgressBar != null && mProgressBar.isShowing())
//                     {
//                         mProgressBar.dismiss();
//                     }
                     Toast.makeText(CreateNewNextSteps.this,
                             msg.obj.toString(),
                             Toast.LENGTH_SHORT).show();
                    break;
                case CHANGE_BUTTON_SEND_MESSAGE:
                    if (!isInterrupt)
                    {
                    	tv_get_val_code.setText("" + msg.arg1);

//                        mProgressBar.dismiss();

                        count_get_identify_code--;
                        if (count_get_identify_code > 0)
                        {
                            Message message = this.obtainMessage();
                            message.what = CHANGE_BUTTON_SEND_MESSAGE;
                            message.arg1 = count_get_identify_code;
                            this.sendMessageDelayed(message, 1000);
                        }
                        else
                        {
                        	tv_get_val_code.setEnabled(true);
                        	tv_get_val_code.setText(R.string.validecode_get);
                        }
                    }
                    else
                    {
                        this.removeMessages(CHANGE_BUTTON_SEND_MESSAGE);
                        isInterrupt = false;
                    }
                    break;
              
                default:
                    break;
            }
        }
    };
        
    private Handler handlerAddNewMem = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case Constants.GET_DATA_START:
                	mProgressBar.setMessage("正在创建成员并绑定设备，请耐心等待...");
                    mProgressBar.show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                	mProgressBar.dismiss();
                	Toast.makeText(CreateNewNextSteps.this,
                             "用户创建成功,初始密码为6个8！",
                             Toast.LENGTH_SHORT).show();
                	 SmartHomeApplication.getInstance().getMainActivity().setRefreshHomeFragment(true);
                	 Intent intent = CreateNewNextSteps.this.getIntent();
                     CreateNewNextSteps.this.setResult(RESULT_OK, intent);
                	 CreateNewNextSteps.this.finish();
                    break;
                case 201:
                case Constants.GET_DATA_FAIL:
                	if (mProgressBar != null && mProgressBar.isShowing()){
                        mProgressBar.dismiss();
                    }
                	Toast.makeText(CreateNewNextSteps.this,
                            "创建成员失败，请稍后再试！",
                            Toast.LENGTH_SHORT).show();
                	 CreateNewNextSteps.this.finish();
                    break;
                default:
                    break;
            }
        }
    };
        
        private Handler handlerWatchData = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                switch (msg.what)
                {
                    case Constants.GET_DATA_START:
                    	mProgressBar.setMessage("正在创建成员并绑定手表...");
                        mProgressBar.show();
                        break;
                    case Constants.GET_DATA_SUCCESS:
                    	mProgressBar.dismiss();
                    	isWatchBinded = true;
	                   	 Toast.makeText(CreateNewNextSteps.this,
	                                "用户创建成功,初始密码为6个8！",
	                                Toast.LENGTH_SHORT).show();
	                   	 SmartHomeApplication.getInstance().getMainActivity().setRefreshHomeFragment(true);
	                   	 Intent intent = CreateNewNextSteps.this.getIntent();
	                     CreateNewNextSteps.this.setResult(RESULT_OK, intent);
	                   	 CreateNewNextSteps.this.finish();
                  
                        break;
                        
                    case Constants.GET_LINK_ACCOUNT:
                    	mProgressBar.setMessage(getResources().getString(R.string.validecode_getting));
                        mProgressBar.show();
                        break;
                    case Constants.GET_DATA_FAIL:
                    	if (mProgressBar != null && mProgressBar.isShowing()){
                            mProgressBar.dismiss();
                        }
                    	mTryBindCount = 2;
                    	isWatchBinded = false;
                    	Toast.makeText(CreateNewNextSteps.this,
                                "创建失败" + msg.obj + "请重试！",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case LinkTopSDKUtil.LINK_SDK_LOGIN_TOKEN:
                    	if (msg.arg1 == 200){
                    		
                    	}else{
                    		
                    	}
                    	break;
                    	
                    case LinkTopSDKUtil.LINK_SDK_REGISTER_ACCOUNT_ACTION:
                    	if (msg.arg1 == 0){
                    		SmartHomeApplication.getInstance().isLinkTopAccout = true;
                    		isLinkAccount = true;
                    		if(!SmartHomeApplication.getInstance().jiTuiRegisterID.equals("")){
                    			instance.registerPushParam(SmartHomeApplication.getInstance().jiTuiRegisterID);
                    		}
                    		if (!mszQrcode.equals("")){
                    			instance.bindDevice(mszQrcode, strTelNum);
                    		}else{
                    			if (!mstrInputWatchAkey.equals("") && !mstrInputWatchID.equals("")){
                    				instance.bindDeviceByID(mstrInputWatchID, strTelNum, mstrInputWatchAkey);
                    			}else{
                    				 if (mProgressBar != null && mProgressBar.isShowing()){
                                         mProgressBar.dismiss();
                                     }
                    				Toast.makeText(CreateNewNextSteps.this,
                    	                    "手表id和akey不能为空！",
                    	                    Toast.LENGTH_SHORT).show();
                    				return;
                    			}
                    		}
                    	}else{
                    		Toast.makeText(CreateNewNextSteps.this,
            	                    "创建手表账号失败，请重试!",
            	                    Toast.LENGTH_SHORT).show();
                    		if (mProgressBar != null && mProgressBar.isShowing()){
                                mProgressBar.dismiss();
                            }
                    	}
                    	break;
                    	
                    case CHANGE_BUTTON_SEND_MESSAGE:
                        if (!isInterrupt)
                        {
                        	tv_get_val_code.setText("" + msg.arg1);

                            mProgressBar.dismiss();

                            count_get_identify_code--;
                            if (count_get_identify_code > 0)
                            {
                                Message message = this.obtainMessage();
                                message.what = CHANGE_BUTTON_SEND_MESSAGE;
                                message.arg1 = count_get_identify_code;
                                this.sendMessageDelayed(message, 1000);
                            }
                            else
                            {
                            	tv_get_val_code.setEnabled(true);
                            	tv_get_val_code.setText(R.string.validecode_get);
                            }
                        }
                        else
                        {
                            this.removeMessages(CHANGE_BUTTON_SEND_MESSAGE);
                            isInterrupt = false;
                        }
                        break;
                    case LinkTopSDKUtil.LINK_SDK_ACTIVE_ACCOUNT_ACTION:
                    	if (msg.arg1 == 0){
                    		//创建账号成功
                    		if (!mszQrcode.equals("")){
                    			instance.bindDevice(mszQrcode, strTelNum);
                    		}else{
                    			instance.bindDeviceByID(mstrInputWatchID, strTelNum, mstrInputWatchAkey);
                    		}
                    	}else{
                    		//创建账号失败
                    		 if (mProgressBar != null && mProgressBar.isShowing()){
                                 mProgressBar.dismiss();
                             }
                    		 if (msg.arg1 == 2){
                    			 Toast.makeText(CreateNewNextSteps.this,
                                         "验证码输入错误！",
                                         Toast.LENGTH_SHORT).show();
                    		 }else{
                    			 Toast.makeText(CreateNewNextSteps.this,
                                    "账号创建失败，请重新点击提交！",
                                    Toast.LENGTH_SHORT).show();
                    		 }
                    	}
                    	break;
                    case Constants.WATCH_DEVICE_BINDED:
                    	 Toast.makeText(CreateNewNextSteps.this,
                                 "此设备已被其他人绑定！",
                                 Toast.LENGTH_SHORT).show();
                    	break;
                    case Constants.WATCH_DEVICES_LIST:
                    	mTryBindCount--;
                    	instance.getDevices();
                    	break;
                    case LinkTopSDKUtil.LINK_SDK_LIST_DEVICES:
                    	if(msg.arg1 == 200){
                    		if(msg.obj.toString().contains(watchDeviceID)){
                    			
                    			if (mstrFamilyNumCommand.equals("")) {
                    				doAddWatchMem();
                                } else {
                                    instance.editWiteNameList(watchDeviceID, mstrFamilyNumCommand);
                                }
                    			return;
                    		}              		
                    	}
                    	
                		if (mTryBindCount > 0){
                			handlerWatchData.sendEmptyMessageDelayed(Constants.WATCH_DEVICES_LIST, 40000);
                		}else {
                          mTryBindCount = 2;
                          Message msgWaiting = new Message();
                          msgWaiting.what = Constants.GET_DATA_FAIL;
                          msgWaiting.obj = "绑定超时,请确认输入的手表号是否正确，2分钟后";
                          handlerWatchData.sendMessage(msgWaiting);
                      }
                	  break;
                    	
                    case Constants.WATCH_DEVICE_BINDED_AGAIN:
                    	mTryBindCount--;
                    	if (!mszQrcode.equals("")){
                			instance.bindDevice(mszQrcode, strTelNum);
                		}else{
                			instance.bindDeviceByID(mstrInputWatchID, strTelNum, mstrInputWatchAkey);
                		}
                    	break;
                    	
                    case LinkTopSDKUtil.LINK_SDK_BIND_DEVICE_ACTION:
                    	if (msg.arg1 != 200){
                    		handlerWatchData.sendEmptyMessage(Constants.GET_DATA_FAIL);
                    	}else{
                    		if (msg.obj != null){
                    			try {
            						JSONObject jsonObject = new JSONObject(
            								(String)msg.obj);
            						String state = jsonObject.optString("state");
            						watchDeviceID = jsonObject.optString("id");

            						if (state.equals("") || state.equals("0")){
            							if(mstrFamilyNumCommand.equals("")){
            								doAddWatchMem();
            							}else{
            								instance.editWiteNameList(watchDeviceID, mstrFamilyNumCommand);
            							}
            						}else if (state.equals("1")){
//            							if (mTryBindCount > 0){
//            								handlerWatchData.sendEmptyMessageDelayed(Constants.WATCH_DEVICE_BINDED_AGAIN, 60000);
//            							}else{            								
//            								Message failMsg = new Message();
//            								failMsg.what = Constants.GET_DATA_FAIL;
//            								failMsg.obj = "服务器忙， 绑定超时,2分钟后";
//            								handlerWatchData.sendMessage(failMsg);
//            							}
            							handlerWatchData.sendEmptyMessageDelayed(Constants.WATCH_DEVICES_LIST, 40000);
            						}
            						else if (state.equals("3")){
            							handlerWatchData.sendEmptyMessage(Constants.WATCH_DEVICE_BINDED);
            						}else if (state.equals("4")) {
                                    	mTryBindCount = 2;
                                    	Message failMsg2 = new Message();
        								failMsg2.what = Constants.GET_DATA_FAIL;
        								failMsg2.obj = "二维码格式错误";
        								handlerWatchData.sendMessage(failMsg2);
                                    }else {
                                    	mTryBindCount = 2;
                                    	Message failMsg2 = new Message();
        								failMsg2.what = Constants.GET_DATA_FAIL;
        								failMsg2.obj = "手表未登记，请确认输入信息是否正确，";
        								handlerWatchData.sendMessage(failMsg2);
//                                      handlerWatchData.sendEmptyMessage(Constants.GET_DATA_FAIL);
                                    }
            					
            					} catch (JSONException e) {
            						e.printStackTrace();
            					}
                    		}
                    	}
                    	break;
                    case LinkTopSDKUtil.LINK_SDK_EDIT_WITE_LIST:
                    	if (msg.arg1 == 200){
                    		doAddWatchMem();
                    	}else{
                         	Message failMsg3 = new Message();
    						failMsg3.what = Constants.GET_DATA_FAIL;
    						failMsg3.obj = "初始化亲情号码无效";
    						handlerWatchData.sendMessage(failMsg3);
//                    		handlerWatchData.sendEmptyMessage(Constants.GET_DATA_FAIL);
                    	}
                    	break;
                    	
                    default:
                        break;
                }
            }
        };

		@Override
		protected void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			
			if(requestCode == 0 && resultCode == RESULT_OK){
				mszQrcode = data.getStringExtra("qrcode");
				tv_qr_code.setText(mszQrcode);
			}else if (requestCode == 1 && resultCode == RESULT_OK){
				String imei = data.getStringExtra("qrcode");
				et_imei_code.setText(imei);
			}
		}

		private void doAddWatchMem(){
			
			if(isWatchBinded){
				return;
			}
			new Thread(new Runnable()
	        {
	            @Override
	            public void run()
	            {
	                //设置登录请求参数
	                JSONObject obj = new JSONObject();
	                try
	                {
	                    obj.put("telNum", strTelNum);
	                    
	                    obj.put("acctType", "02");
	                    obj.put("groupId", strFamilyId);
	                    obj.put("appellationCode", strRelativeCode);
	                    obj.put("deviceType", strDeviceTypeCode);
	                    obj.put("deviceCode", watchDeviceID + ":" + userTelNum);
	                    obj.put("maintotarAlias", strShowName);
	                    obj.put("acctCode", "");
	                    obj.put("email", "");
	                    obj.put("devTelNum", strTelNum);
	                }
	                catch (JSONException e)
	                {
	                    e.printStackTrace();
	                }	               
	                HttpUtil.postRequest(obj,
	                        Constants.ADD_NEM_FAMILY_MEM_V11,
	                        handlerWatchData,
	                        Constants.GET_DATA_SUCCESS,
	                        Constants.GET_DATA_FAIL);
	            }
	        }).start();
		}
		
		
		private void getFamilyNum(){
			
			new Thread(new Runnable()
	        {
	            @Override
	            public void run()
	            {
	                //设置登录请求参数
	                JSONObject obj = new JSONObject();
	                try
	                {
	                    obj.put("devTelNum", strTelNum);           
	                }
	                catch (JSONException e)
	                {
	                    e.printStackTrace();
	                }	               
	                HttpUtil.postRequest(obj,
	                        Constants.GET_FAMILY_NUM,
	                        handleGetFamilyNum,
	                        Constants.GET_DATA_SUCCESS,
	                        Constants.GET_DATA_FAIL);
	            }
	        }).start();
		}
		
		private Handler handleGetFamilyNum = new Handler(){
			
			public void handleMessage(android.os.Message msg){
				switch(msg.what){
				case Constants.GET_DATA_SUCCESS:
					mstrFamilyNumCommand = parserFamilyNum(msg.obj.toString());
					Log.e("-handleGetFamilyNum-", "--familynum=" + mstrFamilyNumCommand);
					break;
				case Constants.GET_DATA_FAIL:
					break;
				default:
					break;
				}
			}
			
		};
		
		private String parserFamilyNum(String jsondata){
			
			JSONObject JsonObj = null;
			StringBuilder strCommand = new StringBuilder();
			
			try {
				JsonObj = new JSONObject(jsondata);
				JSONArray jsonarray = JsonObj.getJSONArray("data");
				for (int i=0; i< jsonarray.length(); i++){
					String name = jsonarray.getJSONObject(i).getString("famName");
					String num = jsonarray.getJSONObject(i).getString("familyTelNum");
					if(num.equals(userTelNum)){
						continue;
					}
					if(name == null || name.equals("") || name.equals("null")){
						name = "关注者" + Integer.toString(i+1);
					}
					if (i > 0){
						strCommand.append("|");
					}						
					strCommand.append("2," + num + "," + name);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
			
			return strCommand.toString();
			
		}

		@Override
		public void onMessage(String message) {
			// TODO Auto-generated method stub
			if(isWatchBinded){
				return;
			}
			if (message.equals(watchDeviceID)){
				handlerWatchData.removeCallbacksAndMessages(null);
				if(mstrFamilyNumCommand.equals("")){
					doAddWatchMem();
				}else{
					instance.editWiteNameList(watchDeviceID, mstrFamilyNumCommand);
				}
			}
			
		}
		
 }
