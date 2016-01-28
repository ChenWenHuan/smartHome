package com.smarthome.client2.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthome.client2.R;
import com.smarthome.client2.SmartHomeApplication;
import com.smarthome.client2.common.Constants;
import com.smarthome.client2.config.Preferences;
import com.smarthome.client2.receiver.JiTuiReceiver;
import com.smarthome.client2.receiver.JiTuiReceiver.EventHandler;
import com.smarthome.client2.util.HttpUtil;
import com.smarthome.client2.util.LinkTopSDKUtil;
import com.smarthome.client2.util.NetStatusListener;
import com.smarthome.client2.util.RequestResult;
import com.smarthome.client2.util.TopBarUtils;
import com.smarthome.client2.view.CustomActionBar;
import com.smarthome.client2.zxing.CaptureActivity;
import com.baidu.mapapi.model.LatLng;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


// add device
public class DeviceManagerment_sm extends BaseActivity implements OnClickListener, EventHandler {

    /**
     * 日志开关
     */
    private static final boolean DEBUG = true;

    private static final int SAVE_DATA_FAIL = 9;
    private static final int SAVE_DATA_SUCCESS = 10;
    private static final int SAVE_DATA_START = 11;
    private static final int SAVE_DEVICETYPE_NULL = 888;
    private static final int SAVE_DEVICECODE_EMPYT = 2000;
    private static final int SAVE_PHONENUM_NULL = 2002;

    /**
     * for dialog id
     */
    private static final int DIALOG_USERINFO_PROGRESS = 0;

    private static final String DIALOG_MSG = "dialog_msg";


    private FrameLayout mTitleBar;
    private CustomActionBar mActionBar;

    //private PopupWindow mPopupWindowDialog;

    private LinearLayout mDeviceTypeLayout;
    private LinearLayout mDeviceCodeLayout;
    private LinearLayout mDevicePhoneNumLayout;

    private Button edit_device_manager_btn_save;
    private TextView mTVDeciceType;
    private TextView mTVDeciceCode;
    private TextView mTVDecicePhoneNum;
    private TextView mTVDeviceQrCode;
    private TextView tv_show_hint;


    //private String familyKeyPersonID; //户主的id
    private String mLoginUserId;   //登陆的用户id

    private String mUserId;   //userId
    private String mTitleName = "";
    private String mPhoneNum = "";
    private int mDeviceTypeSelect = 0;
    private String mStrDeviceType;
    private String mStrDeviceTypeCode = "";
    private String mStrDeviceCode;
    private String mStrPhoneNum;
    private ProgressDialog mProgressBar;
    private int mStatus;

    private String mszQrcode = "";

    private long mLDeviceId;
    private String deviceID = "";
    private String watchAkey = "";

    private LinkTopSDKUtil instance = null;
    private boolean isLinkAccount = false;
    private boolean isWatchBinded = false;
    private int mTryBindCount = 2;
    private String userTelNum = "";
    private String mstrFamilyNumCommand = "";
    //private String strDeviceType[] = new String[]{"学生机", "老人机"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.e_edit_device_manager);

        initUserInfo();
        initDataFromIntent();
        initView();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_USERINFO_PROGRESS:
                dialog = new ProgressDialog(this);
                dialog.setCanceledOnTouchOutside(false);
                break;
            default:
                dialog = null;
                break;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
        switch (id) {
            case DIALOG_USERINFO_PROGRESS:
                String msg = bundle.getString(DIALOG_MSG);
                ((ProgressDialog) dialog).setMessage(msg);
                break;
        }
        super.onPrepareDialog(id, dialog, bundle);
    }


    private void initUserInfo() {
        Preferences preferences = Preferences.getInstance(this.getApplicationContext());
        mLoginUserId = preferences.getUserID();
    }

    private void initView() {

        mDeviceTypeLayout = (LinearLayout) this.findViewById(R.id.ll_deviceType_lay);
        mDeviceCodeLayout = (LinearLayout) this.findViewById(R.id.ll_deviceCode_lay);
        mDevicePhoneNumLayout = (LinearLayout) this.findViewById(R.id.ll_DevicephoneNumber_lay);
        edit_device_manager_btn_save = (Button) this.findViewById(R.id.edit_device_manager_btn_save);
        tv_show_hint = (TextView)this.findViewById(R.id.tv_show_hint);
        mDeviceTypeLayout.setOnClickListener(this);
//		mDeviceCodeLayout.setOnClickListener(this);
        mDevicePhoneNumLayout.setOnClickListener(this);
        edit_device_manager_btn_save.setVisibility(View.VISIBLE);

        mTVDeciceType = (TextView) this.findViewById(R.id.edit_device_manager_deviceType);
        mTVDeciceCode = (TextView) this.findViewById(R.id.edit_device_manager_deviceCode);
        mTVDeciceCode.setOnClickListener(this);
        mTVDecicePhoneNum = (TextView) this.findViewById(R.id.edit_device_manager_phone_number);

        mTVDeviceQrCode = (TextView) this.findViewById(R.id.tv_qr_code);
        mTVDeviceQrCode.setOnClickListener(this);

        mProgressBar = new ProgressDialog(DeviceManagerment_sm.this);
        mProgressBar.setCanceledOnTouchOutside(false);

        addTopBarToHead();

    }

    private void addTopBarToHead() {
        mTitleBar = (FrameLayout) findViewById(R.id.e_edit_deviceManager_setting_info);
        if (mActionBar != null) {
            mTitleBar.removeView(mActionBar);
        }
        if (mTitleName.isEmpty()) {
            mTitleName = "添加设备";
        }
        mActionBar = TopBarUtils.createCustomActionBarInvisiableRightImage(SmartHomeApplication.getInstance(),
                R.drawable.btn_back_selector,
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                mTitleName,
                R.drawable.default_pictures,
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        saveDeviceInfo();
                    }
                });
        mTitleBar.addView(mActionBar);
    }

    private void initDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mUserId = intent.getStringExtra("userId");
        }
    }

    private final int MODIFY_DEVICETCODE = 1; //修改
    private final int MODIFY_DEVICETTYPE = 2;
    private final int MODIFY_PHONENUM = 5; //修手机号码
    private final int GET_QR_CODE = 6; //获取二维码


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {

                    case MODIFY_DEVICETCODE: // user nick name 用户名称修改
                        if (data != null && data.getExtras() != null) {
                            Bundle bundle = data.getExtras();
                            deviceID = bundle.getString("newDeviceCode");
                            watchAkey = bundle.getString("akey", "");
                            if (!TextUtils.isEmpty(watchAkey)) {
                                mTVDeciceCode.setText("ID=" + deviceID + ":Akey=" + watchAkey);
                            } else {
                                if (TextUtils.isEmpty(deviceID)) {
                                    mTVDeciceCode.setText("");
                                } else {
                                    mTVDeciceCode.setText(deviceID);
                                }
                            }
                        }
                        break;
                    case MODIFY_PHONENUM:
                        if (data != null && data.getExtras() != null) {
                            Bundle bundle = data.getExtras();
                            mStrPhoneNum = bundle.getString("newPhoneNum");
                            if (TextUtils.isEmpty(mStrPhoneNum)) {
                                mTVDecicePhoneNum.setText("");
                            } else {
                                mTVDecicePhoneNum.setText(mStrPhoneNum);
                                getFamilyNum();
                            }
                        }
                        break;
                    case MODIFY_DEVICETTYPE:
                        Bundle bundleData = data.getExtras();
                        mStrDeviceType = bundleData.getString("title");
                        mStrDeviceTypeCode = bundleData.getString("code");
                        if (mStrDeviceTypeCode.equals("6")) {
                            initWatchData();
                            tv_show_hint.setVisibility(View.VISIBLE);
                        }else{
                        	tv_show_hint.setVisibility(View.GONE);
                        }
                        //mTVDeciceCode.setText("点击输入（没有二维码）");
                        mTVDeciceType.setText(mStrDeviceType);
                        break;
                    case GET_QR_CODE:
                        mszQrcode = data.getStringExtra("qrcode");
                        if (TextUtils.isEmpty(mszQrcode)) {
                            mTVDeciceCode.setText("");
                        } else {
                            mTVDeciceCode.setText(mszQrcode);
                        }
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initWatchData() {
        instance = LinkTopSDKUtil.getInstance();
        Preferences tmpPreferences = Preferences.getInstance(this.getApplicationContext());
        userTelNum = tmpPreferences.getUserTelNum();

        instance.initSDK(DeviceManagerment_sm.this, handlerWatchData);
        if (SmartHomeApplication.getInstance().isLinkTopAccout) {
            instance.setupAccount(userTelNum, "888888");
            instance.loginToken();
            isLinkAccount = true;
        }
    }

    private Handler handlerWatchData = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case Constants.GET_DATA_START:
                    mProgressBar.setMessage("添加儿童手表过程中，请耐心等待...");
                    mProgressBar.show();
                    break;
                case LinkTopSDKUtil.LINK_SDK_REGISTER_ACCOUNT_ACTION:
                    if (msg.arg1 == 0) {
                        //创建账号成功
                    	SmartHomeApplication.getInstance().isLinkTopAccout = true;
                        if (!SmartHomeApplication.getInstance().jiTuiRegisterID.equals("")) {
                            instance.registerPushParam(SmartHomeApplication.getInstance().jiTuiRegisterID);
                        }
                        if (!mszQrcode.equals("")) {
                            instance.bindDevice(mszQrcode, mStrPhoneNum);
                        } else {
                            instance.bindDeviceByID(deviceID, mStrPhoneNum, watchAkey);
                        }
                    } else {
                        if (mProgressBar != null && mProgressBar.isShowing()) {
                            mProgressBar.dismiss();
                        }
                        Toast.makeText(DeviceManagerment_sm.this,
                                "创建手表账号失败，请重试!",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.WATCH_DEVICE_BINDED:
                    if (mProgressBar != null && mProgressBar.isShowing()) {
                        mProgressBar.dismiss();
                    }
                    Toast.makeText(DeviceManagerment_sm.this,
                            "此设备已被其他人绑定！",
                            Toast.LENGTH_SHORT).show();
                    break;

                case Constants.WATCH_DEVICE_BINDED_AGAIN:
                    mTryBindCount--;
                    if (!mszQrcode.equals("")) {
                        instance.bindDevice(mszQrcode, mStrPhoneNum);
                    } else {
                        instance.bindDeviceByID(deviceID, mStrPhoneNum, watchAkey);
                    }
                    break;
                case Constants.WATCH_DEVICES_LIST:
                	mTryBindCount--;
                	instance.getDevices();
                	break;
                case LinkTopSDKUtil.LINK_SDK_LIST_DEVICES:
                	if(msg.arg1 == 200){
                		if(msg.obj.toString().contains(deviceID)){
                			
                			if (mstrFamilyNumCommand.equals("")) {
                                doAddWatchDevice();
                            } else {
                                instance.editWiteNameList(deviceID, mstrFamilyNumCommand);
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
                      msgWaiting.obj = "绑定超时,请确认输入的手表卡号是否正确，2分钟后";
                      handlerWatchData.sendMessage(msgWaiting);
                  }
            	  break;

                case LinkTopSDKUtil.LINK_SDK_BIND_DEVICE_ACTION:
                    if (msg.arg1 != 200) {
                        handlerWatchData.sendEmptyMessage(Constants.GET_DATA_FAIL);
                    } else {
                        if (msg.obj != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(
                                        (String) msg.obj);
                                String state = jsonObject.optString("state");
                                deviceID = jsonObject.optString("id");

                                if (state.equals("") || state.equals("0")) {
                                    if (mstrFamilyNumCommand.equals("")) {
                                        doAddWatchDevice();
                                    } else {
                                        instance.editWiteNameList(deviceID, mstrFamilyNumCommand);
                                    }
                                } else if (state.equals("1")) {
//                                    if (mTryBindCount > 0) {
//                                        handlerWatchData.sendEmptyMessageDelayed(Constants.WATCH_DEVICE_BINDED_AGAIN, 60000);
//                                    } else {
//                                    	mTryBindCount = 2;
//                                        Message msgWaiting = new Message();
//                                        msgWaiting.what = Constants.GET_DATA_FAIL;
//                                        msgWaiting.obj = "服务器忙， 绑定超时,2分钟后";
//                                        handlerWatchData.sendMessage(msgWaiting);
//                                    }
                                	handlerWatchData.sendEmptyMessageDelayed(Constants.WATCH_DEVICES_LIST, 40000);
                                } else if (state.equals("3")) {
                                	mTryBindCount = 2;
                                    handlerWatchData.sendEmptyMessage(Constants.WATCH_DEVICE_BINDED);
                                } else if (state.equals("4")) {
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
//                                  handlerWatchData.sendEmptyMessage(Constants.GET_DATA_FAIL);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case Constants.GET_DATA_FAIL:
                    if (mProgressBar != null && mProgressBar.isShowing()) {
                        mProgressBar.dismiss();
                    }
//                    instance.unBindDevice(deviceID);
                    isWatchBinded = false;
                    Toast.makeText(DeviceManagerment_sm.this,
                            "添加失败" + msg.obj + "请重试！",
                            Toast.LENGTH_SHORT).show();
                    break;
                case Constants.GET_DATA_SUCCESS:
                    if (mProgressBar != null && mProgressBar.isShowing()) {
                        mProgressBar.dismiss();
                    }
                    isWatchBinded = true;
                    try {
                        JSONObject jobj = new JSONObject(msg.obj.toString());

                        if (jobj.has("data")) {
                            JSONObject data = jobj.getJSONObject("data");
                            if (data.has("deviceId")
                                    && !TextUtils.isEmpty(data.getString("deviceId"))
                                    ) {
                                mLDeviceId = data.getLong("deviceId");
                            }
                        } 
                    } catch (Exception e) {
                    }
                    Bundle param = new Bundle();
                    param.putString("userId", mUserId);
                    param.putString("deviceCode", deviceID + ":" + userTelNum);
                    param.putString("deviceType", mStrDeviceType);
                    param.putString("deviceTypeCode", mStrDeviceTypeCode);
                    param.putString("devicePhone", mStrPhoneNum);
                    param.putLong("deviceId", mLDeviceId);
                    DeviceManagerment_sm.this.setResult(RESULT_OK, DeviceManagerment_sm.this.getIntent().putExtras(param));
                    DeviceManagerment_sm.this.finish();

                    finish();
                    break;
                case LinkTopSDKUtil.LINK_SDK_EDIT_WITE_LIST:
                    if (msg.arg1 == 200) {
                        doAddWatchDevice();
                    } else {
                    	Message failMsg3 = new Message();
						failMsg3.what = Constants.GET_DATA_FAIL;
						failMsg3.obj = "初始化亲情号码无效";
						handlerWatchData.sendMessage(failMsg3);
//                        handlerWatchData.sendEmptyMessage(Constants.GET_DATA_FAIL);
                    }
                    break;
                default:
                    break;
            }

        }
    };

    private void doAddWatchDevice() {
    	
    	if (isWatchBinded){
    		return;
    	}

        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置登录请求参数
                JSONObject obj = new JSONObject();
                try {
                    obj.put("userId", mUserId);
                    obj.put("deviceType", mStrDeviceTypeCode);
                    obj.put("deviceCode", deviceID + ":" + userTelNum);
                    obj.put("mobileCode", mStrPhoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtil.postRequest(obj,
                        Constants.ADDDEVICES,
                        handlerWatchData,
                        Constants.GET_DATA_SUCCESS,
                        Constants.GET_DATA_FAIL);
            }
        }).start();
    }

//	private void getUserInfo() {
//		if (HttpUtil.isNetworkAvailable(this)) {
//			updateState(GETUSERINFO_START, Constants.SC_OK);
//			new GetUserInfoTask().execute();
//		} else {
//			updateState(GETUSERINFO_FAIL, Constants.NO_NETWORK);
//		}
//	}

    /**
     * [update state]
     * 更新界面状态
     *
     * @param state 状态
     * @param code  状态码
     */
    private void updateState(String errorMsg) {
        if (isFinishing()) {
            return;
        }
        removeDialog(DIALOG_USERINFO_PROGRESS);
        showToast(errorMsg);
    }

    private void updateState(int state, int code) {
        if (isFinishing()) {
            return;
        }
        mStatus = state;
        Bundle bundle = new Bundle();
        String errormsg = HttpUtil.responseHandler(this, code);
        switch (state) {

            case SAVE_DATA_START:
                bundle.putString(DIALOG_MSG, getString(R.string.info_saving));
                showDialog(DIALOG_USERINFO_PROGRESS, bundle);
                break;
            case SAVE_DATA_FAIL:
                removeDialog(DIALOG_USERINFO_PROGRESS);
                switch (code) {
                    case SAVE_DEVICETYPE_NULL:
                        showToast("设备类型为空!");
                        break;
                    case SAVE_DEVICECODE_EMPYT:
                        showToast("请输入设备唯一标示号!");
                        break;
                    case Constants.NO_NETWORK:
                        showToast(R.string.no_network);
                        break;
                    case Constants.SERVER_OFFLINE:
                        showToast(R.string.info_server_offline);
                        break;
                    case SAVE_PHONENUM_NULL:
                        showToast("电话号码不能为空!");
                        break;
                    default:
                        showToast(errormsg);
                        break;
                }
                break;
            case SAVE_DATA_SUCCESS:
                showToast(R.string.info_save_ok);
                Bundle param = new Bundle();
                param.putString("userId", mUserId);
                param.putLong("deviceId", mLDeviceId);
                param.putString("deviceCode", mStrDeviceCode);
                param.putString("deviceType", mStrDeviceType);
                param.putString("deviceTypeCode", mStrDeviceTypeCode);
                param.putString("devicePhone", mStrPhoneNum);

                DeviceManagerment_sm.this.setResult(RESULT_OK, DeviceManagerment_sm.this.getIntent().putExtras(param));
                DeviceManagerment_sm.this.finish();

                finish();
                break;
            default:
                log("------updateState----else---" + state + "  " + errormsg);
                showToast(errormsg);
                break;

        }
    }

    @Override
    protected void onDestroy() {
        //unregisterReceiver(mReceiver);
        super.onDestroy();
        JiTuiReceiver.ehList.remove(this);
        handlerWatchData.removeCallbacksAndMessages(null);
    }


    @Override
    public void onClick(View v) {

        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.edit_device_manager_deviceCode:
                intent = new Intent();
                intent.setClass(DeviceManagerment_sm.this, UiEditDeviceCode_sm.class);
                if (mStrDeviceTypeCode.equals("6")) {
                    intent.setAction("GET_WATCH_DEVICE_ID_KEY");
                } else {
                    intent.setAction("");
                }
                startActivityForResult(intent, MODIFY_DEVICETCODE);
                break;
            case R.id.ll_DevicephoneNumber_lay:
                intent = new Intent();
                intent.setClass(DeviceManagerment_sm.this, UiEditUserInfoTelphone_sm.class);
                startActivityForResult(intent, MODIFY_PHONENUM);
                break;
            case R.id.ll_deviceType_lay:

                intent = new Intent(DeviceManagerment_sm.this,
                        SelectFamilyRelative.class);
                intent.setAction("SEL_DEVICE_TYPE_FROM_DM");
                startActivityForResult(intent, MODIFY_DEVICETTYPE);
                break;
            case R.id.tv_qr_code:
                // TODO Auto-generated method stub
                intent = new Intent(DeviceManagerment_sm.this, CaptureActivity.class);
                DeviceManagerment_sm.this.startActivityForResult(intent, GET_QR_CODE);
                break;

            default:
                break;
        }
    }

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            if (imm != null) {
                if (this.getCurrentFocus() == null)
                    return;
                if (this.getCurrentFocus().getWindowToken() == null)
                    return;
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    private boolean doSaveInfo() {

        mStrDeviceCode = mTVDeciceCode.getText().toString().trim();
        if (mStrDeviceCode.contains("点击输入")) {
            updateState(SAVE_DATA_FAIL, SAVE_DEVICECODE_EMPYT);
            return false;
        }
        if (TextUtils.isEmpty(mStrDeviceCode)) {
            updateState(SAVE_DATA_FAIL, SAVE_DEVICECODE_EMPYT);
            return false;
        }

        String deviceType = mTVDeciceType.getText().toString().trim();
        if (TextUtils.isEmpty(deviceType)) {
            updateState(SAVE_DATA_FAIL, SAVE_DEVICETYPE_NULL);
            return false;
        }
//		else {
//			if (deviceType.compareToIgnoreCase("学生机") == 0) {
//				mStrDeviceType = "1";
//			}
//		}

        mStrPhoneNum = mTVDecicePhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(mStrPhoneNum)) {
            updateState(SAVE_DATA_FAIL, SAVE_PHONENUM_NULL);
            return false;
        }
        if (mTVDecicePhoneNum.getText().toString().trim().length() > 11
                || (mTVDecicePhoneNum.getText()
                .toString()
                .trim()
                .length() < 11 && mTVDecicePhoneNum.getText()
                .toString()
                .trim()
                .length() > 0)) {
            Toast.makeText(getApplicationContext(),
                    "电话号码无效",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }


    class SaveInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            JSONObject obj = new JSONObject();
            try {
                obj.put("userId", mUserId);
                obj.put("deviceType", mStrDeviceTypeCode);
                obj.put("deviceCode", mStrDeviceCode);
                obj.put("mobileCode", mStrPhoneNum);
            } catch (JSONException e) {
                log(e.toString());
            }
            final RequestResult requestResult = new RequestResult();
            HttpUtil.postRequest(obj,
                    Constants.ADDDEVICES,
                    requestResult,
                    DeviceManagerment_sm.this);
            result = requestResult.getResult();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int resultcode = Constants.UNKNOW_RESULT;
            String retmsg = "";
            if (!TextUtils.isEmpty(result)) {
                boolean isCode = false;
                try {
                    JSONObject jobj = new JSONObject(result);
                    resultcode = jobj.getInt("retcode");

                    if (jobj.has("data")) {
                        JSONObject data = jobj.getJSONObject("data");
                        if (data.has("deviceId")
                                && !TextUtils.isEmpty(data.getString("deviceId"))
                                ) {
                            mLDeviceId = data.getLong("deviceId");
                        }
                        isCode = true;
                    } else if (jobj.has("retmsg")) {
                        retmsg = jobj.getString("retmsg");
                        isCode = false;
                    }
                } catch (Exception e) {
                    isCode = false;
                }
                if (!isCode) {
                    if (retmsg.isEmpty()) {
                        updateState(SAVE_DATA_FAIL, resultcode);
                    } else {
                        updateState(retmsg);
                    }
                } else {
                    if (retmsg.isEmpty()) {
                        updateState(SAVE_DATA_SUCCESS, resultcode);
                    } else {
                        updateState(retmsg);
                    }
                }
            } else {
                updateState(SAVE_DATA_FAIL, resultcode);
            }
        }
    }

    //保存用户信息入口
    public void onSave(View view) {

        if (mStrDeviceTypeCode.equals("6")) {

            if (mszQrcode.equals("")) {
                if (deviceID.equals("") || watchAkey.equals("")) {
                    Toast.makeText(DeviceManagerment_sm.this,
                            "请扫描手表二维码或输入id,akey，进行绑定！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            mStrPhoneNum = mTVDecicePhoneNum.getText().toString().trim();
            if (mStrPhoneNum.length() == 0){
            	Toast.makeText(getApplicationContext(),
                        "手表号码不能为空，请确认！！",
                        Toast.LENGTH_SHORT)
                        .show();
            	return;
            }
            if (mTVDecicePhoneNum.getText().toString().trim().length() > 11
                    || (mTVDecicePhoneNum.getText()
                    .toString()
                    .trim()
                    .length() < 11 && mTVDecicePhoneNum.getText()
                    .toString()
                    .trim()
                    .length() > 0)) {
                Toast.makeText(getApplicationContext(),
                        "输入的设备手机号无效，请确认！",
                        Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            JiTuiReceiver.ehList.add(this);
            if (isLinkAccount) {
                //进行绑定
                bindWatchDevice();
            } else {
                //激活账号
                instance.registerAccount(userTelNum, "888888");
            }
            handlerWatchData.sendEmptyMessage(Constants.GET_DATA_START);
        } else {
            saveDeviceInfo();
        }
    }


    private void bindWatchDevice() {
        if (!mszQrcode.equals("")) {
            instance.bindDevice(mszQrcode, mStrPhoneNum);
        } else {
            if (!deviceID.equals("") && !watchAkey.equals("")) {
                instance.bindDeviceByID(deviceID, mStrPhoneNum, watchAkey);
            } else {
                Toast.makeText(DeviceManagerment_sm.this,
                        "手表id和akey不能为空！",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    /**
     * save device ifo
     * 保存设备信息
     */
    private void saveDeviceInfo() {
        if (HttpUtil.isNetworkAvailable(getApplicationContext())) {
            if (hasTaskAlready()) {
                updateState(SAVE_DATA_START, Constants.SC_OK);
                return;
            }
            if (doSaveInfo()) {
                updateState(SAVE_DATA_START, Constants.SC_OK);
                new SaveInfoTask().execute();
            }
        } else {
            updateState(SAVE_DATA_FAIL, Constants.NO_NETWORK);
        }
    }


    private boolean hasTaskAlready() {
        log("hasTask" + mStatus);
        switch (mStatus) {
            //case UP_LOAD_IMG_START:
            case SAVE_DATA_START:
                return true;
            default:
                return false;
        }
    }

    private void getFamilyNum() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置登录请求参数
                JSONObject obj = new JSONObject();
                try {
                    obj.put("devTelNum", mStrPhoneNum);
                } catch (JSONException e) {
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

    private Handler handleGetFamilyNum = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
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

    private String parserFamilyNum(String jsondata) {

        JSONObject JsonObj = null;
        StringBuilder strCommand = new StringBuilder();

        try {
            JsonObj = new JSONObject(jsondata);
            JSONArray jsonarray = JsonObj.getJSONArray("data");
            for (int i = 0; i < jsonarray.length(); i++) {
                String name = jsonarray.getJSONObject(i).getString("famName");
                String num = jsonarray.getJSONObject(i).getString("familyTelNum");
                if(!num.equals(userTelNum)){	               
	                if (name == null || name.equals("") || name.equals("null")) {
	                    name = "关注者" + Integer.toString(i + 1);
	                }
	                if (i > 0) {
	                    strCommand.append("|");
	                }
	                strCommand.append("2," + num + "," + name);
                }
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
		if(message.equals(deviceID)){
			handlerWatchData.removeCallbacksAndMessages(null);
			if (mstrFamilyNumCommand.equals("")) {
	            doAddWatchDevice();
	        } else {
	            instance.editWiteNameList(deviceID, mstrFamilyNumCommand);
	        }
		}
		
	}

}